package com.cml.idex.value;

import java.math.BigDecimal;

public class Ticker {
   private final BigDecimal last;
   private final BigDecimal high;
   private final BigDecimal low;
   private final BigDecimal lowestAsk;
   private final BigDecimal highestBid;
   private final BigDecimal percentChange;
   private final BigDecimal baseVolume;
   private final BigDecimal quoteVolume;

   public Ticker(
         BigDecimal last, BigDecimal high, BigDecimal low, BigDecimal lowestAsk, BigDecimal highestBid,
         BigDecimal percentChange, BigDecimal baseVolume, BigDecimal quoteVolume
   ) {
      super();
      this.last = last;
      this.high = high;
      this.low = low;
      this.lowestAsk = lowestAsk;
      this.highestBid = highestBid;
      this.percentChange = percentChange;
      this.baseVolume = baseVolume;
      this.quoteVolume = quoteVolume;
   }

   public BigDecimal getLast() {
      return last;
   }

   public BigDecimal getHigh() {
      return high;
   }

   public BigDecimal getLow() {
      return low;
   }

   public BigDecimal getLowestAsk() {
      return lowestAsk;
   }

   public BigDecimal getHighestBid() {
      return highestBid;
   }

   public BigDecimal getPercentChange() {
      return percentChange;
   }

   public BigDecimal getBaseVolume() {
      return baseVolume;
   }

   public BigDecimal getQuoteVolume() {
      return quoteVolume;
   }

   @Override
   public String toString() {
      return "Ticker [last=" + last + ", high=" + high + ", low=" + low + ", lowestAsk=" + lowestAsk + ", highestBid="
            + highestBid + ", percentChange=" + percentChange + ", baseVolume=" + baseVolume + ", quoteVolume="
            + quoteVolume + "]";
   }

}
