package com.digios.slowmoserver;

import org.apache.log4j.Logger;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

public class Config {
    final static Logger logger = Logger.getLogger(Config.class);

    public static final Config INSTANCE = new Config();
    private Preferences prefs = null;

    private Config() {
        try {
            Ini ini = new Ini(new File(Variables.configPath()));
            prefs = new IniPreferences(ini);
        }
        catch (Exception ex) {
            logger.error(ex);
            System.exit(1);
        }
    }

    public String mediaFolder() {
        return prefs.node("app").get("MEDIA_FOLDER", null);
    }

    public String ffmpegPath() {
        return prefs.node("app").get("FFMPEG", "./bin/ffmpeg/ffmpeg.exe");
    }

    public String ffprobePath() {
        return prefs.node("app").get("FFPROBE", "./bin/ffmpeg/ffprobe.exe");
    }

    public long photoTime() {
        return prefs.node("app").getLong("PHOTO_TIME", 500);
    }

    public long videoDelay() {
        return prefs.node("app").getLong("VIDEO_DELAY", 0);
    }

    public String resultFullPath() {
        return prefs.node("app").get("RESULT_FULLPATH", null);
    }

    public String resultStartVideoFile() {
        return prefs.node("app").get("RESULT_START", null);
    }

    public String resultFinishVideoFile() {
        return prefs.node("app").get("RESULT_FINISH", null);
    }

    public String adbPath() {
        return prefs.node("app").get("ADB", "./bin/adb/adb.exe");
    }

    public String mobilePhotoPath() {
        return prefs.node("app").get("MOBILE_PHOTO_PATH", "/sdcard/DCIM/Camera/");
    }

    public long waitTime() {
        return prefs.node("app").getLong("WAIT", 4000);
    }

    public List<String> photoDevices() {
        String devices = prefs.node("app").get("PHOTO_DEVICES", "");
        return Arrays.asList(devices.split(","));
    }

    public List<String> slowmoDevices() {
        String devices = prefs.node("app").get("SLOWMO_DEVICE", "");
        return Arrays.asList(devices.split(","));
    }
}
