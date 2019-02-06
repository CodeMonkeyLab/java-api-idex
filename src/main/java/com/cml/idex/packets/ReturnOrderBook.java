package com.cml.idex.packets;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.value.Order;
import com.cml.idex.value.OrderBook;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReturnOrderBook implements Req, Parser<OrderBook> {

   final String  market;
   final Integer count;

   private ReturnOrderBook(String market, Integer count) {
      super();
      this.market = market;
      this.count = count;
   }

   @Override
   public String getEndpoint() {
      return "returnOrderBook";
   }

   @Override
   public String getPayload() {
      final StringBuilder sb = new StringBuilder();
      sb.append("{\"market\":\"").append(market).append("\"");
      if (count != null)
         sb.append(",\"count\":").append(count);
      return sb.append("}").toString();
   }

   @Override
   public OrderBook parse(ObjectMapper mapper, String json) {
      if (Utils.isEmptyJson(json))
         return new OrderBook(Collections.emptyList(), Collections.emptyList());
      return fromJson(mapper, json);
   }

   public static ReturnOrderBook create(String market, Integer count) {
      final String marketFixed = Utils.fixString(market);

      if (marketFixed == null)
         throw new IllegalArgumentException("market is requried!");

      if (count != null)
         if (count < 1 || count > 100)
            throw new IllegalArgumentException("count must be between 1 and 100 OR null value");

      return new ReturnOrderBook(marketFixed, count);
   }

   public static OrderBook fromJson(final ObjectMapper mapper, final String body) {
      try {
         final JsonNode root = mapper.readTree(body);
         Utils.checkError(root);

         final List<Order> bids = new LinkedList<>();
         final List<Order> asks = new LinkedList<>();

         JsonNode node = root.get("bids");
         if (node != null) {
            Iterator<JsonNode> eleItr = node.elements();
            while (eleItr.hasNext())
               bids.add(ReturnOpenOrders.parseOrder(eleItr.next()));
         }
         node = root.get("asks");
         if (node != null) {
            Iterator<JsonNode> eleItr = node.elements();
            while (eleItr.hasNext())
               asks.add(ReturnOpenOrders.parseOrder(eleItr.next()));
         }

         return new OrderBook(bids, asks);
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }
}
