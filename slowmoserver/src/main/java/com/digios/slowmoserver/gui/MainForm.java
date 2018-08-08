package com.digios.slowmoserver.gui;

import com.digios.slowmoserver.device.AdbDevice;
import com.digios.slowmoserver.nework.NetworkUserDataSender;
import com.digios.slowmoserver.photomaker.PhotoMaker;
import com.digios.slowmoserver.photomaker.PhotoMakerAlgoritm1;
import com.digios.slowmoserver.photomaker.PhotoMakerAlgoritm2;
import com.digios.slowmoserver.photomaker.PhotoMakerType;
import com.digios.slowmoserver.websocketserver.WebSocketServer;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainForm extends JFrame {
    final static Logger logger = Logger.getLogger(MainForm.class);

    private JTabbedPane tabbedPane1;
    private JButton refreshListButton;
    private JTextArea deviceList;
    private JPanel rootPanel;
    private JLabel algorithmLabel;
    private JTextArea socketMessageTextArea;
    private JButton button1;
    private MainMenu mainMenu;

    WebSocketServer server;
    NetworkUserDataSender networkUserDataSender;
    PhotoMaker photoMaker;
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
                    if (photoMaker != null) {
                        if (photoMaker.isAlive()) {
                            return;
                        } else {
                            photoMaker = null;
                        }
                    }

                    if (currentPhotoMakerType == PhotoMakerType.ALGORITHM1) {
                        photoMaker = new PhotoMakerAlgoritm1();
                    } else if (currentPhotoMakerType == PhotoMakerType.ALGORITHM1) {
                        photoMaker = new PhotoMakerAlgoritm1();
                    }

                    photoMaker.start();
                }
                catch (Exception ex) {
                    logger.error(ex);
                }
                
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
                logger.info(photoMaker.isAlive());
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
                }
                else if (cmd.equals("Algoritm 2")) {
                    currentPhotoMakerType = PhotoMakerType.ALGORITHM2;
                    algorithmLabel.setText(cmd);
                }
            }
        });
        setJMenuBar(mainMenu);

        algorithmLabel.setText("Algoritm 1");

        setSize(480, 640);
        setContentPane(rootPanel);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
