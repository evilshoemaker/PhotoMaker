package com.digios.slowmoserver;

import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PhotoMakerAlgoritm1 extends WebSocketClient implements PhotoMaker {
    final static Logger logger = Logger.getLogger(PhotoMakerAlgoritm1.class);

    List<Device> photoDevicesList = new ArrayList<>();
    Device slowmoDevice = null;
    List<File> photoFiles = new ArrayList<File>();
    List<File> videoFiles = new ArrayList<File>();

    private final long VIDEO_DELAY = Config.INSTANCE.videoDelay();
    private final long WAIT = Config.INSTANCE.waitTime();

    public PhotoMakerAlgoritm1(URI serverUri , Draft draft) {
        super( serverUri, draft );
        init();
    }

    public PhotoMakerAlgoritm1(URI serverURI) {
        super( serverURI );
        init();
    }

    public PhotoMakerAlgoritm1(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
        init();
    }

    private void init() {
        List<String> list = Config.INSTANCE.photoDevices();
        for (String d : list) {
            photoDevicesList.add(new AdbDevice(d));
        }

        if (!Config.INSTANCE.slowmoDevices().isEmpty())
            slowmoDevice = new AdbDevice(Config.INSTANCE.slowmoDevices().get(0));
    }

    @Override
    public void excecute() throws Exception {
        photoFiles.clear();
        videoFiles.clear();

        getMediaFile();
        String resultFile = makeVideo();
        sendResult(resultFile);
    }

    private void getMediaFile() throws Exception {
        if (slowmoDevice != null) {
            slowmoDevice.callSlowmo();

            Thread.sleep(VIDEO_DELAY);
        }

        for (Device d : photoDevicesList) {
            d.callPhoto();
        }

        Thread.sleep(WAIT);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Path targetPath = Paths.get(Config.INSTANCE.mediaFolder(), dateFormat.format(new Date()));
        Utils.createPath(targetPath);

        if (slowmoDevice != null)
        {
            List<String> fileList = slowmoDevice.pullFiles(Config.INSTANCE.mobilePhotoPath(), targetPath.toString());
            slowmoDevice.clearFolder(Config.INSTANCE.mobilePhotoPath());
            if (!fileList.isEmpty())
                videoFiles.add(new File(fileList.get(0)));
        }



        for (Device d : photoDevicesList) {
            List<String> fileList = d.pullFiles(Config.INSTANCE.mobilePhotoPath(), targetPath.toString());
            d.clearFolder(Config.INSTANCE.mobilePhotoPath());

            if (!fileList.isEmpty())
                photoFiles.add(0, new File(fileList.get(0)));
        }
    }

    private String makeVideo() throws Exception {
        MovieMaker movieMaker = new MovieWithPhotoMaker(videoFiles, photoFiles);
        return movieMaker.render();
    }

    private void sendResult(String fileResult) {

    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info("Opened connection");
    }

    @Override
    public void onMessage(String s) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("Connection closed by " + ( remote ? "remote peer" : "us" ) + " Code: " + code + " Reason: " + reason );
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        reconnect();
    }

    @Override
    public void onError(Exception e) {
        logger.error(e);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        reconnect();
    }
}
