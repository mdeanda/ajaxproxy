package com.thedeanda.ajaxproxy.mapper;

import com.thedeanda.ajaxproxy.api.*;
import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigFile;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigLogger;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
import com.thedeanda.ajaxproxy.service.StoredResource;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServerConfigMapper {

    public ServerConfigDto toDto(ServerConfig object);

    public ProxyConfigLoggerDto toDto(ProxyConfigLogger object);

    public ProxyConfigRequestDto toDto(ProxyConfigRequest object);

    public ProxyConfigFileDto toDto(ProxyConfigFile object);

}
