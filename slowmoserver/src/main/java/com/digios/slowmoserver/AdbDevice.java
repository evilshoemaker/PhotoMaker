package com.digios.slowmoserver;

import org.apache.log4j.Logger;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AdbDevice implements Device {
    final static Logger logger = Logger.getLogger(AdbDevice.class);
    private static String ADB_PATH = "";

    public AdbDevice(String adbPath) {
        ADB_PATH = adbPath;
    }

    @Override
    public List<String> getDeviceList() {
        List<String> resultList = new ArrayList<>();

        String command = "adb devices";
        String result = Utils.executeCommand(command);

        String lines[] = result.split("\\r?\\n");
        for (String s : lines) {
            int d = s.indexOf('\t');
            if (d != -1)
            {
                resultList.add(s.substring(0, d));
            }
        }

        return resultList;
    }

    @Override
    public void callPhoto(String deviceId) {
        String command = String.format("adb -s %s shell input keyevent 27", deviceId);
        String result = Utils.executeCommand(command);
        if (!result.isEmpty())
            logger.info(result);
    }

    @Override
    public void callSlowmo(String deviceId) {
        String command = String.format("adb -s %s shell input keyevent 130", deviceId);
        String result = Utils.executeCommand(command);
        if (!result.isEmpty())
            logger.info(result);
    }

    @Override
    public void pullFile(String deviceId, String source, String target) {
        String command = String.format("adb -s %s pull -a %s \"%s\"", deviceId, source, target);
        String result = Utils.executeCommand(command);
        if (!result.isEmpty())
            logger.info(result);
    }

    @Override
    public void clearFolder(String deviceId, String path) {
        String command = String.format("adb -s %s shell ls %s", deviceId, path);
        String result = Utils.executeCommand(command);

        if (!result.isEmpty())
            logger.info(result);
        else
            return;

        String lines[] = result.split("\\r?\\n");

        for (String file : lines) {
            String fullPath = path + file;
            command = String.format("adb -s %s shell rm -f %s", deviceId, fullPath);
            result = Utils.executeCommand(command);
            if (!result.isEmpty())
                logger.info(result);
        }
    }
}
