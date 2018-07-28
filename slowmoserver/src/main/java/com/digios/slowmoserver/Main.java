package com.digios.slowmoserver;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Start app");
        try {
            String deviceId = "09a4e13302983c73";

            Device device = new AdbDevice(Config.INSTANCE.adbPath());
            logger.info(device.getDeviceList());
            device.callPhoto(deviceId);
            //device.callSlowmo("84ea8cec");

            Thread.sleep(Config.INSTANCE.waitTime());

            /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Path targetPath = Paths.get(Config.INSTANCE.mediaFolder(), deviceId, dateFormat.format(new Date()));
            Utils.createPath(targetPath);

            device.pullFile(deviceId, Config.INSTANCE.mobilePhotoPath(), targetPath.toString());*/
            Thread.sleep(1000);
            device.clearFolder(deviceId, Config.INSTANCE.mobilePhotoPath());


            /*List<File> photoFiles = new ArrayList<File>();
            photoFiles.add(new File("d:/Temp/media/1.jpg"));

            List<File> videoFiles = new ArrayList<File>();
            videoFiles.add(new File("d:/Temp/media/001.mp4"));

            MovieMaker movieMaker = new MovieWithPhotoMaker(videoFiles, photoFiles);
            movieMaker.render();*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
