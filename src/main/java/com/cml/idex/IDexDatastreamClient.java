package com.cml.idex;

import java.util.concurrent.ExecutionException;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.netty.ws.NettyWebSocket;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;

public class IDexDatastreamClient {

	private static final String WEBSOCKET_ENDPOINT = "wss://datastream.idex.market";

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		AsyncHttpClient client = Dsl.asyncHttpClient();

		NettyWebSocket wsClient = client.prepareGet(WEBSOCKET_ENDPOINT)
				.execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketListener() {

					@Override
					public void onOpen(WebSocket websocket) {
						websocket.sendTextFrame(
								"{\"request\": \"handshake\", \"payload\" : \"{ \\\"version\\\": \\\"1.0.0\\\", \\\"key\\\": \\\"17paIsICur8sA0OBqG6dH5G1rmrHNMwt4oNk4iX9\\\"}\"}");
					}

					@Override
					public void onClose(WebSocket websocket, int code, String reason) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onTextFrame(String payload, boolean finalFragment, int rsv) {
						System.out.println(payload);
					}

					@Override
					public void onError(Throwable t) {
					}

				}).build()).get();

	}
}
