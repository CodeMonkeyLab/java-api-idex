package com.cml.idex.packets;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.value.Order;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReturnOrderStatus implements Req, Parser<Order> {

   private final String orderHash;

   private ReturnOrderStatus(String orderHash) {
      super();
      this.orderHash = orderHash;
   }

   @Override
   public String getEndpoint() {
      return "returnOrderStatus";
   }

   @Override
   public String getPayload() {
      return "{\"orderHash\": \"" + orderHash + "\"}";
   }

   @Override
   public Order parse(ObjectMapper mapper, String json) {
      if (Utils.isEmptyJson(json))
         throw new IDexException(ErrorCode.ORDER_NOT_FOUND);
      return fromJson(mapper, json);
   }

   public static ReturnOrderStatus create(String orderHash) {
      final String orderHashFixed = Utils.fixString(orderHash);

      if (orderHashFixed == null)
         throw new IllegalArgumentException("orderHash is requried!");

      return new ReturnOrderStatus(orderHashFixed);
   }

   private static Order fromJson(final ObjectMapper mapper, final String body) {
      try {
         final JsonNode root = mapper.readTree(body);
         Utils.checkError(root);
         return ReturnOpenOrders.parseOrder(root);
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }
}
