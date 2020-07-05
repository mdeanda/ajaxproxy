package com.thedeanda.ajaxproxy.mapper;

import com.thedeanda.ajaxproxy.api.*;
import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigFile;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigLogger;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper
public interface ServerConfigMapper {

    @Mapping(source = "object", target = "baseUrls", qualifiedByName = "addBaseUrls")
    public ServerConfigDto toDto(ServerConfig object);

    @Mapping(source = "object", target = "type", qualifiedByName = "addProxyType")
    public ProxyConfigLoggerDto toDto(ProxyConfigLogger object);

    @Mapping(source = "object", target = "type", qualifiedByName = "addProxyType")
    public ProxyConfigRequestDto toDto(ProxyConfigRequest object);

    @Mapping(source = "object", target = "type", qualifiedByName = "addProxyType")
    public ProxyConfigFileDto toDto(ProxyConfigFile object);

    public void merge(ProxyConfigRequestDto dto, @MappingTarget ProxyConfigRequest target);

    @Named("addBaseUrls")
    default String[] addBaseUrls(ServerConfig object) {
        String url = "http://localhost:" + object.getPort().getValue();

        //TODO: add more when we add https
        return new String[]{url};
    }

    @Named("addProxyType")
    default ProxyConfigDto.ProxyType addProxyType(ProxyConfig config) {
        if (config instanceof ProxyConfigFile) {
            return ProxyConfigDto.ProxyType.File;
        } else if (config instanceof ProxyConfigRequest) {
            return ProxyConfigDto.ProxyType.Request;
        } else {
            return ProxyConfigDto.ProxyType.Logger;
        }
    }

}
