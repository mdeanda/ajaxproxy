package com.thedeanda.ajaxproxy.mapper;

import com.thedeanda.ajaxproxy.api.ServerConfigDto;
import com.thedeanda.ajaxproxy.api.VariableDto;
import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import com.thedeanda.ajaxproxy.config.model.Variable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface VariableMapper {
    public VariableDto toDto(Variable object);
    public Variable fromDto(VariableDto object);
}
