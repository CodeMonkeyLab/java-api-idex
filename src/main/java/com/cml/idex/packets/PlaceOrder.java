package com.cml.idex.packets;

import java.math.BigInteger;

import org.web3j.utils.Numeric;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.value.Order;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PlaceOrder implements Req, Parser<Order> {

   final String     tokenBuy;
   final BigInteger amountBuy;
   final String     tokenSell;
   final BigInteger amountSell;
   final String     address;
   final long       nonce;
   final long       expires;
   final byte[]     v;
   final byte[]     r;
   final byte[]     s;

   private PlaceOrder(
         String tokenBuy, BigInteger amountBuy, String tokenSell, BigInteger amountSell, String address, long nonce,
         long expires, byte[] v, byte[] r, byte[] s
   ) {
      super();
      this.tokenBuy = tokenBuy;
      this.amountBuy = amountBuy;
      this.tokenSell = tokenSell;
      this.amountSell = amountSell;
      this.address = address;
      this.nonce = nonce;
      this.expires = expires;
      this.v = v;
      this.r = r;
      this.s = s;
   }

   @Override
   public String getEndpoint() {
      return "order";
   }

   @Override
   public String getPayload() {
      String val = new StringBuilder("{\"tokenBuy\": \"").append(tokenBuy).append("\", \"amountBuy\": \"")
            .append(amountBuy).append("\", \"tokenSell\": \"").append(tokenSell).append("\", \"amountSell\": \"")
            .append(amountSell).append("\", \"address\": \"").append(address).append("\", \"nonce\": \"").append(nonce)
            .append("\", \"expires\": ").append(expires).append(", \"v\": ").append(Numeric.toBigInt(v).longValue())
            .append(", \"r\": \"").append(Numeric.toHexString(r)).append("\", \"s\": \"").append(Numeric.toHexString(s))
            .append("\"}").toString();

      return val;
   }

   @Override
   public Order parse(ObjectMapper mapper, String body) {
      if (Utils.isEmptyJson(body))
         throw new IDexException(ErrorCode.ORDER_FAILED, body);
      return fromJson(mapper, body);
   }

   public static PlaceOrder create(
         String tokenBuy, BigInteger amountBuy, String tokenSell, BigInteger amountSell, String address, Long nonce,
         Long expires, byte[] v, byte[] r, byte[] s
   ) {
      return new PlaceOrder(tokenBuy, amountBuy, tokenSell, amountSell, address, nonce, expires, v, r, s);
   }

   public static Order fromJson(final ObjectMapper mapper, final String body) {
      try {
         final JsonNode root = mapper.readTree(body);
         Utils.checkError(root);

         if (root.get("orderNumber") == null)
            throw new IDexException(ErrorCode.ORDER_FAILED, body);
         return ReturnOpenOrders.parseOrder(root);
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }
}
