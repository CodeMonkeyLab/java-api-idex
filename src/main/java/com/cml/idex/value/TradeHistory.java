package com.cml.idex.value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TradeHistory {

   final LocalDateTime date;
   final BigDecimal    amount;
   final String        type;
   final BigDecimal    total;
   final BigDecimal    price;
   final String        orderHash;
   final String        uuid;
   final Long          tid;
   final BigDecimal    buyerFee;
   final BigDecimal    sellerFee;
   final BigDecimal    gasFee;
   final Long          timestamp;
   final String        maker;
   final String        taker;
   final String        tokenBuy;
   final String        tokenSell;
   final String        transactionHash;
   final String        usdValue;

   public TradeHistory(
         LocalDateTime date, BigDecimal amount, String type, BigDecimal total, BigDecimal price, String orderHash,
         String uuid, Long tid, BigDecimal buyerFee, BigDecimal sellerFee, BigDecimal gasFee, Long timestamp,
         String maker, String taker, String tokenBuy, String tokenSell, String transactionHash, String usdValue
   ) {
      super();
      this.date = date;
      this.amount = amount;
      this.type = type;
      this.total = total;
      this.price = price;
      this.orderHash = orderHash;
      this.uuid = uuid;
      this.tid = tid;
      this.buyerFee = buyerFee;
      this.sellerFee = sellerFee;
      this.gasFee = gasFee;
      this.timestamp = timestamp;
      this.maker = maker;
      this.taker = taker;
      this.tokenBuy = tokenBuy;
      this.tokenSell = tokenSell;
      this.transactionHash = transactionHash;
      this.usdValue = usdValue;
   }

   public LocalDateTime getDate() {
      return date;
   }

   public BigDecimal getAmount() {
      return amount;
   }

   public String getType() {
      return type;
   }

   public BigDecimal getTotal() {
      return total;
   }

   public BigDecimal getPrice() {
      return price;
   }

   public String getOrderHash() {
      return orderHash;
   }

   public String getUuid() {
      return uuid;
   }

   public Long getTid() {
      return tid;
   }

   public BigDecimal getBuyerFee() {
      return buyerFee;
   }

   public BigDecimal getSellerFee() {
      return sellerFee;
   }

   public BigDecimal getGasFee() {
      return gasFee;
   }

   public Long getTimestamp() {
      return timestamp;
   }

   public String getMaker() {
      return maker;
   }

   public String getTaker() {
      return taker;
   }

   public String getTokenBuy() {
      return tokenBuy;
   }

   public String getTokenSell() {
      return tokenSell;
   }

   public String getTransactionHash() {
      return transactionHash;
   }

   public String getUsdValue() {
      return usdValue;
   }

   @Override
   public String toString() {
      return "TradeHistory [date=" + date + ", amount=" + amount + ", type=" + type + ", total=" + total + ", price="
            + price + ", orderHash=" + orderHash + ", uuid=" + uuid + ", tid=" + tid + ", buyerFee=" + buyerFee
            + ", sellerFee=" + sellerFee + ", gasFee=" + gasFee + ", timestamp=" + timestamp + ", maker=" + maker
            + ", taker=" + taker + ", tokenBuy=" + tokenBuy + ", tokenSell=" + tokenSell + ", transactionHash="
            + transactionHash + ", usdValue=" + usdValue + "]";
   }
}
