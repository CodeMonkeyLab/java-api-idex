package com.cml.idex.packets;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.value.BalanceOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Returns available balances for an address along with the amount of open
 * orders for each token, indexed by token symbol.
 * 
 * @author plouw
 *
 */
public class ReturnCompleteBalances implements Req, Parser<Map<String, BalanceOrder>> {

   private final String address;

   private ReturnCompleteBalances(String address) {
      super();
      this.address = address;
   }

   @Override
   public String getEndpoint() {
      return "returnCompleteBalances";
   }

   @Override
   public String getPayload() {
      return "{\"address\":\"" + address + "\"}";
   }

   @Override
   public Map<String, BalanceOrder> parse(ObjectMapper mapper, String body) {
      if (Utils.isEmptyJson(body))
         throw new IDexException(ErrorCode.UNKNOWN_ADDRESS, address);
      return fromJson(mapper, body);
   }

   public static ReturnCompleteBalances create(final String address) {
      return new ReturnCompleteBalances(address);
   }

   private static Map<String, BalanceOrder> fromJson(final ObjectMapper mapper, final String body) {
      try {
         final JsonNode root = mapper.readTree(body);

         final Map<String, BalanceOrder> map = new HashMap<>();
         final Iterator<Entry<String, JsonNode>> fieldItr = root.fields();

         while (fieldItr.hasNext()) {
            final Entry<String, JsonNode> node = fieldItr.next();
            map.put(node.getKey(), parseCompleteBalance(node.getValue()));
         }
         return map;
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }

   private static BalanceOrder parseCompleteBalance(final JsonNode node) {
      BigDecimal available = Utils.toBD(node, "available");
      BigDecimal onOrders = Utils.toBD(node, "onOrders");
      return new BalanceOrder(available, onOrders);
   }
}
