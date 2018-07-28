package com.digios.slowmoserver;

import java.util.List;

public interface Device {
    List<String> getDeviceList();
    void callPhoto(String deviceId);
    void callSlowmo(String deviceId);
    void pullFile(String deviceId, String source, String target);
    void clearFolder(String deviceId, String path);
}
