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

   /**
    * Decimal Places.
    * 
    * @return Decimal Places
    */
   public int getDecimals() {
      return decimals;
   }

   /**
    * Ethereum Contract Address unless the token is ETH the uses
    * IDexAPI.DEFAULT_ETH_ADR.
    * 
    * @return Ethereum Contract Address for Token.
    */
   public String getAddress() {
      return address;
   }

   /**
    * Name of the Token.
    * 
    * @return Name
    */
   public String getName() {
      return name;
   }

   @Override
   public String toString() {
      return "Currency [decimals=" + decimals + ", address=" + address + ", name=" + name + "]";
   }
}
