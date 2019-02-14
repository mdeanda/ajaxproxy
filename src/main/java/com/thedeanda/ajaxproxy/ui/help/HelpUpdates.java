package com.thedeanda.ajaxproxy.ui.help;

import javax.swing.*;
import java.awt.*;

public class HelpUpdates extends JDialog {
    public HelpUpdates(JFrame frame) {
        super(frame, true);
        this.setTitle("Check for Updates");
        setLayout(new BorderLayout());
        add(new JButton("updates"), BorderLayout.CENTER);
    }
}
