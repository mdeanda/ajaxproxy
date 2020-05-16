package com.thedeanda.ajaxproxy.service;

import com.thedeanda.ajaxproxy.api.ProxyConfigDto;
import com.thedeanda.ajaxproxy.api.ServerConfigDto;
import com.thedeanda.ajaxproxy.config.ConfigFileService;
import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigFile;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigLogger;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
import com.thedeanda.ajaxproxy.mapper.ServerConfigMapper;

import java.util.*;
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
        List<ProxyConfig> proxies = getServer(serverId).map(ServerConfig::getProxyConfig).orElse(null);

        if (proxies != null) {
            return proxies.stream()
                    .map(this::map)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    public ProxyConfigDto getProxy(int serverId, int proxyId) {
        List<ProxyConfig> proxies = getServer(serverId)
                .map(ServerConfig::getProxyConfig)
                .orElse(null);
        if (proxies != null) {
            return proxies.stream()
                    .filter(p -> p.getId() == proxyId)
                    .map(this::map)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private ProxyConfigDto map(ProxyConfig proxyConfig) {

        if (proxyConfig instanceof ProxyConfigFile) {
            return serverConfigMapper.toDto((ProxyConfigFile) proxyConfig);
        } else if (proxyConfig instanceof ProxyConfigRequest) {
            return serverConfigMapper.toDto((ProxyConfigRequest) proxyConfig);
        } else if (proxyConfig instanceof ProxyConfigLogger) {
            return serverConfigMapper.toDto((ProxyConfigLogger) proxyConfig);
        }

        return null;
    }


    public void update(int id, ServerConfigDto config) {
        ServerConfig server = getServer(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));

        server.getPort().setOriginalValue(config.getPort().getOriginalValue());
        server.getResourceBase().setOriginalValue(config.getResourceBase().getOriginalValue());
        server.setShowIndex(config.isShowIndex());

        //TODO: save
        configFileService.save();
    }
}
