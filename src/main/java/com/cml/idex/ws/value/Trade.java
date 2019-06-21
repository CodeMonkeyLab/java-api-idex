package com.cml.idex.ws.value;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.cml.idex.util.Utils;
import com.fasterxml.jackson.databind.JsonNode;

public class Trade {

   final long          id;
   final String        type;
   final ZonedDateTime date;
   final long          timestamp;
   final String        market;
   final BigDecimal    usdValue;
   final BigDecimal    price;
   final BigDecimal    amount;
   final BigDecimal    total;
   final String        taker;
   final String        maker;
   final String        orderHash;
   final BigDecimal    gasFee;
   final String        tokenBuy;
   final String        tokenSell;
   final BigDecimal    buyerFee;
   final BigDecimal    sellerFee;
   final long          amountWei;
   final ZonedDateTime updatedAt;

   public Trade(
         long id, String type, ZonedDateTime date, long timestamp, String market, BigDecimal usdValue, BigDecimal price,
         BigDecimal amount, BigDecimal total, String taker, String maker, String orderHash, BigDecimal gasFee,
         String tokenBuy, String tokenSell, BigDecimal buyerFee, BigDecimal sellerFee, long amountWei,
         ZonedDateTime updatedAt
   ) {
      super();
      this.id = id;
      this.type = type;
      this.date = date;
      this.timestamp = timestamp;
      this.market = market;
      this.usdValue = usdValue;
      this.price = price;
      this.amount = amount;
      this.total = total;
      this.taker = taker;
      this.maker = maker;
      this.orderHash = orderHash;
      this.gasFee = gasFee;
      this.tokenBuy = tokenBuy;
      this.tokenSell = tokenSell;
      this.buyerFee = buyerFee;
      this.sellerFee = sellerFee;
      this.amountWei = amountWei;
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

   public String getType() {
      return type;
   }

   public ZonedDateTime getDate() {
      return date;
   }

   /**
    * Unix epoch time. To convert to TimeStamp remember to multiply with 1000.
    *
    * @return Long Timestamp
    */
   public long getTimestamp() {
      return timestamp;
   }

   public String getMarket() {
      return market;
   }

   public BigDecimal getUsdValue() {
      return usdValue;
   }

   public BigDecimal getPrice() {
      return price;
   }

   public BigDecimal getAmount() {
      return amount;
   }

   public BigDecimal getTotal() {
      return total;
   }

   public String getTaker() {
      return taker;
   }

   public String getMaker() {
      return maker;
   }

   public String getOrderHash() {
      return orderHash;
   }

   public BigDecimal getGasFee() {
      return gasFee;
   }

   public String getTokenBuy() {
      return tokenBuy;
   }

   public String getTokenSell() {
      return tokenSell;
   }

   public BigDecimal getBuyerFee() {
      return buyerFee;
   }

   public BigDecimal getSellerFee() {
      return sellerFee;
   }

   public long getAmountWei() {
      return amountWei;
   }

   public ZonedDateTime getUpdatedAt() {
      return updatedAt;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((amount == null) ? 0 : amount.hashCode());
      result = prime * result + (int) (amountWei ^ (amountWei >>> 32));
      result = prime * result + ((buyerFee == null) ? 0 : buyerFee.hashCode());
      result = prime * result + ((date == null) ? 0 : date.hashCode());
      result = prime * result + ((gasFee == null) ? 0 : gasFee.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((maker == null) ? 0 : maker.hashCode());
      result = prime * result + ((market == null) ? 0 : market.hashCode());
      result = prime * result + ((orderHash == null) ? 0 : orderHash.hashCode());
      result = prime * result + ((price == null) ? 0 : price.hashCode());
      result = prime * result + ((sellerFee == null) ? 0 : sellerFee.hashCode());
      result = prime * result + ((taker == null) ? 0 : taker.hashCode());
      result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
      result = prime * result + ((tokenBuy == null) ? 0 : tokenBuy.hashCode());
      result = prime * result + ((tokenSell == null) ? 0 : tokenSell.hashCode());
      result = prime * result + ((total == null) ? 0 : total.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
      result = prime * result + ((usdValue == null) ? 0 : usdValue.hashCode());
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
      Trade other = (Trade) obj;
      if (amount == null) {
         if (other.amount != null)
            return false;
      } else if (!amount.equals(other.amount))
         return false;
      if (amountWei != other.amountWei)
         return false;
      if (buyerFee == null) {
         if (other.buyerFee != null)
            return false;
      } else if (!buyerFee.equals(other.buyerFee))
         return false;
      if (date == null) {
         if (other.date != null)
            return false;
      } else if (!date.equals(other.date))
         return false;
      if (gasFee == null) {
         if (other.gasFee != null)
            return false;
      } else if (!gasFee.equals(other.gasFee))
         return false;
      if (id != other.id)
         return false;
      if (maker == null) {
         if (other.maker != null)
            return false;
      } else if (!maker.equals(other.maker))
         return false;
      if (market == null) {
         if (other.market != null)
            return false;
      } else if (!market.equals(other.market))
         return false;
      if (orderHash == null) {
         if (other.orderHash != null)
            return false;
      } else if (!orderHash.equals(other.orderHash))
         return false;
      if (price == null) {
         if (other.price != null)
            return false;
      } else if (!price.equals(other.price))
         return false;
      if (sellerFee == null) {
         if (other.sellerFee != null)
            return false;
      } else if (!sellerFee.equals(other.sellerFee))
         return false;
      if (taker == null) {
         if (other.taker != null)
            return false;
      } else if (!taker.equals(other.taker))
         return false;
      if (timestamp != other.timestamp)
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
      if (total == null) {
         if (other.total != null)
            return false;
      } else if (!total.equals(other.total))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      if (updatedAt == null) {
         if (other.updatedAt != null)
            return false;
      } else if (!updatedAt.equals(other.updatedAt))
         return false;
      if (usdValue == null) {
         if (other.usdValue != null)
            return false;
      } else if (!usdValue.equals(other.usdValue))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Trade [id=" + id + ", type=" + type + ", date=" + date + ", timestamp=" + timestamp + ", market=" + market
            + ", usdValue=" + usdValue + ", price=" + price + ", amount=" + amount + ", total=" + total + ", taker="
            + taker + ", maker=" + maker + ", orderHash=" + orderHash + ", gasFee=" + gasFee + ", tokenBuy=" + tokenBuy
            + ", tokenSell=" + tokenSell + ", buyerFee=" + buyerFee + ", sellerFee=" + sellerFee + ", amountWei="
            + amountWei + ", updatedAt=" + updatedAt + "]";
   }

   public static Trade parseOrder(final JsonNode node) {
      final long id = node.get("id").asLong();
      final String type = node.get("type").asText();
      final ZonedDateTime date = Utils.parseDateWs(node, "date");
      final long timestamp = node.get("timestamp").asLong();
      final String market = node.get("market").asText();
      final BigDecimal usdValue = Utils.toBDrequired(node, "usdValue");
      final BigDecimal price = Utils.toBDrequired(node, "price");
      final BigDecimal amount = Utils.toBDrequired(node, "amount");
      final BigDecimal total = Utils.toBDrequired(node, "total");
      final String taker = node.get("taker").asText();
      final String maker = node.get("maker").asText();
      final String orderHash = node.get("orderHash").asText();
      final BigDecimal gasFee = Utils.toBDrequired(node, "gasFee");
      final String tokenBuy = node.get("tokenBuy").asText();
      final String tokenSell = node.get("tokenSell").asText();
      final BigDecimal buyerFee = Utils.toBDrequired(node, "buyerFee");
      final BigDecimal sellerFee = Utils.toBDrequired(node, "sellerFee");
      final long amountWei = node.get("amountWei").asLong();
      final ZonedDateTime updatedAt = Utils.parseDateWs(node, "updatedAt");

      return new Trade(id, type, date, timestamp, market, usdValue, price, amount, total, taker, maker, orderHash,
            gasFee, tokenBuy, tokenSell, buyerFee, sellerFee, amountWei, updatedAt);

   }
}
