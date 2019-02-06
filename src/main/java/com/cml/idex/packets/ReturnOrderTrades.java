package com.cml.idex.packets;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.value.OrderTrade;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReturnOrderTrades implements Req, Parser<List<OrderTrade>> {

   private final String orderHash;

   private ReturnOrderTrades(String orderHash) {
      super();
      this.orderHash = orderHash;
   }

   @Override
   public String getEndpoint() {
      return "returnOrderTrades";
   }

   @Override
   public String getPayload() {
      return "{\"orderHash\": \"" + orderHash + "\"}";
   }

   @Override
   public List<OrderTrade> parse(ObjectMapper mapper, String json) {
      if (Utils.isEmptyJson(json))
         return Collections.emptyList();
      return fromJson(mapper, json);
   }

   public static ReturnOrderTrades create(String orderHash) {
      final String orderHashFixed = Utils.fixString(orderHash);

      if (orderHashFixed == null)
         throw new IllegalArgumentException("orderHash is requried!");

      return new ReturnOrderTrades(orderHashFixed);
   }

   private static List<OrderTrade> fromJson(final ObjectMapper mapper, final String body) {
      try {
         final JsonNode root = mapper.readTree(body);
         Utils.checkError(root);

         List<OrderTrade> orderTrades = new LinkedList<>();

         Iterator<JsonNode> eleItr = root.elements();
         while (eleItr.hasNext()) {
            OrderTrade orderTrade = parseOrderTrade(eleItr.next());
            if (orderTrade != null)
               orderTrades.add(orderTrade);
         }
         return orderTrades;
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }

   public static OrderTrade parseOrderTrade(final JsonNode node) {
      if (node == null)
         return null;

      BigDecimal amount = Utils.toBD("amount");
      String type = node.get("type").asText();
      BigDecimal total = Utils.toBD("total");
      BigDecimal price = Utils.toBD("price");
      String uuid = node.get("uuid").asText();
      String transactionHash = node.get("transactionHash").asText();

      return new OrderTrade(amount, type, total, price, uuid, transactionHash);
   }
}
