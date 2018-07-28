package com.digios.slowmoserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Variables {

    public static String applicationPath() throws IOException {
        return new File(".").getCanonicalPath();
    }

    public static String configPath() throws IOException {
        return Paths.get(applicationPath(), "config.ini").toString();
    }

    public static String tempDirPath() {
        return System.getProperty("java.io.tmpdir");
    }
}
