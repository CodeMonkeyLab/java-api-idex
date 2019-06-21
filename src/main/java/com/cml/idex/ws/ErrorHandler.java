package com.cml.idex.ws;

import org.asynchttpclient.ws.WebSocket;

public class ErrorHandler {

   public static final void onClose(WebSocket websocket, int code, String reason) {

      switch (code) {
         case 1002: {
            // All these are very bad!
            // ProtocolNegotiationFailure
            // - Handshake delayed?
            // AuthenticationFailure
            // - Do not reconnect!
            // InvalidVersion
            // - Do not reconnect!
            // SessionIDMismatch
            // - sid miss match weird issue
         }
            break;
         case 1006: {
            // Usually due to no subscriptions idle connection
         }
            break;
         case 1012: {
            // ServiceRestarting

            // Do exponential backoff reconnect
            // Think about auto resubscribe on this disconnect?
         }
         default:
            // TODO: do something here
            throw new IllegalStateException("Unhandles connection close Code=" + code + ", Reason=" + reason);

      }
   }
}
