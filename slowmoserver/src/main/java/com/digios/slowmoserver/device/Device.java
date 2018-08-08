package com.digios.slowmoserver.device;

import java.util.List;

public interface Device {
    //List<String> getDeviceList();
    String getDeviceId();
    void callPhoto();
    void callSlowmo();
    void callFocus();
    void openCamera();
    List<String> pullFiles(String source, String target);
    void clearFolder(String path);
}
