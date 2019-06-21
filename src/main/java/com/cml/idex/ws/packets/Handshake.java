package com.cml.idex.ws.packets;

import com.fasterxml.jackson.databind.JsonNode;

public class Handshake {

   public static final String REQ = "handshake";

   public String getRequest() {
      return "{\"request\": \"handshake\", \"payload\" : \"{ \\\"version\\\": \\\"1.0.0\\\", \\\"key\\\": \\\"17paIsICur8sA0OBqG6dH5G1rmrHNMwt4oNk4iX9\\\"}\"}";
   }

   public RspKey getRspKey() {

   }

   public HandshakeResult parseRsp(final JsonNode root) {

      final String sid = root.get("sid").asText();
      final String rid = root.get("rid").asText();
   }

   public static class HandshakeResult {
      final String sid;

   }
}
