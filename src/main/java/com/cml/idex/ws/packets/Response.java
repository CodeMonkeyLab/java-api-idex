package com.cml.idex.ws.packets;

import java.util.Arrays;

import com.cml.idex.ErrorCode;
import com.cml.idex.IDexException;
import com.cml.idex.util.Utils;
import com.cml.idex.ws.Category;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Response<T extends Category> {

   final String           result;
   final String           sid;
   final String           rid;
   final String           request;
   final String[]         warnings;

   final ActionPayload<T> payload;
   final String           errorMessage;

   public Response(
         String result, String sid, String rid, String request, String[] warnings, ActionPayload<T> payload,
         String errorMessage
   ) {
      super();
      this.result = result;
      this.sid = sid;
      this.rid = rid;
      this.request = request;
      this.warnings = warnings;
      this.payload = payload;
      this.errorMessage = errorMessage;
   }

   public String getResult() {
      return result;
   }

   public String getSid() {
      return sid;
   }

   public String getRid() {
      return rid;
   }

   public String getRequest() {
      return request;
   }

   public String[] getWarnings() {
      return warnings;
   }

   public ActionPayload<T> getPayload() {
      return payload;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   public boolean isError() {
      return result != null && "error".equalsIgnoreCase(result);
   }

   @Override
   public String toString() {
      return "Response [result=" + result + ", sid=" + sid + ", rid=" + rid + ", request=" + request + ", warnings="
            + Arrays.toString(warnings) + ", payload=" + payload + ", errorMessage=" + errorMessage + "]";
   }

   public static <T extends Category> Response<T> fromJson(final ObjectMapper mapper, final JsonNode root) {

      try {

         final String result = root.get("result").asText();
         final String sid = root.get("sid").asText();
         final String rid = root.get("rid").asText();
         final String request = root.get("request").asText();
         JsonNode node = root.get("warnings");
         final String[] warnings;
         if (node != null && node.isArray()) {
            warnings = Utils.iteratorToStream(node.elements()).map(JsonNode::asText).toArray(size -> new String[size]);
         } else {
            warnings = node == null ? null : new String[] { node.asText() };
         }

         final String errorMessage;
         final ActionPayload<T> actionPayload;

         final JsonNode payload = mapper.readTree(root.get("payload").asText());
         if ("error".equalsIgnoreCase(result)) {
            node = payload.get("message");
            errorMessage = node == null ? null : node.asText();
            actionPayload = null;
         } else {
            actionPayload = ActionPayload.parse(payload);
            errorMessage = null;
         }
         return new Response<>(result, sid, rid, request, warnings, actionPayload, errorMessage);
      } catch (Exception e1) {
         throw new IDexException(ErrorCode.RESPONSE_PARSE_FAILED, e1.getLocalizedMessage(), e1);
      }
   }
}
