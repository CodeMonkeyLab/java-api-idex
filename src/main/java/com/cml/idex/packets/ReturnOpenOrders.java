package com.cml.idex.packets;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.value.Order;
import com.cml.idex.value.OrderParm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReturnOpenOrders implements Req, Parser<List<Order>> {

   final String  market;
   final String  address;
   final Integer count;
   final Long    cursor;

   private ReturnOpenOrders(String market, String address, Integer count, Long cursor) {
      super();
      this.market = market;
      this.address = address;
      this.count = count;
      this.cursor = cursor;
   }

   @Override
   public String getEndpoint() {
      return "returnOpenOrders";
   }

   @Override
   public String getPayload() {
      StringBuilder sb = new StringBuilder();
      sb.append("{");
      if (market != null)
         sb.append("\"market\":\"").append(market).append("\"");
      if (address != null) {
         if (market != null)
            sb.append(",");
         sb.append("\"address\":\"").append(address).append("\"");
      }
      if (count != null)
         sb.append(",\"count\":").append(count);
      if (cursor != null)
         sb.append("\"cursor\": \"").append(cursor).append("\"");
      return sb.append("}").toString();
   }

   @Override
   public List<Order> parse(ObjectMapper mapper, String body) {
      if (Utils.isEmptyJson(body))
         return Collections.emptyList();
      return fromJson(mapper, body);
   }

   public static ReturnOpenOrders create(String market, String address, Integer count, Long cursor) {
      final String marketFixed = Utils.fixString(market);
      final String adrFixed = Utils.fixString(address);

      if (marketFixed == null && adrFixed == null)
         throw new IllegalArgumentException("market or address is requried!");
      return new ReturnOpenOrders(marketFixed, adrFixed, count, cursor);
   }

   private static List<Order> fromJson(final ObjectMapper mapper, final String body) {
      try {
         final JsonNode root = mapper.readTree(body);

         List<Order> orders = new LinkedList<>();

         Iterator<JsonNode> eleItr = root.elements();
         while (eleItr.hasNext()) {
            orders.add(parseOrder(eleItr.next()));
         }
         return orders;
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }

   public static Order parseOrder(final JsonNode node) {
      if (node == null)
         return null;

      final long timestamp = node.get("timestamp").asLong();
      final String market = node.get("market").asText();
      final long orderNumber = node.get("orderNumber").asLong();
      final String orderHash = node.get("orderHash").asText();
      final BigDecimal price = Utils.toBD(node, "price");
      final BigDecimal amount = Utils.toBD(node, "amount");
      final BigDecimal total = Utils.toBD(node, "total");
      final String type = node.get("type").asText();
      final OrderParm params = parseOrderParams(node.get("params"));
      return new Order(timestamp, market, orderNumber, orderHash, price, amount, total, type, params);
   }

   private static OrderParm parseOrderParams(final JsonNode node) {
      if (node == null)
         return null;

      final String tokenBuy = node.get("tokenBuy").asText();
      final int buyPrecision = node.get("buyPrecision").asInt();
      final long amountBuy = node.get("amountBuy").asLong();
      final String tokenSell = node.get("tokenSell").asText();
      final int sellPrecision = node.get("sellPrecision").asInt();
      final long amountSell = node.get("amountSell").asLong();
      final long expires = node.get("expires").asLong();
      final long nonce = node.get("nonce").asLong();
      final String user = node.get("user").asText();

      return new OrderParm(tokenBuy, buyPrecision, amountBuy, tokenSell, sellPrecision, amountSell, expires, nonce,
            user);
   }
}
