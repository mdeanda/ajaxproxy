package com.thedeanda.ajaxproxy.service;

import com.thedeanda.ajaxproxy.AjaxProxyServer;
import com.thedeanda.ajaxproxy.config.ConfigFileService;

public class AjaxProxyService {
    private final ConfigFileService configFileService;
    private AjaxProxyServer ajaxProxyServer;
    private Thread apThread;

    public AjaxProxyService(ConfigFileService configFileService) {
        this.configFileService = configFileService;
    }

    public void startServer() throws Exception {
        ajaxProxyServer = new AjaxProxyServer(configFileService.getConfig(), configFileService.getWorkingDirectory(), null);
        apThread = new Thread(ajaxProxyServer);
        apThread.start();
    }

    public void stopServer() {
        if (ajaxProxyServer != null) {
            ajaxProxyServer.stop();
            apThread.interrupt();
            apThread = null;
        }
    }
}
