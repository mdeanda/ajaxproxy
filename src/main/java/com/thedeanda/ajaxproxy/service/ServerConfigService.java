package com.thedeanda.ajaxproxy.service;

import com.thedeanda.ajaxproxy.core.ServerConfig;
import com.thedeanda.ajaxproxy.db.ServerConfigDao;

import java.util.List;

public class ServerConfigService {
    private ServerConfigDao serverConfigDao;

    public ServerConfigService(ServerConfigDao serverConfigDao) {
        this.serverConfigDao = serverConfigDao;
    }

    public List<ServerConfig> list() {
        return null;
    }

    public ServerConfig save(ServerConfig serverConfig) {
        Long id = serverConfigDao.insert(serverConfig.getName(), serverConfig.getDescription());
        return serverConfig;
    }
}
