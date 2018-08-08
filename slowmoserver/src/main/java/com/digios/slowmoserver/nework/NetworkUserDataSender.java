package com.digios.slowmoserver.nework;

import com.digios.slowmoserver.core.Looper;
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
                    logger.info("timer");
                }
                catch (Exception ex) {
                    logger.error(ex);
                }
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(sendTimerTask, 3000, 1000);

        Looper.loop();
    }
}
