package com.digios.slowmoserver.photomaker;

import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class PhotoMaker {
    final static Logger logger = Logger.getLogger(PhotoMaker.class);


    public interface PhotoMakerListener {
        void onResult(String message);
    }

    public void addListener(PhotoMakerListener toAdd) {
        listeners.add(toAdd);
    }

    private List<PhotoMakerListener> listeners = new ArrayList<PhotoMakerListener>();

    public PhotoMaker() {

    }

    public void ready() throws Exception {
        throw new Exception("no implementation");
    }


    public void excecute() throws Exception {
        throw new Exception("no implementation");
    }

    protected void sendResult(String message) {
        for (PhotoMakerListener pm : listeners)
            pm.onResult(message);
    }

}
