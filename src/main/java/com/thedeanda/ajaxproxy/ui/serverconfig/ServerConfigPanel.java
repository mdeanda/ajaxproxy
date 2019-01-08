package com.thedeanda.ajaxproxy.ui.serverconfig;

import com.thedeanda.ajaxproxy.AjaxProxyServer;
import com.thedeanda.ajaxproxy.ui.GeneralPanel;
import com.thedeanda.ajaxproxy.ui.SettingsChangedListener;
import com.thedeanda.ajaxproxy.ui.merge.MergePanel;
import com.thedeanda.ajaxproxy.ui.merge.MergeTableModel;
import com.thedeanda.ajaxproxy.ui.proxy.ProxyPanel;
import com.thedeanda.ajaxproxy.ui.proxy.ProxyTableModel;
import com.thedeanda.ajaxproxy.ui.tamper.TamperPanel;
import com.thedeanda.ajaxproxy.ui.variable.VariablesPanel;
import com.thedeanda.javajson.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ServerConfigPanel extends JPanel implements SettingsChangedListener {
    private final SettingsChangedListener settingsChangedListener;
    private ProxyTableModel proxyModel;
    private MergeTableModel mergeModel;
    private GeneralPanel generalPanel;
    private TamperPanel tamperPanel;
    private VariablesPanel variablePanel;

    public ServerConfigPanel(SettingsChangedListener settingsChangedListener) {
        JPanel tabsPanel = this;
        JTabbedPane tabs = new JTabbedPane();
        this.settingsChangedListener = settingsChangedListener;

        tabsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabsPanel.setLayout(new BorderLayout());
        tabsPanel.add(tabs, BorderLayout.CENTER);

        generalPanel = new GeneralPanel(this);
        tabs.add("General", generalPanel);

        proxyModel = new ProxyTableModel();
        tabs.add("Proxy", new ProxyPanel(this, proxyModel));

        mergeModel = new MergeTableModel();
        tabs.add("Merge", new MergePanel(this, mergeModel));

        // TODO: move proxy to its own panel so code is easier to maintain
        variablePanel = new VariablesPanel(this);
        tabs.add("Variables", variablePanel);

        tamperPanel = new TamperPanel();
        // tabs.add("Tamper", tamperPanel);

    }

    /**
     * adds variables from command line
     * @param vars
     */
    public void addVariables(Map<String, String> vars) {
        variablePanel.setVariables(vars);
    }

    public void setConfig(JsonObject config) {
        proxyModel.setConfig(config.getJsonArray("proxy"));
        mergeModel.setConfig(config.getJsonArray("merge"));
        variablePanel.setConfig(config.getJsonObject("variables"));
        generalPanel.setConfig(config);
        tamperPanel.setConfig(config.getJsonObject("tamper"));
    }

    public void updateConfig(JsonObject config) {
        config.put("proxy", proxyModel.getConfig(generalPanel.getCacheTime()));
        config.put("merge", mergeModel.getConfig());
        config.put("variables", variablePanel.getConfig());
        config.put("tamper", tamperPanel.getConfig());
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
        variablePanel.clear();
    }

    @Override
    public void settingsChanged() {
        settingsChangedListener.settingsChanged();
    }

    @Override
    public void restartRequired() {
        settingsChangedListener.restartRequired();
    }
}
