package com.thedeanda.ajaxproxy.service;

import com.thedeanda.ajaxproxy.api.ProxyConfigDto;
import com.thedeanda.ajaxproxy.api.ServerConfigDto;
import com.thedeanda.ajaxproxy.config.ConfigFileService;
import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import com.thedeanda.ajaxproxy.mapper.ServerConfigMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServerConfigService {

    private final ConfigFileService configFileService;
    private final ServerConfigMapper serverConfigMapper;

    public ServerConfigService(ConfigFileService configFileService, ServerConfigMapper serverConfigMapper) {
        this.configFileService = configFileService;
        this.serverConfigMapper = serverConfigMapper;
    }

    public List<ServerConfigDto> list(){
        Config config = configFileService.getConfig();

        return config.getServers().stream()
                .map(serverConfigMapper::toDto)
                .collect(Collectors.toList());
    }

    public ServerConfigDto get(int id) {
        return getServer(id)
                .map(serverConfigMapper::toDto)
                .orElse(null);
    }

    private Optional<ServerConfig> getServer(int id) {
        Config config = configFileService.getConfig();

        return config.getServers().stream()
                .filter(sc -> sc.getId() == id)
                .findFirst();
    }

    public List<ProxyConfigDto> listProxies(int serverId) {
        getServer(serverId)
                .map(ServerConfig::getProxyConfig);

        return null;
    }

}
