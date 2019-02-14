package com.thedeanda.ajaxproxy.ui.help;

import com.thedeanda.ajaxproxy.ui.update.ReleaseEntry;
import com.thedeanda.ajaxproxy.ui.update.UpdateCheckWorker;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.concurrent.ExecutionException;

@Slf4j
public class HelpUpdates extends JDialog {
    private JProgressBar progressBar;

    public HelpUpdates(JFrame frame) {
        super(frame, true);
        this.setTitle("Check for Updates");
        setLayout(new BorderLayout());

        initComponents();

        log.debug("checking for udpates");
        UpdateCheckWorker worker = new UpdateCheckWorker() {
            @Override
            protected void done() {
                try {
                    ReleaseEntry entry = get();
                    log.debug("ok, done {}", entry);
                    progressBar.setIndeterminate(false);
                } catch (InterruptedException  | ExecutionException e) {
                    log.warn(e.getMessage(), e);
                }
            }
        };
        worker.execute();
    }

    private void initComponents() {
        JButton btn = new JButton("View Releases");
        add(btn, BorderLayout.NORTH);
        btn.addActionListener(el -> this.openReleasesPage());

        progressBar = new JProgressBar();
        //when the task of (initially) unknown length begins:
        progressBar.setIndeterminate(true);
        add(progressBar, BorderLayout.CENTER);
    }

    private void openReleasesPage() {
        try {
            URI url = new URI(UpdateCheckWorker.RELEASE_URL);
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            this.setVisible(false);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }
}
