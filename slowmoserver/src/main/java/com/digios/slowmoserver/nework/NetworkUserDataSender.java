package com.digios.slowmoserver.nework;

import com.digios.slowmoserver.core.Config;
import com.digios.slowmoserver.core.Looper;
import com.digios.slowmoserver.database.DataBase;
import com.digios.slowmoserver.database.UserDataForSend;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkUserDataSender extends Thread {
    final static Logger logger = Logger.getLogger(NetworkUserDataSender.class);

    private final String HOST = Config.INSTANCE.saveUrl();

    @Override
    public void run() {
        TimerTask sendTimerTask = new TimerTask(){

            public void run() {
                try {
                    List<UserDataForSend> dataList = DataBase.getDataForSend();

                    for (UserDataForSend data : dataList) {
                        if (Network.sendData(HOST, data.toMap())) {
                            DataBase.updateSendSatus(data.getId(), true);
                        }

                        Thread.sleep(100);
                    }

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
