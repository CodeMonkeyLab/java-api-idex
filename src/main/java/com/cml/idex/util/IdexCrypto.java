package com.cml.idex.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;

import com.cml.idex.sig.SigParms;

public class IdexCrypto {

   private static final byte[] SALT = "\u0019Ethereum Signed Message:\n32".getBytes(StandardCharsets.UTF_8);

   public static SignatureData createParamsSig(final SigParms params, final Credentials credentials)
         throws IOException {
      return Sign.signMessage(createIdxParamsToSign(params), credentials.getEcKeyPair());
   }

   public static byte[] createIdxParamsToSign(SigParms params) throws IOException {
      byte[] msgArgsSha3 = Hash.sha3(params.encode());
      ByteArrayOutputStream ba = new ByteArrayOutputStream();
      ba.write(SALT);
      ba.write(msgArgsSha3);
      return ba.toByteArray();
   }

   static int MAX_BIT_LENGTH  = 256;
   static int MAX_BYTE_LENGTH = MAX_BIT_LENGTH / 8;

   public static byte[] encodeNumeric(long val) {
      return encodeNumeric(BigInteger.valueOf(val));
   }

   public static byte[] encodeNumeric(BigInteger val) {
      byte[] rawValue = val.toByteArray();
      byte paddingValue = getPaddingValue(val);
      byte[] paddedRawValue = new byte[MAX_BYTE_LENGTH];
      if (paddingValue != 0) {
         for (int i = 0; i < paddedRawValue.length; i++) {
            paddedRawValue[i] = paddingValue;
         }
      }

      System.arraycopy(rawValue, 0, paddedRawValue, MAX_BYTE_LENGTH - rawValue.length, rawValue.length);
      return paddedRawValue;
   }

   private static byte getPaddingValue(BigInteger val) {
      if (val.signum() == -1)
         return (byte) 0xff;
      return 0;
   }
}
