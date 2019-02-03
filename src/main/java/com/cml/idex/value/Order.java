package com.cml.idex.value;

import java.math.BigDecimal;

public class Order {

   final long       timestamp;
   final String     market;
   final long       orderNumber;
   final String     orderHash;
   final BigDecimal price;
   final BigDecimal amount;
   final BigDecimal total;
   final String     type;
   final OrderParm  params;

   public Order(
         long timestamp, String market, long orderNumber, String orderHash, BigDecimal price, BigDecimal amount,
         BigDecimal total, String type, OrderParm params
   ) {
      super();
      this.timestamp = timestamp;
      this.market = market;
      this.orderNumber = orderNumber;
      this.orderHash = orderHash;
      this.price = price;
      this.amount = amount;
      this.total = total;
      this.type = type;
      this.params = params;
   }

   public long getTimestamp() {
      return timestamp;
   }

   public String getMarket() {
      return market;
   }

   public long getOrderNumber() {
      return orderNumber;
   }

   public String getOrderHash() {
      return orderHash;
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

   public String getType() {
      return type;
   }

   public OrderParm getParams() {
      return params;
   }

   @Override
   public String toString() {
      return "Order [timestamp=" + timestamp + ", market=" + market + ", orderNumber=" + orderNumber + ", orderHash="
            + orderHash + ", price=" + price + ", amount=" + amount + ", total=" + total + ", type=" + type
            + ", params=" + params + "]";
   }

}
