package com.cml.idex.value;

import java.math.BigInteger;

public class Trade {

   final String     address;
   final BigInteger amount;
   final String     orderHash;
   final long       nonce;
   final byte       v;
   final byte[]     r;
   final byte[]     s;

   public Trade(String address, BigInteger amount, String orderHash, long nonce, byte v, byte[] r, byte[] s) {
      super();
      this.address = address;
      this.amount = amount;
      this.orderHash = orderHash;
      this.nonce = nonce;
      this.v = v;
      this.r = r;
      this.s = s;
   }

   public String getAddress() {
      return address;
   }

   public BigInteger getAmount() {
      return amount;
   }

   public String getOrderHash() {
      return orderHash;
   }

   public long getNonce() {
      return nonce;
   }

   public byte getV() {
      return v;
   }

   public byte[] getR() {
      return r;
   }

   public byte[] getS() {
      return s;
   }
}
