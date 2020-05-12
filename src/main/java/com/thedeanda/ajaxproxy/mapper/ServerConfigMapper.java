package com.thedeanda.ajaxproxy.mapper;

import com.thedeanda.ajaxproxy.api.*;
import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigFile;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigLogger;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

//@Service
//@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Mapper
public interface ServerConfigMapper {
    ServerConfigMapper INSTANCE = Mappers.getMapper( ServerConfigMapper.class );


    public ServerConfigDto toDto(ServerConfig input);

    public ProxyConfigLoggerDto toDto(ProxyConfigLogger config);

    public ProxyConfigRequestDto toDto(ProxyConfigRequest config);

    public ProxyConfigFileDto toDto(ProxyConfigFile config);

}
