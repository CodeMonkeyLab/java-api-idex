package com.cml.idex.sig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.web3j.utils.Numeric;

import com.cml.idex.util.IdexCrypto;

public class WithdrawSigParms implements SigParms {

   final String     contractAddress;
   final String     token;
   final BigInteger amount;
   final String     address;
   final long       nonce;

   public WithdrawSigParms(String contractAddress, String token, BigInteger amount, String address, long nonce) {
      super();
      this.contractAddress = contractAddress;
      this.token = token;
      this.amount = amount;
      this.address = address;
      this.nonce = nonce;
   }

   public String getContractAddress() {
      return contractAddress;
   }

   public String getToken() {
      return token;
   }

   public BigInteger getAmount() {
      return amount;
   }

   public String getAddress() {
      return address;
   }

   public long getNonce() {
      return nonce;
   }

   @Override
   public byte[] encode() throws IOException {
      final ByteArrayOutputStream ba = new ByteArrayOutputStream(160);
      ba.write(Numeric.hexStringToByteArray(contractAddress));
      ba.write(Numeric.hexStringToByteArray(token));
      ba.write(IdexCrypto.encodeNumeric(amount));
      ba.write(Numeric.hexStringToByteArray(address));
      ba.write(IdexCrypto.encodeNumeric(nonce));
      return ba.toByteArray();
   }
}
