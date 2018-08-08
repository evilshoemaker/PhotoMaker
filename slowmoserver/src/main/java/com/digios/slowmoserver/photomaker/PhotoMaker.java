package com.digios.slowmoserver.photomaker;

import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;

public class PhotoMaker extends Thread {
    final static Logger logger = Logger.getLogger(PhotoMaker.class);

    public PhotoMaker() {

    }
    @Override
    public void run()
    {
        try {
            excecute();
        }
        catch (Exception ex)  {
            logger.error(ex);
        }
    }


    void excecute() throws Exception {
        throw new Exception("no implementation");
    }

}
