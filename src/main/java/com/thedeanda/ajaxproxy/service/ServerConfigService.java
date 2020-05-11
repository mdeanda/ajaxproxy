package com.thedeanda.ajaxproxy.service;

import com.thedeanda.ajaxproxy.core.ServerConfig;

import java.util.List;

public class ServerConfigService {

    public ServerConfigService() {
    }

    public List<ServerConfig> list() {
        return null;
    }

    public ServerConfig save(ServerConfig serverConfig) {
        //Long id = serverConfigDao.insert(serverConfig.getName(), serverConfig.getDescription());
        return serverConfig;
    }
}
