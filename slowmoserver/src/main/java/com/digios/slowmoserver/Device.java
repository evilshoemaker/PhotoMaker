package com.digios.slowmoserver;

import java.util.List;

public interface Device {
    List<String> getDeviceList();
    String getDeviceId();
    void callPhoto();
    void callSlowmo();
    List<String> pullFiles(String source, String target);
    void clearFolder(String path);
}
