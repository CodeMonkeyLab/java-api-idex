package com.cml.idex.packets;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReturnContractAddress implements Req, Parser<String> {

   private ReturnContractAddress() {
   }

   @Override
   public String getEndpoint() {
      return "returnContractAddress";
   }

   @Override
   public String getPayload() {
      return "";
   }

   @Override
   public String parse(ObjectMapper mapper, String body) {
      if (Utils.isEmptyJson(body))
         throw new IDexException(ErrorCode.UNKNOWN_ADDRESS);
      return fromJson(mapper, body);
   }

   public static ReturnContractAddress create() {
      return INSTANCE;
   }

   public static ReturnContractAddress INSTANCE = new ReturnContractAddress();

   public static String fromJson(final ObjectMapper mapper, final String body) {
      try {
         final JsonNode root = mapper.readTree(body);
         return root.get("address").asText();
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }
}
