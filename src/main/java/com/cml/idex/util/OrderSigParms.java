package com.cml.idex.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class OrderSigParms implements SigParms {
   final String     contractAddress;
   final String     tokenBuy;
   final BigInteger amountBuy;
   final String     tokenSell;
   final BigInteger amountSell;
   final long       expires;
   final long       nonce;
   final String     address;

   public OrderSigParms(
         String contractAddress, String tokenBuy, BigInteger amountBuy, String tokenSell, BigInteger amountSell,
         long expires, long nonce, String address
   ) {
      super();
      this.contractAddress = contractAddress;
      this.tokenBuy = tokenBuy;
      this.amountBuy = amountBuy;
      this.tokenSell = tokenSell;
      this.amountSell = amountSell;
      this.expires = expires;
      this.nonce = nonce;
      this.address = address;
   }

   public String getContractAddress() {
      return contractAddress;
   }

   public String getTokenBuy() {
      return tokenBuy;
   }

   public BigInteger getAmountBuy() {
      return amountBuy;
   }

   public String getTokenSell() {
      return tokenSell;
   }

   public BigInteger getAmountSell() {
      return amountSell;
   }

   public long getExpires() {
      return expires;
   }

   public long getNonce() {
      return nonce;
   }

   public String getAddress() {
      return address;
   }

   @Override
   public byte[] encode() throws IOException {
      ByteArrayOutputStream ba = new ByteArrayOutputStream();
      ba.write(contractAddress.substring(2).getBytes(StandardCharsets.UTF_8));
      ba.write(tokenBuy.substring(2).getBytes(StandardCharsets.UTF_8));
      ba.write(amountBuy.toByteArray());
      ba.write(tokenSell.substring(2).getBytes(StandardCharsets.UTF_8));
      ba.write(amountSell.toByteArray());
      ba.write(BigInteger.valueOf(expires).toByteArray());
      ba.write(BigInteger.valueOf(nonce).toByteArray());
      ba.write(address.substring(2).getBytes(StandardCharsets.UTF_8));
      return ba.toByteArray();
   }
}
