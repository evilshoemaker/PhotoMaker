package com.digios.slowmoserver.photomaker;

import com.digios.slowmoserver.core.Config;
import com.digios.slowmoserver.device.AdbDevice;
import com.digios.slowmoserver.device.Device;
import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PhotoMaker {
    final static Logger logger = Logger.getLogger(PhotoMaker.class);

    protected final long VIDEO_DELAY = Config.INSTANCE.videoDelay();
    protected final long WAIT = Config.INSTANCE.waitTime();
    protected final String TAB_FOLDER = Config.INSTANCE.tabFolder();

    protected List<Device> tabDeviceList = new ArrayList<>();

    public interface PhotoMakerListener {
        void onResult(String message);
    }

    public void addListener(PhotoMakerListener toAdd) {
        listeners.add(toAdd);
    }

    private List<PhotoMakerListener> listeners = new ArrayList<PhotoMakerListener>();

    public PhotoMaker() {

    }

    protected void init() {
        List<String> list = Config.INSTANCE.tabDevices();
        for (String d : list) {
            tabDeviceList.add(new AdbDevice(d));
        }
    }

    public void ready() throws Exception {
        throw new Exception("no implementation");
    }


    public void execute() throws Exception {
        throw new Exception("no implementation");
    }

    protected void sendResult(String message) {
        for (PhotoMakerListener pm : listeners)
            pm.onResult(message);
    }

    protected void copyFoleToTab(String resultFile) {
        String targetFile = Paths.get(TAB_FOLDER,
                Paths.get(resultFile).getFileName().toString()).toString();

        for (Device d : tabDeviceList) {
            d.pushFile(resultFile, targetFile);
        }
    }

}
