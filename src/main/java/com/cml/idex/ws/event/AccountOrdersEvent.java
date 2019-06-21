package com.cml.idex.ws.event;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.cml.idex.ws.Category.Accounts;
import com.cml.idex.ws.EventType;
import com.cml.idex.ws.value.Order;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * When the subscribed account has new orders received and processed by the
 * exchange, this event will be provided including each of the orders that were
 * processed in the given batch.
 *
 * At this point the orders should be considered pending.
 *
 * @author codemonkeylab
 *
 */
public class AccountOrdersEvent extends Event<Accounts> {

   public static final EventType<Accounts> EVENT_TYPE      = EventType.ACCOUNT_ORDERS;
   public static final String              EVENT_TYPE_NAME = "account_orders";

   final String                            account;
   final List<Order>                       orders;

   public AccountOrdersEvent(String chain, long seqID, String eventID, String account, List<Order> orders) {
      super(chain, seqID, eventID);
      this.account = account;
      this.orders = orders;
   }

   /**
    * The account address the order(s) are for.
    *
    * @return Market
    */
   public String getAccount() {
      return account;
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
   public EventType<Accounts> getEventType() {
      return EVENT_TYPE;
   }

   @Override
   public String toString() {
      return "MarketOrdersEvent [account=" + account + ", chain=" + chain + ", seqID=" + seqID + ", eventID=" + eventID
            + "]";
   }

   public static AccountOrdersEvent parse(final ObjectMapper mapper, final JsonNode root) throws IOException {

      final String chain = root.get("chain").asText();
      final String eid = root.get("eid").asText();
      final long seqID = root.get("seq").asLong();

      final JsonNode payload = mapper.readTree(root.get("payload").asText());

      final String account = payload.get("account").asText();

      final List<Order> orders = new LinkedList<>();
      final Iterator<JsonNode> orderItr = payload.get("orders").elements();
      while (orderItr.hasNext())
         orders.add(Order.parseOrder(orderItr.next()));

      return new AccountOrdersEvent(chain, seqID, eid, account, orders);
   }

}
