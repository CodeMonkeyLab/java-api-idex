package com.cml.idex.value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TradeResponse {

   final BigDecimal    amount;
   final LocalDateTime date;
   final BigDecimal    total;
   final String        market;
   final String        type;
   final BigDecimal    price;
   final String        orderHash;
   final String        uuid;

   public TradeResponse(
         BigDecimal amount, LocalDateTime date, BigDecimal total, String market, String type, BigDecimal price,
         String orderHash, String uuid
   ) {
      super();
      this.amount = amount;
      this.date = date;
      this.total = total;
      this.market = market;
      this.type = type;
      this.price = price;
      this.orderHash = orderHash;
      this.uuid = uuid;
   }

   public BigDecimal getAmount() {
      return amount;
   }

   public LocalDateTime getDate() {
      return date;
   }

   public BigDecimal getTotal() {
      return total;
   }

   public String getMarket() {
      return market;
   }

   public String getType() {
      return type;
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

   @Override
   public String toString() {
      return "Trade [amount=" + amount + ", date=" + date + ", total=" + total + ", market=" + market + ", type=" + type
            + ", price=" + price + ", orderHash=" + orderHash + ", uuid=" + uuid + "]";
   }
}
