package com.cml.idex.packets;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReturnBalances implements Req, Parser<Map<String, BigDecimal>> {

   private final String address;

   private ReturnBalances(String address) {
      super();
      this.address = address;
   }

   @Override
   public String getEndpoint() {
      return "returnBalances";
   }

   @Override
   public String getPayload() {
      return "{\"address\": \"" + address + "\"}";
   }

   @Override
   public Map<String, BigDecimal> parse(ObjectMapper mapper, String body) {
      if (Utils.isEmptyJson(body))
         throw new IDexException(ErrorCode.UNKNOWN_ADDRESS, address);
      return fromJson(mapper, body);
   }

   public static ReturnBalances create(final String address) {
      final String fixedAdr = Utils.fixString(address);
      if (fixedAdr == null)
         throw new IllegalArgumentException("address is required!");
      return new ReturnBalances(address);
   }

   private static Map<String, BigDecimal> fromJson(final ObjectMapper mapper, final String body) {
      try {
         final JsonNode root = mapper.readTree(body);
         final Map<String, BigDecimal> map = new HashMap<>();
         final Iterator<Entry<String, JsonNode>> fieldItr = root.fields();

         while (fieldItr.hasNext()) {
            final Entry<String, JsonNode> node = fieldItr.next();
            map.put(node.getKey(), Utils.toBD(node.getValue().asText()));
         }
         return map;
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }
}
