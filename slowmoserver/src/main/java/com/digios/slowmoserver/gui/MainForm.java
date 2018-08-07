package com.digios.slowmoserver.gui;

import com.digios.slowmoserver.AdbDevice;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainForm extends JFrame {

    private JTabbedPane tabbedPane1;
    private JButton refreshListButton;
    private JTextArea deviceList;
    private MainMenu mainMenu;

    public MainForm() {

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

        mainMenu = new MainMenu();
        mainMenu.addListener(new MainMenu.MainMenuListener() {
            @Override
            public void onCommand(String cmd) {
                if (cmd.equals("Exit")) {
                    System.exit(0);
                }
            }
        });
        setJMenuBar(mainMenu);

        setSize(480, 640);
        setContentPane(tabbedPane1);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
