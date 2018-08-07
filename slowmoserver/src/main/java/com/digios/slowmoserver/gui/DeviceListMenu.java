package com.digios.slowmoserver.gui;

import javax.swing.*;

public class DeviceListMenu extends JPopupMenu {

    JMenuItem copyItem;
    public DeviceListMenu(){
        copyItem = new JMenuItem("Copy");
        add(copyItem);
    }
}
