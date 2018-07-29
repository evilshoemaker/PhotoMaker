package com.digios.slowmoserver;

import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class WebSocketEchoServer extends WebSocketServer {
    final static Logger logger = Logger.getLogger(WebSocketEchoServer.class);

    public WebSocketEchoServer( int port ) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
    }

    public WebSocketEchoServer( InetSocketAddress address ) {
        super( address );
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        logger.info(webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + " connect" );
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        logger.info(webSocket + " disconnect" );
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        broadcast(message);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        logger.error(e);
    }

    @Override
    public void onStart() {
        logger.info("Server started!");
    }
}
