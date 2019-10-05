package com.thedeanda.ajaxproxy.ui.serverconfig;

import com.thedeanda.ajaxproxy.AjaxProxyServer;
import com.thedeanda.ajaxproxy.ProxyListener;
import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import com.thedeanda.ajaxproxy.ui.MainPanel;
import com.thedeanda.ajaxproxy.ui.SettingsChangedListener;
import com.thedeanda.ajaxproxy.ui.util.SwingUtils;
import com.thedeanda.ajaxproxy.ui.serverconfig.merge.MergePanel;
import com.thedeanda.ajaxproxy.ui.serverconfig.merge.MergeTableModel;
import com.thedeanda.ajaxproxy.ui.serverconfig.proxy.ProxyPanel;
import com.thedeanda.ajaxproxy.ui.serverconfig.proxy.ProxyTableModel;
import com.thedeanda.ajaxproxy.ui.serverconfig.tamper.TamperPanel;
import com.thedeanda.ajaxproxy.ui.variable.VariablesPanel;
import com.thedeanda.javajson.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ServerConfigPanel extends JPanel implements SettingsChangedListener, ProxyListener {
    private final SettingsChangedListener settingsChangedListener;
    private final MainPanel mainPanel;
    private ProxyTableModel proxyModel;
    private MergeTableModel mergeModel;
    private GeneralPanel generalPanel;
    private TamperPanel tamperPanel;
    private JButton startButton;
    private JButton stopButton;

    public ServerConfigPanel(SettingsChangedListener settingsChangedListener, MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        JPanel tabsPanel = this;
        JTabbedPane tabs = new JTabbedPane();
        this.settingsChangedListener = settingsChangedListener;

        tabsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabsPanel.setLayout(new BorderLayout());
        tabsPanel.add(tabs, BorderLayout.CENTER);
        tabsPanel.add(initTopBar(), BorderLayout.NORTH);

        generalPanel = new GeneralPanel(this);
        tabs.add("General", generalPanel);


        proxyModel = new ProxyTableModel();
        tabs.add("Proxy", new ProxyPanel(this, proxyModel));

        mergeModel = new MergeTableModel();
        tabs.add("Merge", new MergePanel(this, mergeModel));

        tamperPanel = new TamperPanel();
        // tabs.add("Tamper", tamperPanel);

    }

    private JPanel initTopBar() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.TRAILING));

        startButton = SwingUtils.newJButton("Start");
        panel.add(startButton);
        startButton.addActionListener((a) -> {
            mainPanel.start();
        });

        stopButton = SwingUtils.newJButton("Stop");
        panel.add(stopButton);
        stopButton.addActionListener((a) -> {
            mainPanel.stop();
        });

        return panel;
    }

    /**
     * adds variables from command line
     * @param vars
     */
    public void addVariables(Map<String, String> vars) {
//        variablePanel.setVariables(vars);
    }

    public void setConfig(JsonObject json, Config config) {
        //only show first server for now
        ServerConfig serverConfig = config.getServers().stream().findFirst().get();

        //TODO: add drop down or something to change which server config is being viewed/editted

        proxyModel.setConfig(json.getJsonArray("proxy"), serverConfig);
        mergeModel.setConfig(json.getJsonArray("merge"), serverConfig);
        generalPanel.setConfig(json);
        //tamperPanel.setConfig(json.getJsonObject("tamper"));
    }

    public void updateConfig(JsonObject config) {
        config.put("proxy", proxyModel.getConfig(generalPanel.getCacheTime()));
        config.put("merge", mergeModel.getConfig());
        //config.put("tamper", tamperPanel.getConfig());
        generalPanel.updateConfig(config);
    }

    public void setProxy(AjaxProxyServer proxy) {
        generalPanel.setProxy(proxy);
    }

    public void clearAll() {
        generalPanel.setPort(0);
        generalPanel.setResourceBase("");
        generalPanel.setShowIndex(false);
        proxyModel.clear();
        mergeModel.clear();
    }

    @Override
    public void settingsChanged() {
        settingsChangedListener.settingsChanged();
    }

    @Override
    public void restartRequired() {
        settingsChangedListener.restartRequired();
    }

    @Override
    public void started() {
        stopButton.setEnabled(true);
        startButton.setEnabled(false);
    }

    @Override
    public void stopped() {
        stopButton.setEnabled(false);
        startButton.setEnabled(true);
    }

    @Override
    public void failed() {
        stopButton.setEnabled(false);
        startButton.setEnabled(true);
    }
}
