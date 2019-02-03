package com.cml.idex.value;

import java.math.BigDecimal;

public class VolumePair {

   private final String     symbol1;
   private final BigDecimal volume1;
   private final String     symbol2;
   private final BigDecimal volume2;

   public VolumePair(String code1, BigDecimal volume1, String code2, BigDecimal volume2) {
      super();
      this.symbol1 = code1;
      this.volume1 = volume1;
      this.symbol2 = code2;
      this.volume2 = volume2;
   }

   public String getSymbol1() {
      return symbol1;
   }

   public BigDecimal getVolume1() {
      return volume1;
   }

   public String getSymbol2() {
      return symbol2;
   }

   public BigDecimal getVolume2() {
      return volume2;
   }

   /**
    * Returns the Market Code
    * 
    * @return Market Code
    */
   public String getMarket() {
      return symbol1 + "_" + symbol2;
   }

   @Override
   public String toString() {
      return "VolumePair [symbol1=" + symbol1 + ", volume1=" + volume1 + ", symbol2=" + symbol2 + ", volume2=" + volume2
            + "]";
   }

}
