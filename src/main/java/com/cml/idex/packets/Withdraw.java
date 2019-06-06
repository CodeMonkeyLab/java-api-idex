package com.cml.idex.packets;

import java.math.BigInteger;
import java.util.Objects;

import org.web3j.utils.Numeric;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.value.Outcome;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Withdraw implements Req, Parser<Outcome> {

   final String     address;
   final BigInteger amount;
   final String     token;
   final long       nonce;
   final byte[]     v;
   final byte[]     r;
   final byte[]     s;

   public Withdraw(String address, BigInteger amount, String token, long nonce, byte[] v, byte[] r, byte[] s) {
      super();
      this.address = address;
      this.amount = amount;
      this.token = token;
      this.nonce = nonce;
      this.v = v;
      this.r = r;
      this.s = s;
   }

   @Override
   public String getEndpoint() {
      return "withdraw";
   }

   @Override
   public String getPayload() {
      return new StringBuilder("{\"address\": \"").append(address).append("\", \"amount\": \"").append(amount)
            .append("\", \"token\": \"").append(token).append("\", \"nonce\": \"").append(nonce).append("\", \"v\": ")
            .append(Numeric.toBigInt(v).longValue()).append(", \"r\": \"").append(Numeric.toHexString(r))
            .append("\", \"s\": \"").append(Numeric.toHexString(s)).append("\"}").toString();
   }

   @Override
   public Outcome parse(ObjectMapper mapper, String json) {
      if (Utils.isEmptyJson(json))
         throw new IDexException(ErrorCode.WITHDRAW_FAILED, json);
      return fromJson(mapper, json);
   }

   public static Withdraw create(
         String address, BigInteger amount, String token, long nonce, byte[] v, byte[] r, byte[] s
   ) {
      Objects.requireNonNull(address, "address is required!");
      Objects.requireNonNull(amount, "amount is required!");
      Objects.requireNonNull(token, "token is required!");
      Objects.requireNonNull(nonce, "nonce is required!");
      Objects.requireNonNull(v, "v is required!");
      Objects.requireNonNull(r, "r is required!");
      Objects.requireNonNull(s, "s is required!");
      return new Withdraw(address, amount, token, nonce, v, r, s);
   }

   private static Outcome fromJson(final ObjectMapper mapper, final String body) {
      try {
         final JsonNode root = mapper.readTree(body);
         JsonNode node = root.get("error");
         if (node != null)
            return new Outcome(node.asText(), -1);

         return new Outcome("successful", 0);
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }
}
