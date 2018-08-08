package com.digios.slowmoserver.websocketserver;

import com.digios.slowmoserver.command.GalleryResponse;
import com.digios.slowmoserver.database.DataBase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {
    final static Logger logger = Logger.getLogger(WebSocketServer.class);

    public interface WebSocketServerListener {
        void onShot();
        void onReady();
        void onMessage(String message);
    }

    public void addListener(WebSocketServerListener toAdd) {
        listeners.add(toAdd);
    }

    private List<WebSocketServer.WebSocketServerListener> listeners = new ArrayList<WebSocketServer.WebSocketServerListener>();

    public WebSocketServer(int port ) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
    }

    public WebSocketServer(InetSocketAddress address ) {
        super( address );
    }

    public void sendBroadcast(String message) {
        broadcast(message);
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
        logger.info(message);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        try {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> map = gson.fromJson(message, type);

            if (map.containsKey("cmd")) {

                for (WebSocketServerListener hl : listeners)
                    hl.onMessage(message);

                if (map.get("cmd").equals("shoot")) {
                    for (WebSocketServerListener hl : listeners)
                        hl.onShot();
                }
                else if (map.get("cmd").equals("ready")) {
                    for (WebSocketServerListener hl : listeners)
                        hl.onReady();
                }
                else if (map.get("cmd").equals("save")) {
                    DataBase.addUserInfo(map.get("file"), map.get("email"));
                }
                else if (map.get("cmd").equals("result")) {
                    DataBase.addFile(map.get("path"));
                }
                else if (map.get("cmd").equals("refresh")) {
                    List<String> files = DataBase.getFiles();

                    GalleryResponse galleryResponse = new GalleryResponse(files);

                    String json = gson.toJson(galleryResponse);
                    webSocket.send(json);
                }
            }

            broadcast(message);
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
