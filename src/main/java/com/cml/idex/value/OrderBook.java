package com.cml.idex.value;

import java.util.List;

public class OrderBook {

   final List<Order> bids;
   final List<Order> asks;

   public OrderBook(List<Order> bids, List<Order> asks) {
      super();
      this.bids = bids;
      this.asks = asks;
   }

   public List<Order> getBids() {
      return bids;
   }

   public List<Order> getAsks() {
      return asks;
   }

   @Override
   public String toString() {
      return "OrderBook [bids=" + bids + ", asks=" + asks + "]";
   }

}
