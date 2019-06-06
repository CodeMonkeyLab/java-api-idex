package com.cml.idex.packets;

import org.web3j.utils.Numeric;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.value.Outcome;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CancelOrder implements Req, Parser<Outcome> {

   final String orderHash;
   final String address;
   final long   nonce;
   final byte[] v;
   final byte[] r;
   final byte[] s;

   private CancelOrder(String orderHash, String address, long nonce, byte[] v, byte[] r, byte[] s) {
      super();
      this.orderHash = orderHash;
      this.address = address;
      this.nonce = nonce;
      this.v = v;
      this.r = r;
      this.s = s;
   }

   @Override
   public String getEndpoint() {
      return "cancel";
   }

   @Override
   public String getPayload() {
      return new StringBuilder("{\"orderHash\": \"").append(orderHash).append("\", \"address\": \"").append(address)
            .append("\", \"nonce\": \"").append(nonce).append("\", \"v\": ").append(Numeric.toBigInt(v).longValue())
            .append(", \"r\": \"").append(Numeric.toHexString(r)).append("\", \"s\": \"").append(Numeric.toHexString(s))
            .append("\"}").toString();
   }

   @Override
   public Outcome parse(ObjectMapper mapper, String body) {
      if (Utils.isEmptyJson(body))
         throw new IDexException(ErrorCode.ORDER_FAILED, body);
      return fromJson(mapper, body);
   }

   public static CancelOrder create(String orderHash, String address, long nonce, byte[] v, byte[] r, byte[] s) {
      return new CancelOrder(orderHash, address, nonce, v, r, s);
   }

   private static Outcome fromJson(final ObjectMapper mapper, final String body) {
      try {
         final JsonNode root = mapper.readTree(body);
         if (root.get("success") == null) {
            if (root.get("error") != null)
               return new Outcome(root.get("error").asText(), -1);
            return new Outcome("failed", -1);
         }
         return new Outcome("success", root.get("success").asInt());
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }
}
