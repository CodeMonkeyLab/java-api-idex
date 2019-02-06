package com.cml.idex.sig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.web3j.utils.Numeric;

import com.cml.idex.util.IdexCrypto;

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
      ByteArrayOutputStream ba = new ByteArrayOutputStream(256);
      ba.write(Numeric.hexStringToByteArray(contractAddress));
      ba.write(Numeric.hexStringToByteArray(tokenBuy));
      ba.write(IdexCrypto.encodeNumeric(amountBuy));
      ba.write(Numeric.hexStringToByteArray(tokenSell));
      ba.write(IdexCrypto.encodeNumeric(amountSell));
      ba.write(IdexCrypto.encodeNumeric(expires));
      ba.write(IdexCrypto.encodeNumeric(nonce));
      ba.write(Numeric.hexStringToByteArray(address));
      return ba.toByteArray();
   }
}
