package com.cml.idex.packets;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Returns the lowest nonce that you can use from the given address in one of
 * the contract-backed trade functions.
 * 
 * @author plouw
 *
 */
public class ReturnNextNonce implements Req, Parser<Long> {

   private final String address;

   private ReturnNextNonce(final String address) {
      this.address = address;
   }

   @Override
   public String getEndpoint() {
      return "returnNextNonce";
   }

   @Override
   public String getPayload() {
      return "{\"address\": \"" + address + "\"}";
   }

   @Override
   public Long parse(ObjectMapper mapper, String json) {
      if (Utils.isEmptyJson(json))
         throw new IDexException(ErrorCode.UNKNOWN_ADDRESS, address);
      return fromJson(mapper, json);
   }

   public static ReturnNextNonce create(final String address) {
      final String fixedAdr = Utils.fixString(address);
      if (fixedAdr == null)
         throw new IllegalArgumentException("address is required!");
      return new ReturnNextNonce(address);
   }

   private static long fromJson(final ObjectMapper mapper, final String json) {
      try {
         final JsonNode root = mapper.readTree(json);
         Utils.checkError(root);
         return root.get("nonce").asLong();
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }
}
