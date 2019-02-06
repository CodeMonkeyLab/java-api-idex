package com.cml.idex.value;

import java.math.BigDecimal;

public class Order {

   final Long       timestamp;
   final String     market;
   final Long       orderNumber;
   final String     orderHash;
   final BigDecimal price;
   final BigDecimal amount;
   final BigDecimal total;
   final String     type;
   final OrderParm  params;

   public Order(
         Long timestamp, String market, Long orderNumber, String orderHash, BigDecimal price, BigDecimal amount,
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

   public Long getTimestamp() {
      return timestamp;
   }

   public String getMarket() {
      return market;
   }

   public Long getOrderNumber() {
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
