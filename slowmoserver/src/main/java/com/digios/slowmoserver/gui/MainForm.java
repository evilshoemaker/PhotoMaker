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
import java.awt.*;
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
        } catch (Exception ex) {
            logger.error(ex);
        }

        try {
            networkUserDataSender = new NetworkUserDataSender();
            networkUserDataSender.start();
        } catch (Exception ex) {
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
                } catch (Exception ex) {
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
                } else if (cmd.equals("Algoritm 1")) {
                    currentPhotoMakerType = PhotoMakerType.ALGORITHM1;
                    algorithmLabel.setText(cmd);
                    createPhotoMaker();
                } else if (cmd.equals("Algoritm 2")) {
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
        } else if (currentPhotoMakerType == PhotoMakerType.ALGORITHM2) {
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Algorithm:");
        panel1.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        algorithmLabel = new JLabel();
        algorithmLabel.setText("Label");
        panel1.add(algorithmLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Label");
        panel1.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        button1 = new JButton();
        button1.setText("Button");
        panel1.add(button1, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setTabPlacement(1);
        panel2.add(tabbedPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Device list", panel3);
        final JToolBar toolBar1 = new JToolBar();
        panel3.add(toolBar1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        refreshListButton = new JButton();
        refreshListButton.setText("Refresh list");
        toolBar1.add(refreshListButton);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel3.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        deviceList = new JTextArea();
        deviceList.setEditable(false);
        deviceList.setEnabled(true);
        scrollPane1.setViewportView(deviceList);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Web-socket commands", panel4);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel4.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        socketMessageTextArea = new JTextArea();
        socketMessageTextArea.setEditable(false);
        scrollPane2.setViewportView(socketMessageTextArea);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Created files", panel5);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel5.add(scrollPane3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        createdFileTextArea = new JTextArea();
        createdFileTextArea.setEditable(false);
        scrollPane3.setViewportView(createdFileTextArea);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
