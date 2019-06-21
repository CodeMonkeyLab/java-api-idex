package com.cml.idex.ws.value;

import java.time.ZonedDateTime;

import com.cml.idex.util.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class CancelMarket extends Cancel {

   final String market;

   public CancelMarket(long id, String orderHash, ZonedDateTime createdAt, String market) {
      super(id, orderHash, createdAt);
      this.market = market;
   }

   /**
    * Market the Cancel is for.
    * 
    * @return Market
    */
   public String getMarket() {
      return market;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((market == null) ? 0 : market.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      CancelMarket other = (CancelMarket) obj;
      if (market == null) {
         if (other.market != null)
            return false;
      } else if (!market.equals(other.market))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "CancelMarket [market=" + market + ", id=" + id + ", orderHash=" + orderHash + ", createdAt=" + createdAt
            + "]";
   }

   public static CancelMarket parseOrder(final JsonNode node) {
      final long id = node.get("id").asLong();
      final String market = node.get("market").asText();
      final String orderHash = node.get("orderHash").asText();
      final ZonedDateTime createdAt = Utils.parseDateWs(node, "createdAt");

      return new CancelMarket(id, orderHash, createdAt, market);

   }
}
