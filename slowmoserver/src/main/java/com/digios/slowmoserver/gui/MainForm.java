package com.digios.slowmoserver.gui;

import com.digios.slowmoserver.device.AdbDevice;
import com.digios.slowmoserver.nework.NetworkUserDataSender;
import com.digios.slowmoserver.photomaker.PhotoMaker;
import com.digios.slowmoserver.photomaker.PhotoMakerAlgoritm1;
import com.digios.slowmoserver.photomaker.PhotoMakerAlgoritm2;
import com.digios.slowmoserver.photomaker.PhotoMakerType;
import com.digios.slowmoserver.websocketserver.WebSocketServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainForm extends JFrame {
    final static Logger logger = Logger.getLogger(MainForm.class);

    private JTabbedPane tabbedPane1;
    private JButton refreshListButton;
    private JTextArea deviceList;
    private JPanel rootPanel;
    private JLabel algorithmLabel;
    private JTextArea socketMessageTextArea;
    private JButton button1;
    private JTextArea createdFileTextArea;
    private MainMenu mainMenu;

    WebSocketServer server;
    NetworkUserDataSender networkUserDataSender;
    PhotoMaker photoMaker;
    Thread photoMakerThread;
    PhotoMakerType currentPhotoMakerType = PhotoMakerType.ALGORITHM1;

    public MainForm() {

        try {
            server = new WebSocketServer(8887);
            server.start();
        }
        catch (Exception ex) {
            logger.error(ex);
        }

        try {
            networkUserDataSender = new NetworkUserDataSender();
            networkUserDataSender.start();
        }
        catch (Exception ex) {
            logger.error(ex);
        }

        server.addListener(new WebSocketServer.WebSocketServerListener() {
            @Override
            public void onShot() {

                try {
                    if (photoMakerThread != null) {
                        if (photoMakerThread.isAlive()) {
                            return;
                        } else {
                            photoMakerThread = null;
                        }
                    }

                    photoMakerThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (photoMaker != null) {
                                try {
                                    photoMaker.execute();
                                } catch (Exception e) {
                                    logger.error(e);
                                }
                            }
                        }
                    });

                    photoMakerThread.start();
                }
                catch (Exception ex) {
                    logger.error(ex);
                }

            }

            @Override
            public void onReady() {
                try {
                    photoMaker.ready();
                } catch (Exception e) {
                    logger.error(e);
                }
            }

            @Override
            public void onMessage(String message) {
                socketMessageTextArea.append(message + "\n");
            }
        });

        refreshListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> devices = AdbDevice.getDeviceList();

                deviceList.setText("");
                devices.forEach((deviceId) -> {
                    deviceList.append(deviceId);
                });
            }
        });

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                logger.info(photoMakerThread.isAlive());
            }
        });

        mainMenu = new MainMenu();
        mainMenu.addListener(new MainMenu.MainMenuListener() {
            @Override
            public void onCommand(String cmd) {
                if (cmd.equals("Exit")) {
                    System.exit(0);
                }
                else if (cmd.equals("Algoritm 1")) {
                    currentPhotoMakerType = PhotoMakerType.ALGORITHM1;
                    algorithmLabel.setText(cmd);
                    createPhotoMaker();
                }
                else if (cmd.equals("Algoritm 2")) {
                    currentPhotoMakerType = PhotoMakerType.ALGORITHM2;
                    algorithmLabel.setText(cmd);
                    createPhotoMaker();
                }
            }
        });
        setJMenuBar(mainMenu);

        algorithmLabel.setText("Algoritm 1");
        createPhotoMaker();

        setSize(480, 640);
        setContentPane(rootPanel);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void createPhotoMaker() {
        if (currentPhotoMakerType == PhotoMakerType.ALGORITHM1) {
            photoMaker = new PhotoMakerAlgoritm1();
        }
        else if (currentPhotoMakerType == PhotoMakerType.ALGORITHM2) {
            photoMaker = new PhotoMakerAlgoritm2();
        }

        photoMaker.addListener(new PhotoMaker.PhotoMakerListener() {
            @Override
            public void onResult(String message) {
                createdFileTextArea.append(message + "\n");

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("cmd", "result");
                map.put("path", message);

                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .create();

                String json = gson.toJson(map);
                server.sendBroadcast(json);
            }
        });
    }
}
