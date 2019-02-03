package com.cml.idex.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class CancelSigParms implements SigParms {

   final String orderHash;
   final Long   nonce;

   public CancelSigParms(String orderHash, Long nonce) {
      super();
      this.orderHash = orderHash;
      this.nonce = nonce;
   }

   @Override
   public byte[] encode() throws IOException {
      ByteArrayOutputStream ba = new ByteArrayOutputStream();
      ba.write(orderHash.substring(2).getBytes(StandardCharsets.UTF_8));
      ba.write(BigInteger.valueOf(nonce).toByteArray());
      return ba.toByteArray();
   }
}
