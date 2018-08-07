package com.digios.slowmoserver.websocketserver;

import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

public class NetworkUserDataSender extends Thread {
    final static Logger logger = Logger.getLogger(NetworkUserDataSender.class);

    @Override
    public void run() {
        TimerTask sendTimerTask = new TimerTask(){

            public void run() {
                try {
                    //do the processing
                }
                catch (Exception ex) {}
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(sendTimerTask, 3000, 10);

        while(true) {

        }
    }
}
