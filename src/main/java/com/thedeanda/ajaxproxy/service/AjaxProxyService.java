package com.thedeanda.ajaxproxy.service;

import com.thedeanda.ajaxproxy.AjaxProxyServer;
import com.thedeanda.ajaxproxy.api.AjaxProxyStatus;
import com.thedeanda.ajaxproxy.config.ConfigFileService;

public class AjaxProxyService {
    private final ConfigFileService configFileService;
    private final ResourceService resourceService;
    private AjaxProxyServer ajaxProxyServer;
    private Thread apThread;

    public AjaxProxyService(ConfigFileService configFileService, ResourceService resourceService) {
        this.configFileService = configFileService;
        this.resourceService = resourceService;

    }

    public void startServer() throws Exception {
        if (ajaxProxyServer == null) {
            try {
                ajaxProxyServer = new AjaxProxyServer(configFileService.getConfig(),
                        configFileService.getWorkingDirectory(),
                        resourceService);
                apThread = new Thread(ajaxProxyServer);
                apThread.start();
            } catch (Exception e) {
                stopServer();
                throw e;
            }
        }
    }

    public void stopServer() {
        if (ajaxProxyServer != null) {
            ajaxProxyServer.stop();
            apThread.interrupt();
            apThread = null;
            ajaxProxyServer = null;
        }
    }

    public AjaxProxyStatus getStatus() {
        if (ajaxProxyServer != null) {
            return AjaxProxyStatus.builder()
                    .status(AjaxProxyStatus.Status.RUNNING)
                    .build();
        } else {
            return AjaxProxyStatus.builder()
                    .status(AjaxProxyStatus.Status.STOPPED)
                    .build();
        }
    }
}
