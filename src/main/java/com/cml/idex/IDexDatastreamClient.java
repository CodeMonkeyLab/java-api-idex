package com.cml.idex;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.netty.ws.NettyWebSocket;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cml.idex.util.RandomString;
import com.cml.idex.util.Utils;
import com.cml.idex.ws.Category;
import com.cml.idex.ws.EventType;
import com.cml.idex.ws.event.AccountCancelsEvent;
import com.cml.idex.ws.event.AccountOrdersEvent;
import com.cml.idex.ws.event.AccountTradesEvent;
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

   private static final int          MAX_SUB_CHAINS     = 1;
   private static final int          MAX_SUB_ACC        = 25;
   private static final int          MAX_SUB_MARKET     = 100;

   private final static RandomString ridGen             = new RandomString(25);

   private static final Logger       log                = LoggerFactory.getLogger(IDexDatastreamClient.class);

   final NettyWebSocket              wsClient;
   final HandshakeListener           listner;
   final ObjectMapper                mapper             = new ObjectMapper();

   public static void main(String[] args) throws InterruptedException, ExecutionException {

      CompletableFuture<IDexDatastreamClient> clientF = create(Dsl.asyncHttpClient());

      clientF.thenAccept(client -> {
         client.subscribe(Category.SUBSCRIBE_TO_MARKETS, Set.of("ETH_QNT", "ETH_LIT"),
               Set.of(EventType.MARKET_ORDERS, EventType.MARKET_CANCELS, EventType.MARKET_TRADES));
      });

      while (true)
         Thread.sleep(1000L);
   }

   public static CompletableFuture<IDexDatastreamClient> create(AsyncHttpClient client)
         throws InterruptedException, ExecutionException {
      final IDexDatastreamClient wsClient = new IDexDatastreamClient(client);
      return wsClient.listner.authedSid.thenApply(val -> wsClient);
   }

   private IDexDatastreamClient(AsyncHttpClient client) throws InterruptedException, ExecutionException {
      this.listner = new HandshakeListener(mapper);
      this.wsClient = client.prepareGet(WEBSOCKET_ENDPOINT)
            .execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(listner).build()).get();
   }

   public <T extends Category> CompletableFuture<Response<T>> subscribe(
         T category, Set<String> topics, Set<EventType<T>> events
   ) {
      Request<T> req = new Request<>(getSid(), ridGen.nextString(), category,
            new ActionPayload<>(Action.subscribe, topics, events));

      CompletableFuture<Response<T>> future = listner.rspTracker.registerRid(req.getRid());
      Utils.prettyPrint(mapper, req.toJson());
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

      final ObjectMapper              mapper;
      final CompletableFuture<String> authedSid  = new CompletableFuture<>();
      final ResponseTracker           rspTracker = new ResponseTracker();

      public HandshakeListener(ObjectMapper mapper) {
         super();
         this.mapper = mapper;
      }

      @Override
      public void onOpen(WebSocket websocket) {
         websocket.sendTextFrame(
               "{\"request\": \"handshake\", \"payload\" : \"{ \\\"version\\\": \\\"1.0.0\\\", \\\"key\\\": \\\"17paIsICur8sA0OBqG6dH5G1rmrHNMwt4oNk4iX9\\\"}\"}");
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

               processEvent(node.asText(), root, mapper);

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

   private static void processEvent(final String eventType, JsonNode root, ObjectMapper mapper) {
      try {
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

}
