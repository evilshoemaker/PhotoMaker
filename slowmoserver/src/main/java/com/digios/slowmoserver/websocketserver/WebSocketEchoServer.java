package com.digios.slowmoserver.websocketserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;

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
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        try {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> map = gson.fromJson(message, type);

            if (map.containsKey("cmd")) {
                if (map.get("cmd").equals("shoot") || map.get("cmd").equals("ready")) {
                    broadcast(message);
                }
                else if (map.get("cmd").equals("save")) {
                    DataBase.addUserInfo(map.get("file"), map.get("email"));
                }
                else if (map.get("cmd").equals("result")) {
                    DataBase.addFile(map.get("path"));
                }
            }
        }
        catch (Exception ex) {
            logger.error(ex);
        }
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
