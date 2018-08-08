package com.digios.slowmoserver.device;

import com.digios.slowmoserver.core.Config;
import com.digios.slowmoserver.core.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AdbDevice implements Device {
    final static Logger logger = Logger.getLogger(AdbDevice.class);
    private static String ADB_PATH = Config.INSTANCE.adbPath();

    private String deviceId;

    public AdbDevice(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public static List<String> getDeviceList() {
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
    public void callPhoto() {
        String command = String.format("%s -s %s shell input keyevent 27", ADB_PATH, deviceId);
        Utils.executeCommandNotWait(command);
    }

    @Override
    public void callSlowmo() {
        String command = String.format("%s -s %s shell input keyevent 130", ADB_PATH, deviceId);
        Utils.executeCommandNotWait(command);
    }

    @Override
    public void callFocus() {
        String command = String.format("%s -s %s shell input keyevent KEYCODE_FOCUS", ADB_PATH, deviceId);
        Utils.executeCommandNotWait(command);
    }

    @Override
    public void openCamera() {
        String command = String.format("%s -s %s shell am start -a android.media.action.IMAGE_CAPTURE", ADB_PATH, deviceId);
        Utils.executeCommandNotWait(command);
    }

    @Override
    public List<String> pullFiles(String source, String target) {
        String command = String.format("%s -s %s pull -a %s \"%s\"", ADB_PATH, deviceId, source, target);
        String result = Utils.executeCommand(command);
        if (!result.isEmpty())
            logger.info(result);

        List<String> files = new ArrayList<>();

        String lines[] = result.split("\\r?\\n");
        for (String line : lines) {
            int d = line.indexOf("->");
            if (d != -1) {
                files.add(line.substring(d + 3));
            }
        }

        return files;
    }

    @Override
    public void pushFile(String sourceFile, String targetFile) {
        String command = String.format("%s -s %s push %s \"%s\"", ADB_PATH, deviceId, sourceFile, targetFile);
        Utils.executeCommandNotWait(command);
    }

    @Override
    public void clearFolder(String path) {
        String command = String.format("%s -s %s shell ls %s", ADB_PATH, deviceId, path);
        String result = Utils.executeCommand(command);

        if (!result.isEmpty())
            logger.info(result);
        else
            return;

        String lines[] = result.split("\\r?\\n");

        for (String file : lines) {
            String fullPath = path + file;
            command = String.format("%s -s %s shell rm -f %s", ADB_PATH, deviceId, fullPath);
            result = Utils.executeCommand(command);
            if (!result.isEmpty())
                logger.info(result);
        }
    }
}
