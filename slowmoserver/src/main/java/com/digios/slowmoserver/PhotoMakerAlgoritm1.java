package com.digios.slowmoserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class PhotoMakerAlgoritm1 extends PhotoMaker {
    final static Logger logger = Logger.getLogger(PhotoMakerAlgoritm1.class);

    private List<Device> photoDevicesList = new ArrayList<>();
    private Device slowmoDevice = null;
    private List<File> photoFiles = new ArrayList<File>();
    private List<File> videoFiles = new ArrayList<File>();

    private Timer refocusTimer = new Timer();
    private boolean refocusTimerPaused = false;

    private final long VIDEO_DELAY = Config.INSTANCE.videoDelay();
    private final long WAIT = Config.INSTANCE.waitTime();

    public PhotoMakerAlgoritm1(URI serverURI) {
        super( serverURI );
        init();
    }

    private void init() {
        List<String> list = Config.INSTANCE.photoDevices();
        for (String d : list) {
            photoDevicesList.add(new AdbDevice(d));
        }

        if (!Config.INSTANCE.slowmoDevices().isEmpty())
            slowmoDevice = new AdbDevice(Config.INSTANCE.slowmoDevices().get(0));

        refocusTimer.schedule(new CameraRefocusTimerTask(), 1000, Config.INSTANCE.touthInterval());
    }

    @Override
    public void excecute() throws Exception {
        refocusTimerPaused = true;

        photoFiles.clear();
        videoFiles.clear();

        getMediaFile();
        String resultFile = makeVideo();
        sendResult(resultFile);

        refocusTimerPaused = false;
    }

    private void getMediaFile() throws Exception {

        for (Device d : photoDevicesList) {
            d.openCamera();
        }

        Thread.sleep(1000);

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

            //videoFiles.add(0, Utils.lastFileModified(targetPath.toString(), false));
            logger.info(fileList);

            if (!fileList.isEmpty()) {
                for (String file : fileList)
                {
                    if (Utils.getFileExtension(file).equals("mp4")) {
                        videoFiles.add(new File(file));
                        break;
                    }
                }
            }
        }



        for (Device d : photoDevicesList) {
            List<String> fileList = d.pullFiles(Config.INSTANCE.mobilePhotoPath(), targetPath.toString());
            d.clearFolder(Config.INSTANCE.mobilePhotoPath());

            //photoFiles.add(0,Utils.lastFileModified(targetPath.toString(), false));

            logger.info(fileList);

            /*if (!fileList.isEmpty()) {
                photoFiles.add(0, new File(fileList.get(0)));
            }*/
            if (!fileList.isEmpty()) {
                for (String file : fileList)
                {
                    if (Utils.getFileExtension(file).equals("jpg")) {
                        photoFiles.add(new File(file));
                        break;
                    }
                }
            }
        }
    }

    private String makeVideo() throws Exception {
        MovieMaker movieMaker = new MovieWithPhotoMaker(videoFiles, photoFiles);
        return movieMaker.render();
    }

    private void sendResult(String fileResult) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cmd", "result");
        map.put("path", fileResult);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        String json = gson.toJson(map);
        send(json);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info("Opened connection");
    }

    @Override
    public void onMessage(String json) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        try {
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> map = gson.fromJson(json, type);

            if (map.containsKey("cmd") && map.get("cmd").equals("shoot")) {
                logger.info("Server command shoot");
                try {
                    excecute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception ex) {
            logger.error(ex);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("Connection closed by " + ( remote ? "remote peer" : "us" ) + " Code: " + code + " Reason: " + reason );
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("Reconnect");
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

        logger.info("Reconnect");
        reconnect();
    }

    class CameraRefocusTimerTask extends TimerTask {
        @Override
        public void run() {
            if (slowmoDevice != null && !refocusTimerPaused)
                slowmoDevice.callFocus();
        }
    }
}
