package com.cml.idex.value;

import java.math.BigDecimal;

public class OrderTrade {

   final BigDecimal amount;
   final String     type;
   final BigDecimal total;
   final BigDecimal price;
   final String     uuid;
   final String     transactionHash;

   public OrderTrade(
         BigDecimal amount, String type, BigDecimal total, BigDecimal price, String uuid, String transactionHash
   ) {
      super();
      this.amount = amount;
      this.type = type;
      this.total = total;
      this.price = price;
      this.uuid = uuid;
      this.transactionHash = transactionHash;
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

   public String getUuid() {
      return uuid;
   }

   public String getTransactionHash() {
      return transactionHash;
   }

}
