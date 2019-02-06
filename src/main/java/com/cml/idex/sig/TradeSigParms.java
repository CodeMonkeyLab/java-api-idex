package com.cml.idex.sig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.web3j.utils.Numeric;

import com.cml.idex.util.IdexCrypto;

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
      ByteArrayOutputStream ba = new ByteArrayOutputStream(128);
      ba.write(Numeric.hexStringToByteArray(orderHash));
      ba.write(IdexCrypto.encodeNumeric(amount));
      ba.write(Numeric.hexStringToByteArray(address));
      ba.write(IdexCrypto.encodeNumeric(nonce));
      return ba.toByteArray();
   }

}
