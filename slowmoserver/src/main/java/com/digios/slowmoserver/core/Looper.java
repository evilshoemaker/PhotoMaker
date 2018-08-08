package com.digios.slowmoserver.core;

public class Looper {
    public static void loop() {
        try {
            Object obj = new Object();
            synchronized (obj) {
                obj.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
