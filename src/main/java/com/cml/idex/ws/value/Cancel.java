package com.cml.idex.ws.value;

import java.time.ZonedDateTime;

import com.cml.idex.util.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class Cancel {

   final long          id;
   final String        orderHash;
   final ZonedDateTime createdAt;

   public Cancel(long id, String orderHash, ZonedDateTime createdAt) {
      super();
      this.id = id;
      this.orderHash = orderHash;
      this.createdAt = createdAt;
   }

   public long getId() {
      return id;
   }

   public String getOrderHash() {
      return orderHash;
   }

   public ZonedDateTime getCreatedAt() {
      return createdAt;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((orderHash == null) ? 0 : orderHash.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Cancel other = (Cancel) obj;
      if (createdAt == null) {
         if (other.createdAt != null)
            return false;
      } else if (!createdAt.equals(other.createdAt))
         return false;
      if (id != other.id)
         return false;
      if (orderHash == null) {
         if (other.orderHash != null)
            return false;
      } else if (!orderHash.equals(other.orderHash))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Cancel [id=" + id + ", orderHash=" + orderHash + ", createdAt=" + createdAt + "]";
   }

   public static Cancel parseOrder(final JsonNode node) {
      final long id = node.get("id").asLong();
      final String orderHash = node.get("orderHash").asText();
      final ZonedDateTime createdAt = Utils.parseDateWs(node, "createdAt");

      return new Cancel(id, orderHash, createdAt);

   }

}
