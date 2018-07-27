package com.digios.slowmoserver;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class Config {

    public static final Config INSTANCE = new Config();

    private static String CONFIG_FILE_PATH = "";

    private Preferences prefs = null;

    private Config() {
        try {
            Ini ini = new Ini(new File(CONFIG_FILE_PATH));
            prefs = new IniPreferences(ini);
        }
        catch (Exception ex) {
            System.exit(1);
        }
    }
}
