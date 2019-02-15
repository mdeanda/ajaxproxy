package com.thedeanda.ajaxproxy.ui.help;

import javax.swing.*;
import java.awt.*;

public class HelpAbout extends JDialog {
    public HelpAbout(JFrame frame) {
        super(frame, true);
        setTitle("About Ajax Proxy");
        setLayout(new BorderLayout());
        add(new JButton("help"), BorderLayout.CENTER);
    }
}
