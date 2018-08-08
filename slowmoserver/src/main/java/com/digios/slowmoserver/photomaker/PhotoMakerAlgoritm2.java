package com.digios.slowmoserver.photomaker;

import com.digios.slowmoserver.core.Config;
import com.digios.slowmoserver.core.Utils;
import com.digios.slowmoserver.device.AdbDevice;
import com.digios.slowmoserver.device.Device;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class PhotoMakerAlgoritm2 extends PhotoMaker {
    final static Logger logger = Logger.getLogger(PhotoMakerAlgoritm2.class);

    private List<Device> slowmoDeviceList = new ArrayList<>();
    private List<File> photoFiles = new ArrayList<File>();
    private List<File> videoFiles = new ArrayList<File>();

    private Timer refocusTimer = new Timer();
    private boolean refocusTimerPaused = false;

    private final long VIDEO_DELAY = Config.INSTANCE.videoDelay();
    private final long WAIT = Config.INSTANCE.waitTime();

    public PhotoMakerAlgoritm2() {
        init();
    }

    private void init() {
        List<String> list = Config.INSTANCE.slowmoDevices();
        for (String d : list) {
            slowmoDeviceList.add(new AdbDevice(d));
        }

        refocusTimer.schedule(new PhotoMakerAlgoritm2.CameraRefocusTimerTask(), 1000, Config.INSTANCE.touthInterval());
    }

    @Override
    public void excecute() throws Exception {

        /*videoFiles.clear();
        videoFiles.add(new File("d:\\Temp\\media\\video\\001.mp4"));
        videoFiles.add(new File("d:\\Temp\\media\\video\\002.mp4"));
        videoFiles.add(new File("d:\\Temp\\media\\video\\003.mp4"));
        videoFiles.add(new File("d:\\Temp\\media\\video\\004.mp4"));

        String resultFile = makeVideo();*/

        photoFiles.clear();
        videoFiles.clear();

        getMediaFile();
        String resultFile = makeVideo();
        sendResult(resultFile);
    }

    private void getMediaFile() throws Exception {
        for (Device d : slowmoDeviceList) {
            d.callSlowmo();
        }

        Thread.sleep(WAIT);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Path targetPath = Paths.get(Config.INSTANCE.mediaFolder(), dateFormat.format(new Date()));
        Utils.createPath(targetPath);

        for (Device d : slowmoDeviceList) {
            List<String> fileList = d.pullFiles(Config.INSTANCE.mobilePhotoPath(), targetPath.toString());
            d.clearFolder(Config.INSTANCE.mobilePhotoPath());

            videoFiles.add(0, Utils.lastFileModified(targetPath.toString(), false));
            /*if (!fileList.isEmpty())
                photoFiles.add(0, new File(fileList.get(0)));*/
        }
    }

    private String makeVideo() throws Exception {
        MovieMaker movieMaker = new MovieFrom4Maker(videoFiles, photoFiles);
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
        //send(json);
    }

    /*@Override
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
    }*/

    class CameraRefocusTimerTask extends TimerTask {
        @Override
        public void run() {
            if (!refocusTimerPaused) {
                for (Device d : slowmoDeviceList) {
                    d.callFocus();
                }
            }
        }
    }
}
