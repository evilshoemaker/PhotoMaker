package com.digios.slowmoserver.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends JMenuBar implements ActionListener {

    interface MainMenuListener {
        void onCommand(String cmd);
    }

    private List<MainMenuListener> listeners = new ArrayList<MainMenuListener>();

    public MainMenu() {
        JMenu jmFile = new JMenu("File");
        JMenuItem jmiExit = new JMenuItem("Exit");
        jmiExit.addActionListener(this);
        jmFile.add(jmiExit);

        add(jmFile);

        JMenu jmSettings = new JMenu("Settings");

        ButtonGroup algorithmGroup = new ButtonGroup();

        JRadioButtonMenuItem jmiAlgorithm1 = new JRadioButtonMenuItem("Algoritm 1");
        jmiAlgorithm1.addActionListener(this);
        algorithmGroup.add(jmiAlgorithm1);
        jmSettings.add(jmiAlgorithm1);

        JRadioButtonMenuItem jmiAlgorithm2 = new JRadioButtonMenuItem("Algoritm 2");
        jmiAlgorithm2.addActionListener(this);
        algorithmGroup.add(jmiAlgorithm2);

        jmSettings.add(jmiAlgorithm2);

        jmSettings.addSeparator();

        JMenuItem jmiSettings = new JMenuItem("Settings");
        jmiSettings.addActionListener(this);
        jmSettings.add(jmiSettings);

        add(jmSettings);
    }

    public void addListener(MainMenuListener toAdd) {
        listeners.add(toAdd);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comStr = e.getActionCommand();

        for (MainMenuListener hl : listeners)
            hl.onCommand(comStr);
    }
}
