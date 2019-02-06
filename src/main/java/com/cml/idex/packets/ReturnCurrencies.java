package com.cml.idex.packets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.value.Currency;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReturnCurrencies implements Req, Parser<Map<String, Currency>> {

   private ReturnCurrencies() {
   }

   @Override
   public String getEndpoint() {
      return "returnCurrencies";
   }

   @Override
   public String getPayload() {
      return "";
   }

   public static ReturnCurrencies create() {
      return INSTANCE;
   }

   public static ReturnCurrencies INSTANCE = new ReturnCurrencies();

   @Override
   public Map<String, Currency> parse(ObjectMapper mapper, String body) {
      return fromJson(mapper, body);
   }

   public static Map<String, Currency> fromJson(final ObjectMapper mapper, final String body) {
      try {
         JsonNode root = mapper.readTree(body);
         Utils.checkError(root);
         final Map<String, Currency> map = new HashMap<>();
         final Iterator<Entry<String, JsonNode>> fieldItr = root.fields();
         while (fieldItr.hasNext()) {
            final Entry<String, JsonNode> entry = fieldItr.next();
            final JsonNode node = entry.getValue();
            map.put(entry.getKey(),
                  new Currency(node.get("decimals").asInt(), node.get("address").asText(), node.get("name").asText()));
         }

         return map;
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }
}
