package com.digios.slowmoserver;

import java.util.ArrayList;
import java.util.List;

public class PhotoMakerAlgoritm1 implements PhotoMaker {

    List<Device> photoDevicesList = new ArrayList<>();
    Device slowmoDevice = null;

    private final long VIDEO_DELAY = Config.INSTANCE.videoDelay();
    private final long WAIT = Config.INSTANCE.waitTime();

    public PhotoMakerAlgoritm1() {
        List<String> list = Config.INSTANCE.photoDevices();
        for (String d : list) {
            photoDevicesList.add(new AdbDevice(d));
        }

        if (!Config.INSTANCE.slowmoDevices().isEmpty())
            slowmoDevice = new AdbDevice(Config.INSTANCE.slowmoDevices().get(0));
    }

    @Override
    public void excecute() throws InterruptedException {
        slowmoDevice.callSlowmo();

        Thread.sleep(VIDEO_DELAY);

        for (Device d : photoDevicesList) {
            d.callPhoto();
        }

        Thread.sleep(WAIT);


    }

    private void getMediaFile() {

    }

    private void makeVideo() {

    }

    private void sendResult() {

    }
}
