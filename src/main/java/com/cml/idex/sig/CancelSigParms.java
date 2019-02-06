package com.cml.idex.sig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.web3j.utils.Numeric;

import com.cml.idex.util.IdexCrypto;

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
      ByteArrayOutputStream ba = new ByteArrayOutputStream(64);
      ba.write(Numeric.hexStringToByteArray(orderHash));
      ba.write(IdexCrypto.encodeNumeric(nonce));
      return ba.toByteArray();
   }
}
