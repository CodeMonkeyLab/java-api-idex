package com.cml.idex.value;

import java.math.BigDecimal;

public class BalanceOrder {

   final BigDecimal available;
   final BigDecimal onOrders;

   public BalanceOrder(BigDecimal available, BigDecimal onOrders) {
      super();
      this.available = available;
      this.onOrders = onOrders;
   }

   /**
    * Available Balance.
    * 
    * @return Available Balance
    */
   public BigDecimal getAvailable() {
      return available;
   }

   /**
    * Balance in orders
    * 
    * @return Balance in orders.
    */
   public BigDecimal getOnOrders() {
      return onOrders;
   }

   @Override
   public String toString() {
      return "BalanceOrder [available=" + available + ", onOrders=" + onOrders + "]";
   }

}
