package com.cml.idex.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class TradeSigParms implements SigParms {

   final String     orderHash;
   final BigInteger amount;
   final String     address;
   final Long       nonce;

   public TradeSigParms(String orderHash, BigInteger amount, String address, Long nonce) {
      super();
      this.orderHash = orderHash;
      this.amount = amount;
      this.address = address;
      this.nonce = nonce;
   }

   @Override
   public byte[] encode() throws IOException {
      ByteArrayOutputStream ba = new ByteArrayOutputStream();
      ba.write(orderHash.substring(2).getBytes(StandardCharsets.UTF_8));
      ba.write(amount.toByteArray());
      ba.write(address.substring(2).getBytes(StandardCharsets.UTF_8));
      ba.write(BigInteger.valueOf(nonce).toByteArray());
      return ba.toByteArray();
   }

}
