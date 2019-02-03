package com.cml.idex.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;

public class IdexCrypto {

   private static final byte[] SALT = "\u0019Ethereum Signed Message:\n".getBytes(StandardCharsets.UTF_8);

   public static SignatureData createParamsSig(final SigParms params, final Credentials credentials)
         throws IOException {
      return Sign.signMessage(createIdxParamsToSign(params), credentials.getEcKeyPair());
   }

   private static byte[] createIdxParamsToSign(SigParms params) throws IOException {
      byte[] msgArgsSha3 = Hash.sha3(params.encode());
      ByteArrayOutputStream ba = new ByteArrayOutputStream();
      ba.write(SALT);
      ba.write(msgArgsSha3);
      return Hash.sha3(ba.toByteArray());
   }
}
