package com.thedeanda.ajaxproxy.ui.help;

import com.thedeanda.ajaxproxy.ui.ConfigService;
import com.thedeanda.ajaxproxy.ui.help.update.ReleaseEntry;
import com.thedeanda.ajaxproxy.ui.help.update.ReleaseVersion;
import com.thedeanda.ajaxproxy.ui.help.update.UpdateCheckWorker;
import com.thedeanda.ajaxproxy.ui.util.SwingUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.concurrent.ExecutionException;

@Slf4j
public class HelpUpdates extends JDialog {
    private JProgressBar progressBar;
    private JButton viewReleasesButton;
    private JLabel lblCurrent;
    private JLabel lblLatest;
    private JLabel lblCurrentValue;
    private JLabel lblLatestValue;
    private JLabel lblIntroText;
    private JTextField txtLink;

    public HelpUpdates(JFrame frame) {
        super(frame, true);
        this.setTitle("Check for Updates");
        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        add(panel, BorderLayout.CENTER);

        initComponents(panel);
        initLayout(layout, panel);
        setMinimumSize(new Dimension(450, 305));
        setResizable(false);

        log.debug("checking for udpates");
        UpdateCheckWorker worker = new UpdateCheckWorker() {
            @Override
            protected void done() {
                try {
                    ReleaseEntry entry = get();
                    log.debug("ok, done {}", entry);
                    progressBar.setIndeterminate(false);
                    progressBar.setMaximum(1);
                    progressBar.setValue(1);

                    lblLatestValue.setText(entry.getVersion().toString());

                    String vs = ConfigService.get().getVersionString();
                    ReleaseVersion version = new ReleaseVersion(vs);
                    if (version.compareTo(entry.getVersion()) < 0) {
                        lblLatestValue.setForeground(new Color(52, 164, 52));
                    }

                } catch (InterruptedException  | ExecutionException e) {
                    log.warn(e.getMessage(), e);
                }
            }
        };
        worker.execute();
    }

    private void initLayout(SpringLayout layout, Container panel) {
        //*
        layout.putConstraint(SpringLayout.WEST, lblIntroText, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.EAST, lblIntroText, -20, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.NORTH, lblIntroText, 30, SpringLayout.NORTH, panel);
        //layout.putConstraint(SpringLayout.SOUTH, lblIntroText, 70, SpringLayout.NORTH, panel);
        //*/

        layout.putConstraint(SpringLayout.NORTH, progressBar, 30, SpringLayout.SOUTH, lblIntroText);
        layout.putConstraint(SpringLayout.WEST, progressBar, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.EAST, progressBar, -20, SpringLayout.EAST,panel);
        layout.putConstraint(SpringLayout.SOUTH, progressBar, 15, SpringLayout.NORTH, progressBar);

        layout.putConstraint(SpringLayout.NORTH, lblCurrent, 30, SpringLayout.SOUTH, progressBar);
        layout.putConstraint(SpringLayout.WEST, lblCurrent, 20, SpringLayout.WEST, panel);

        layout.putConstraint(SpringLayout.BASELINE, lblCurrentValue, 0, SpringLayout.BASELINE, lblCurrent);
        layout.putConstraint(SpringLayout.WEST, lblCurrentValue, 20, SpringLayout.EAST, lblCurrent);

        layout.putConstraint(SpringLayout.NORTH, lblLatest, 10, SpringLayout.SOUTH, lblCurrent);
        layout.putConstraint(SpringLayout.WEST, lblLatest, 20, SpringLayout.WEST, panel);

        layout.putConstraint(SpringLayout.BASELINE, lblLatestValue, 0, SpringLayout.BASELINE, lblLatest);
        layout.putConstraint(SpringLayout.WEST, lblLatestValue, 0, SpringLayout.WEST, lblCurrentValue);

        layout.putConstraint(SpringLayout.NORTH, txtLink, 15, SpringLayout.SOUTH, lblLatest);
        layout.putConstraint(SpringLayout.WEST, txtLink, 20, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.EAST, txtLink, -20, SpringLayout.EAST, panel);


        layout.putConstraint(SpringLayout.SOUTH, viewReleasesButton, -20, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, viewReleasesButton, 0, SpringLayout.HORIZONTAL_CENTER, panel);

    }

    private void initComponents(JPanel panel) {
        lblIntroText = new JLabel("Ajax Proxy");
        lblIntroText.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblIntroText);

        progressBar = new JProgressBar();
        //when the task of (initially) unknown length begins:
        progressBar.setIndeterminate(true);
        panel.add(progressBar);

        lblCurrent = new JLabel("Current Version");
        lblLatest = new JLabel("Latest Release");
        lblCurrentValue = new JLabel(ConfigService.get().getVersionString());
        lblLatestValue = new JLabel("");

        panel.add(lblCurrent);
        panel.add(lblLatest);
        panel.add(lblCurrentValue);
        panel.add(lblLatestValue);

        viewReleasesButton = SwingUtils.newJButton("View Releases");
        panel.add(viewReleasesButton);
        viewReleasesButton.addActionListener(el -> this.openReleasesPage());

        txtLink = SwingUtils.newJTextField();
        txtLink.setText(UpdateCheckWorker.RELEASE_URL);
        txtLink.setEditable(false);
        panel.add(txtLink);

        //*/

    }

    private void openReleasesPage() {
        try {
            URI url = new URI(UpdateCheckWorker.RELEASE_URL);
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(url);
                this.setVisible(false);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }
}
