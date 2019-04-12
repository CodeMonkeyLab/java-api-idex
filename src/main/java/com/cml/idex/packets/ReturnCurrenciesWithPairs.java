package com.cml.idex.packets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.value.Currency;
import com.cml.idex.value.CurrencyPairs;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReturnCurrenciesWithPairs implements Req, Parser<CurrencyPairs> {

   private ReturnCurrenciesWithPairs() {
   }

   @Override
   public String getEndpoint() {
      return "returnCurrenciesWithPairs";
   }

   @Override
   public String getPayload() {
      return "";
   }

   public static ReturnCurrenciesWithPairs create() {
      return INSTANCE;
   }

   public static final ReturnCurrenciesWithPairs INSTANCE = new ReturnCurrenciesWithPairs();

   @Override
   public CurrencyPairs parse(ObjectMapper mapper, String json) {
      return fromJson(mapper, json);
   }

   public static CurrencyPairs fromJson(final ObjectMapper mapper, final String body) {
      try {
         JsonNode root = mapper.readTree(body);
         Utils.checkError(root);

         Map<String, List<String>> pairs = new HashMap<>();
         JsonNode node = root.get("pairs");
         if (node != null) {
            Iterator<Entry<String, JsonNode>> pairItr = node.fields();
            while (pairItr.hasNext()) {
               Entry<String, JsonNode> pairNode = pairItr.next();
               List<String> tokens = new LinkedList<>();
               pairNode.getValue().elements().forEachRemaining(tokenNode -> tokens.add(tokenNode.asText()));
               pairs.put(pairNode.getKey(), tokens);
            }
         }

         return new CurrencyPairs(pairs, parseCurrencies(root.get("tokens")));
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }

   public static Map<String, Currency> parseCurrencies(final JsonNode tokensNode) {
      if (tokensNode == null)
         return null;

      final Map<String, Currency> currencies = new HashMap<>();
      Iterator<JsonNode> nodeItr = tokensNode.iterator();
      while (nodeItr.hasNext()) {
         JsonNode node = nodeItr.next();
         currencies.put(node.get("symbol").asText(),
               new Currency(node.get("decimals").asInt(), node.get("address").asText(), node.get("name").asText()));
      }
      return currencies;
   }
}
