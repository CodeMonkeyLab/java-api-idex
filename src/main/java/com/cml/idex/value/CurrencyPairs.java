package com.cml.idex.value;

import java.util.List;
import java.util.Map;

public class CurrencyPairs {

   final Map<String, List<String>> pairs;
   final Map<String, Currency>     tokenBySumbol;

   public CurrencyPairs(Map<String, List<String>> pairs, Map<String, Currency> tokenBySumbol) {
      super();
      this.pairs = pairs;
      this.tokenBySumbol = tokenBySumbol;
   }

   /**
    * Returns the Currency Pairs available. For example Map key ETH will have
    * PLU, GNO, ZCC, QNT, CCN, etc.
    * 
    * @return Map
    */
   public Map<String, List<String>> getPairs() {
      return pairs;
   }

   /**
    * All the Currency details by Symbol.
    * 
    * @return Currency by Symbol
    */
   public Map<String, Currency> getTokenBySumbol() {
      return tokenBySumbol;
   }

   @Override
   public String toString() {
      return "CurrencyPairs [pairs=" + pairs + ", tokenBySumbol=" + tokenBySumbol + "]";
   }
}
