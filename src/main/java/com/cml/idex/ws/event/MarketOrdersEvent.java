package com.cml.idex.ws.event;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.cml.idex.ws.Category.Markets;
import com.cml.idex.ws.EventType;
import com.cml.idex.ws.value.Order;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides an aggregated group of new orders which have occurred on the
 * subscribed market.
 *
 * @author codemonkeylab
 *
 */
public class MarketOrdersEvent extends Event<Markets> {

   public static final EventType<Markets> EVENT_TYPE      = EventType.MARKET_ORDERS;
   public static final String             EVENT_TYPE_NAME = "market_orders";

   final String                           market;
   final List<Order>                      orders;

   private MarketOrdersEvent(String chain, long seqID, String eventID, String market, List<Order> orders) {
      super(chain, seqID, eventID);
      this.market = market;
      this.orders = orders;
   }

   /**
    * Market the order(s) are for.
    *
    * @return Market
    */
   public String getMarket() {
      return market;
   }

   /**
    * Returns List or Orders.
    *
    * @return Orders
    */
   public List<Order> getOrders() {
      return orders;
   }

   @Override
   public EventType<Markets> getEventType() {
      return EVENT_TYPE;
   }

   @Override
   public String toString() {
      return "MarketOrdersEvent [market=" + market + ", chain=" + chain + ", seqID=" + seqID + ", eventID=" + eventID
            + "]";
   }

   public static MarketOrdersEvent parse(final ObjectMapper mapper, final JsonNode root) throws IOException {

      final String chain = root.get("chain").asText();
      final String eid = root.get("eid").asText();
      final long seqID = root.get("seq").asLong();

      final JsonNode payload = mapper.readTree(root.get("payload").asText());

      final String market = payload.get("market").asText();

      final List<Order> orders = new LinkedList<>();
      final Iterator<JsonNode> orderItr = payload.get("orders").elements();
      while (orderItr.hasNext())
         orders.add(Order.parseOrder(orderItr.next()));

      return new MarketOrdersEvent(chain, seqID, eid, market, orders);
   }
}
