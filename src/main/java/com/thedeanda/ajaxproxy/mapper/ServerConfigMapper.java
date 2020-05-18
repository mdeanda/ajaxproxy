package com.thedeanda.ajaxproxy.mapper;

import com.thedeanda.ajaxproxy.api.*;
import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigFile;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigLogger;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
import com.thedeanda.ajaxproxy.service.StoredResource;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ServerConfigMapper {

    @Mapping(source = "object", target = "baseUrls", qualifiedByName = "addBaseUrls")
    public ServerConfigDto toDto(ServerConfig object);

    public ProxyConfigLoggerDto toDto(ProxyConfigLogger object);

    public ProxyConfigRequestDto toDto(ProxyConfigRequest object);

    public ProxyConfigFileDto toDto(ProxyConfigFile object);

    @Named("addBaseUrls")
    default String[] addBaseUrls(ServerConfig object) {
        String url = "http://localhost:" + object.getPort().getValue();

        //TODO: add more when we add https
        return new String[]{url};
    }

}
