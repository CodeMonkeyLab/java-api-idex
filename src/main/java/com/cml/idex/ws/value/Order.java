package com.cml.idex.ws.value;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.cml.idex.util.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class Order {

   final long          id;
   final BigDecimal    amountBuy;
   final BigDecimal    amountSell;
   final String        tokenBuy;
   final String        tokenSell;
   final long          nonce;
   final String        hash;
   final String        user;
   final ZonedDateTime createdAt;
   final ZonedDateTime updatedAt;

   public Order(
         long id, BigDecimal amountBuy, BigDecimal amountSell, String tokenBuy, String tokenSell, long nonce,
         String hash, String user, ZonedDateTime createdAt, ZonedDateTime updatedAt
   ) {
      super();
      this.id = id;
      this.amountBuy = amountBuy;
      this.amountSell = amountSell;
      this.tokenBuy = tokenBuy;
      this.tokenSell = tokenSell;
      this.nonce = nonce;
      this.hash = hash;
      this.user = user;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
   }

   /**
    * Returns the Order ID.
    *
    * @return Order ID
    */
   public long getId() {
      return id;
   }

   public BigDecimal getAmountBuy() {
      return amountBuy;
   }

   public BigDecimal getAmountSell() {
      return amountSell;
   }

   public String getTokenBuy() {
      return tokenBuy;
   }

   public String getTokenSell() {
      return tokenSell;
   }

   public long getNonce() {
      return nonce;
   }

   public String getHash() {
      return hash;
   }

   public String getUser() {
      return user;
   }

   public ZonedDateTime getCreatedAt() {
      return createdAt;
   }

   public ZonedDateTime getUpdatedAt() {
      return updatedAt;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((amountBuy == null) ? 0 : amountBuy.hashCode());
      result = prime * result + ((amountSell == null) ? 0 : amountSell.hashCode());
      result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
      result = prime * result + ((hash == null) ? 0 : hash.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + (int) (nonce ^ (nonce >>> 32));
      result = prime * result + ((tokenBuy == null) ? 0 : tokenBuy.hashCode());
      result = prime * result + ((tokenSell == null) ? 0 : tokenSell.hashCode());
      result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
      result = prime * result + ((user == null) ? 0 : user.hashCode());
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
      Order other = (Order) obj;
      if (amountBuy == null) {
         if (other.amountBuy != null)
            return false;
      } else if (!amountBuy.equals(other.amountBuy))
         return false;
      if (amountSell == null) {
         if (other.amountSell != null)
            return false;
      } else if (!amountSell.equals(other.amountSell))
         return false;
      if (createdAt == null) {
         if (other.createdAt != null)
            return false;
      } else if (!createdAt.equals(other.createdAt))
         return false;
      if (hash == null) {
         if (other.hash != null)
            return false;
      } else if (!hash.equals(other.hash))
         return false;
      if (id != other.id)
         return false;
      if (nonce != other.nonce)
         return false;
      if (tokenBuy == null) {
         if (other.tokenBuy != null)
            return false;
      } else if (!tokenBuy.equals(other.tokenBuy))
         return false;
      if (tokenSell == null) {
         if (other.tokenSell != null)
            return false;
      } else if (!tokenSell.equals(other.tokenSell))
         return false;
      if (updatedAt == null) {
         if (other.updatedAt != null)
            return false;
      } else if (!updatedAt.equals(other.updatedAt))
         return false;
      if (user == null) {
         if (other.user != null)
            return false;
      } else if (!user.equals(other.user))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Order [id=" + id + ", amountBuy=" + amountBuy + ", amountSell=" + amountSell + ", tokenBuy=" + tokenBuy
            + ", tokenSell=" + tokenSell + ", nonce=" + nonce + ", hash=" + hash + ", user=" + user + ", createdAt="
            + createdAt + ", updatedAt=" + updatedAt + "]";
   }

   public static Order parseOrder(final JsonNode node) {
      final long id = node.get("id").asLong();
      final BigDecimal amountBuy = Utils.toBDrequired(node, "amountBuy");
      final BigDecimal amountSell = Utils.toBDrequired(node, "amountSell");
      final String tokenBuy = node.get("tokenBuy").asText();
      final String tokenSell = node.get("tokenSell").asText();
      final long nonce = node.get("nonce").asLong();
      final String hash = node.get("hash").asText();
      final String user = node.get("user").asText();
      final ZonedDateTime createdAt = Utils.parseDateWs(node, "createdAt");
      final ZonedDateTime updatedAt = Utils.parseDateWs(node, "updatedAt");

      return new Order(id, amountBuy, amountSell, tokenBuy, tokenSell, nonce, hash, user, createdAt, updatedAt);

   }
}
