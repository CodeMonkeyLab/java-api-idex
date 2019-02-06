package com.cml.idex.value;

public class OrderParm {

   final String tokenBuy;
   final int    buyPrecision;
   final long   amountBuy;
   final String tokenSell;
   final int    sellPrecision;
   final long   amountSell;
   final Long   expires;
   final long   nonce;
   final String user;

   public OrderParm(
         String tokenBuy, int buyPrecision, long amountBuy, String tokenSell, int sellPrecision, long amountSell,
         Long expires, long nonce, String user
   ) {
      super();
      this.tokenBuy = tokenBuy;
      this.buyPrecision = buyPrecision;
      this.amountBuy = amountBuy;
      this.tokenSell = tokenSell;
      this.sellPrecision = sellPrecision;
      this.amountSell = amountSell;
      this.expires = expires;
      this.nonce = nonce;
      this.user = user;
   }

   public String getTokenBuy() {
      return tokenBuy;
   }

   public int getBuyPrecision() {
      return buyPrecision;
   }

   public long getAmountBuy() {
      return amountBuy;
   }

   public String getTokenSell() {
      return tokenSell;
   }

   public int getSellPrecision() {
      return sellPrecision;
   }

   public long getAmountSell() {
      return amountSell;
   }

   public Long getExpires() {
      return expires;
   }

   public long getNonce() {
      return nonce;
   }

   public String getUser() {
      return user;
   }

   @Override
   public String toString() {
      return "OrderParm [tokenBuy=" + tokenBuy + ", buyPrecision=" + buyPrecision + ", amountBuy=" + amountBuy
            + ", tokenSell=" + tokenSell + ", sellPrecision=" + sellPrecision + ", amountSell=" + amountSell
            + ", expires=" + expires + ", nonce=" + nonce + ", user=" + user + "]";
   }

}
