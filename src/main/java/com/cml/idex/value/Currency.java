package com.cml.idex.value;

public class Currency {

   final int    decimals;
   final String address;
   final String name;

   public Currency(int decimals, String address, String name) {
      super();
      this.decimals = decimals;
      this.address = address;
      this.name = name;
   }

   public int getDecimals() {
      return decimals;
   }

   public String getAddress() {
      return address;
   }

   public String getName() {
      return name;
   }

   @Override
   public String toString() {
      return "Currency [decimals=" + decimals + ", address=" + address + ", name=" + name + "]";
   }
}
