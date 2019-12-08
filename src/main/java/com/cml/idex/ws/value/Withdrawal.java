package com.cml.idex.ws.value;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.cml.idex.util.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class Withdrawal {
   final long          id;
   final String        user;
   final BigDecimal    amount;
   final String        token;
   final long          nonce;
   final BigDecimal    fee;
   final BigDecimal    usdValue;
   final ZonedDateTime createdAt;

   private Withdrawal(
         long id, String user, BigDecimal amount, String token, long nonce, BigDecimal fee, BigDecimal usdValue,
         ZonedDateTime createdAt
   ) {
      super();
      this.id = id;
      this.user = user;
      this.amount = amount;
      this.token = token;
      this.nonce = nonce;
      this.fee = fee;
      this.usdValue = usdValue;
      this.createdAt = createdAt;
   }

   public long getId() {
      return id;
   }

   public String getUser() {
      return user;
   }

   public BigDecimal getAmount() {
      return amount;
   }

   public String getToken() {
      return token;
   }

   public long getNonce() {
      return nonce;
   }

   public BigDecimal getFee() {
      return fee;
   }

   public BigDecimal getUsdValue() {
      return usdValue;
   }

   public ZonedDateTime getCreatedAt() {
      return createdAt;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((amount == null) ? 0 : amount.hashCode());
      result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
      result = prime * result + ((fee == null) ? 0 : fee.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + (int) (nonce ^ (nonce >>> 32));
      result = prime * result + ((token == null) ? 0 : token.hashCode());
      result = prime * result + ((usdValue == null) ? 0 : usdValue.hashCode());
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
      Withdrawal other = (Withdrawal) obj;
      if (amount == null) {
         if (other.amount != null)
            return false;
      } else if (!amount.equals(other.amount))
         return false;
      if (createdAt == null) {
         if (other.createdAt != null)
            return false;
      } else if (!createdAt.equals(other.createdAt))
         return false;
      if (fee == null) {
         if (other.fee != null)
            return false;
      } else if (!fee.equals(other.fee))
         return false;
      if (id != other.id)
         return false;
      if (nonce != other.nonce)
         return false;
      if (token == null) {
         if (other.token != null)
            return false;
      } else if (!token.equals(other.token))
         return false;
      if (usdValue == null) {
         if (other.usdValue != null)
            return false;
      } else if (!usdValue.equals(other.usdValue))
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
      return "Withdrawal [id=" + id + ", user=" + user + ", amount=" + amount + ", token=" + token + ", nonce=" + nonce
            + ", fee=" + fee + ", usdValue=" + usdValue + ", createdAt=" + createdAt + "]";
   }

   public static Withdrawal parse(final JsonNode node) {
      final long id = node.get("id").asLong();
      final String user = node.get("user").asText();
      final BigDecimal amount = Utils.toBDrequired(node, "amount");
      final String token = node.get("token").asText();
      final long nonce = node.get("nonce").asLong();
      final BigDecimal fee = Utils.toBDrequired(node, "fee");
      final BigDecimal usdValue = Utils.toBDrequired(node, "usdValue");
      final ZonedDateTime createdAt = Utils.parseDateWs(node, "createdAt");

      return new Withdrawal(id, user, amount, token, nonce, fee, usdValue, createdAt);
   }
}
