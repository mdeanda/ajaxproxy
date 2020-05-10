package com.thedeanda.ajaxproxy.mapper;

import com.thedeanda.ajaxproxy.api.ServerConfigDto;
import com.thedeanda.ajaxproxy.core.ServerConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

//@Service
//@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Mapper
public interface ServerConfigMapper {
    ServerConfigMapper INSTANCE = Mappers.getMapper( ServerConfigMapper.class );


    public ServerConfig fromDto(ServerConfigDto dto);
}
