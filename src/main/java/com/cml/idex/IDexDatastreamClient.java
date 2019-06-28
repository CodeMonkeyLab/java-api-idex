package com.cml.idex;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.netty.ws.NettyWebSocket;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cml.idex.util.RandomString;
import com.cml.idex.ws.Category;
import com.cml.idex.ws.EventListener;
import com.cml.idex.ws.EventType;
import com.cml.idex.ws.event.AccountCancelsEvent;
import com.cml.idex.ws.event.AccountDepositCompleteEvent;
import com.cml.idex.ws.event.AccountNonceEvent;
import com.cml.idex.ws.event.AccountOrdersEvent;
import com.cml.idex.ws.event.AccountTradesEvent;
import com.cml.idex.ws.event.Event;
import com.cml.idex.ws.event.MarketCancelsEvent;
import com.cml.idex.ws.event.MarketListingEvent;
import com.cml.idex.ws.event.MarketOrdersEvent;
import com.cml.idex.ws.event.MarketTradesEvent;
import com.cml.idex.ws.packets.ActionPayload;
import com.cml.idex.ws.packets.ActionPayload.Action;
import com.cml.idex.ws.packets.Request;
import com.cml.idex.ws.packets.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IDexDatastreamClient {

   private static final String       WEBSOCKET_ENDPOINT = "wss://datastream.idex.market";
   private static final String       API_KEY            = "17paIsICur8sA0OBqG6dH5G1rmrHNMwt4oNk4iX9";

   private static final int          MAX_SUB_CHAINS     = 1;
   private static final int          MAX_SUB_ACC        = 25;
   private static final int          MAX_SUB_MARKET     = 100;

   private final static RandomString ridGen             = new RandomString(25);

   private static final Logger       log                = LoggerFactory.getLogger(IDexDatastreamClient.class);

   final String                      apiKey;
   final NettyWebSocket              wsClient;
   final HandshakeListener           listner;
   final ObjectMapper                mapper             = new ObjectMapper();

   final Set<String>                 chainTopics        = new HashSet<>();
   final Set<String>                 accountTopics      = new HashSet<>();
   final Set<String>                 marketTopics       = new HashSet<>();

   public static void main(String[] args) throws InterruptedException, ExecutionException {

      CompletableFuture<IDexDatastreamClient> clientF = create(Dsl.asyncHttpClient());
      IDexDatastreamClient clientWs = clientF.join();

      // clientWs.addEventListner(MarketOrdersEvent.class, event -> {
      // System.out.println(event);
      // event.getOrders().forEach(System.out::println);
      // });
      //
      // clientWs.subscribe(Category.SUBSCRIBE_TO_MARKETS, Set.of("ETH_QNT",
      // "ETH_LIT"),
      // Set.of(EventType.MARKET_ORDERS, EventType.MARKET_CANCELS,
      // EventType.MARKET_TRADES));

      clientWs.subscribe(Category.SUBSCRIBE_TO_ACCOUNTS, Set.of("0x529ba941d82cfbbf61d63bd1f38aec1c90788fc8"),
            EventType.ACCOUNT_NONCE);

      while (true)
         Thread.sleep(1000L);
   }

   /**
    * Creates a IDEXDatastreamClient with default Endpoint URL and API Key.
    *
    * @param client
    * @return
    * @throws InterruptedException
    * @throws ExecutionException
    */
   public static CompletableFuture<IDexDatastreamClient> create(final AsyncHttpClient client)
         throws InterruptedException, ExecutionException {
      return create(client, null, null);
   }

   public static CompletableFuture<IDexDatastreamClient> create(
         final AsyncHttpClient client, final String endpoint, final String apiKey
   ) throws InterruptedException, ExecutionException {
      final IDexDatastreamClient wsClient = new IDexDatastreamClient(client, endpoint, apiKey);
      return wsClient.listner.authedSid.thenApply(val -> wsClient);
   }

   private IDexDatastreamClient(AsyncHttpClient client, final String endpoint, final String apiKey)
         throws InterruptedException, ExecutionException {
      this.apiKey = apiKey == null ? API_KEY : apiKey;
      this.listner = new HandshakeListener(mapper, this.apiKey);
      this.wsClient = client.prepareGet(endpoint == null ? WEBSOCKET_ENDPOINT : endpoint)
            .execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(listner).build()).get();
   }

   @SuppressWarnings("rawtypes")
   public <T extends Event> void addEventListner(Class<T> eventClass, EventListener<T> listener) {
      listner.eventListners.add(new EventListnerWrapper(listener, event -> eventClass == event.getClass()));
   }

   public <T extends Category> void addEventListner(T category, EventListener<Event<T>> listener) {
      listner.eventListners
            .add(new EventListnerWrapper(listener, event -> event.getEventType().getCategoryType() == category));
   }

   public void addEventListner(@SuppressWarnings("rawtypes") EventListener<Event> listener) {
      listner.eventListners.add(new EventListnerWrapper(listener, event -> true));
   }

   @SuppressWarnings("rawtypes")
   public void removeEventListner(EventListener listener) {
      listner.eventListners.removeAll(
            listner.eventListners.stream().filter(list -> list.listener == listener).collect(Collectors.toList()));
   }

   @SuppressWarnings("rawtypes")
   private class EventListnerWrapper {

      final EventListener    listener;
      final Predicate<Event> accepts;

      public EventListnerWrapper(EventListener listener, Predicate<Event> accepts) {
         super();
         this.listener = listener;
         this.accepts = accepts;
      }
   }

   public <T extends Category> CompletableFuture<Response<T>> subscribe(
         T category, Set<String> topics, EventType<T>... events
   ) {
      Objects.requireNonNull(events, "Events is required!");
      return subscribe(category, topics, Set.of(events));
   }

   public <T extends Category> CompletableFuture<Response<T>> subscribe(
         T category, Set<String> topics, Set<EventType<T>> events
   ) {
      Objects.requireNonNull(category, "Category is required!");
      Objects.requireNonNull(events, "Events is required!");
      Request<T> req = new Request<>(getSid(), ridGen.nextString(), category,
            new ActionPayload<>(Action.subscribe, topics, events));

      CompletableFuture<Response<T>> future = listner.rspTracker.registerRid(req.getRid());
      wsClient.sendTextFrame(req.toJson());
      return future;
   }

   /**
    * Returns the current Session ID.
    *
    * @return Session ID
    */
   public String getSid() {
      try {
         return listner.authedSid.get();
      } catch (InterruptedException | ExecutionException e) {
         throw new RuntimeException(e);
      }
   }

   private final static class HandshakeListener implements WebSocketListener {

      final String                    apiKey;
      final ObjectMapper              mapper;
      final CompletableFuture<String> authedSid     = new CompletableFuture<>();
      final ResponseTracker           rspTracker    = new ResponseTracker();
      final List<EventListnerWrapper> eventListners = new CopyOnWriteArrayList<>();

      public HandshakeListener(final ObjectMapper mapper, final String apiKey) {
         super();
         this.apiKey = apiKey;
         this.mapper = mapper;
      }

      @Override
      public void onOpen(WebSocket websocket) {
         websocket.sendTextFrame(
               "{\"request\": \"handshake\", \"payload\" : \"{ \\\"version\\\": \\\"1.0.0\\\", \\\"key\\\": \\\""
                     + apiKey + "\\\"}\"}");
      }

      @Override
      public void onError(Throwable t) {
         if (!authedSid.isDone()) {
            // TODO Handle this
            authedSid.completeExceptionally(new IllegalStateException("Authentication Failed!"));
         }
         t.printStackTrace();
      }

      @Override
      public void onTextFrame(String payload, boolean finalFragment, int rsv) {
         System.out.println(payload + ", FinalFragment=" + finalFragment + ", Rsv=" + rsv);

         try {
            JsonNode root = mapper.readTree(payload);

            JsonNode node = root.get("event");
            if (node != null) {
               // Event Received
               System.out.println("Event Received!");

               processEvent(node.asText(), root, mapper, eventListners);

               return;
            }
            node = root.get("request");
            if (node != null) {
               // Request Received
               final String reqType = node.asText();

               if ("handshake".equalsIgnoreCase(reqType)) {
                  // Handshake Rsp
                  node = root.get("sid");
                  if (node != null) {
                     // TODO : Nuke!
                     final String sid = node.asText();

                     System.out.println("SID : " + sid);
                     authedSid.complete(sid);
                  }
               } else {
                  final Response<Category> rsp = Response.fromJson(mapper, root);
                  final CompletableFuture<Response<Category>> future = rspTracker
                        .getAndRemoveFutureForRid(rsp.getRid());
                  if (future != null)
                     try {
                        future.complete(rsp);
                     } catch (Throwable e) {
                        log.error(e.getLocalizedMessage(), e);
                     }
               }

               return;
            }
            System.out.println("Unknown Frame received!!!!");
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      @Override
      public void onClose(WebSocket websocket, int code, String reason) {
         // TODO Auto-generated method stub
         System.out.println("onClose: code=" + code + ", reason=" + reason);
         if (!authedSid.isDone()) {
            // TODO Handle this
            authedSid.completeExceptionally(new IllegalStateException("Authentication Failed!"));
         }
      }
   };

   @SuppressWarnings("unchecked")
   private static void processEvent(
         final String eventType, JsonNode root, ObjectMapper mapper, final List<EventListnerWrapper> eventListners
   ) {
      try {
         BiFunction<ObjectMapper, JsonNode, Event<?>> eventProcessor = EVENT_PROCESSOR.get(eventType);
         if (eventProcessor == null) {
            log.error("Unhandeled Event!! " + eventType);
            return;
         }

         {
            Event<?> event = eventProcessor.apply(mapper, root);
            for (EventListnerWrapper list : eventListners) {
               if (list.accepts.test(event)) {
                  try {
                     list.listener.onEvent(event);
                  } catch (Throwable e) {
                     log.error(e.getLocalizedMessage(), e);
                  }
               }
            }
            if (!eventListners.isEmpty())
               return;
         }

         switch (eventType) {
            // Markets
            case MarketOrdersEvent.EVENT_TYPE_NAME: {
               MarketOrdersEvent event = MarketOrdersEvent.parse(mapper, root);
               System.out.println(event);
               event.getOrders().forEach(System.out::println);
            }
               break;
            case MarketCancelsEvent.EVENT_TYPE_NAME: {
               MarketCancelsEvent event = MarketCancelsEvent.parse(mapper, root);
               System.out.println(event);
               event.getCancels().forEach(System.out::println);
            }
               break;
            case MarketTradesEvent.EVENT_TYPE_NAME: {
               MarketTradesEvent event = MarketTradesEvent.parse(mapper, root);
               System.out.println(event);
               event.getTrades().forEach(System.out::println);
            }
               break;
            case MarketListingEvent.EVENT_TYPE_NAME: {
               MarketListingEvent event = MarketListingEvent.parse(mapper, root);
               System.out.println(event);
            }
            // Account
            case AccountOrdersEvent.EVENT_TYPE_NAME: {
               AccountOrdersEvent event = AccountOrdersEvent.parse(mapper, root);
               System.out.println(event);
               event.getOrders().forEach(System.out::println);
            }
               break;
            case AccountCancelsEvent.EVENT_TYPE_NAME: {
               AccountCancelsEvent event = AccountCancelsEvent.parse(mapper, root);
               System.out.println(event);
               event.getCancels().forEach(System.out::println);
            }
               break;
            case AccountTradesEvent.EVENT_TYPE_NAME: {
               AccountTradesEvent event = AccountTradesEvent.parse(mapper, root);
               System.out.println(event);
               event.getTrades().forEach(System.out::println);
            }
               break;

         }
      } catch (Throwable e) {
         // TODO Parse failure!
         log.error(e.getLocalizedMessage(), e);
      }
   }

   private static final Map<String, BiFunction<ObjectMapper, JsonNode, Event<?>>> EVENT_PROCESSOR;

   static {
      EVENT_PROCESSOR = new HashMap<>();
      // Market
      EVENT_PROCESSOR.put(MarketOrdersEvent.EVENT_TYPE_NAME, MarketOrdersEvent::parse);
      EVENT_PROCESSOR.put(MarketCancelsEvent.EVENT_TYPE_NAME, MarketCancelsEvent::parse);
      EVENT_PROCESSOR.put(MarketTradesEvent.EVENT_TYPE_NAME, MarketTradesEvent::parse);
      EVENT_PROCESSOR.put(MarketListingEvent.EVENT_TYPE_NAME, MarketListingEvent::parse);
      // Account
      EVENT_PROCESSOR.put(AccountNonceEvent.EVENT_TYPE_NAME, AccountNonceEvent::parse);
      EVENT_PROCESSOR.put(AccountDepositCompleteEvent.EVENT_TYPE_NAME, AccountDepositCompleteEvent::parse);
      EVENT_PROCESSOR.put(AccountOrdersEvent.EVENT_TYPE_NAME, AccountOrdersEvent::parse);
      EVENT_PROCESSOR.put(AccountCancelsEvent.EVENT_TYPE_NAME, AccountCancelsEvent::parse);
      EVENT_PROCESSOR.put(AccountTradesEvent.EVENT_TYPE_NAME, AccountTradesEvent::parse);
   }

}
