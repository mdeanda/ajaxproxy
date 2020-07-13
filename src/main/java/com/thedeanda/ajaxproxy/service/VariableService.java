package com.thedeanda.ajaxproxy.service;

import com.thedeanda.ajaxproxy.api.VariableDto;
import com.thedeanda.ajaxproxy.config.ConfigFileService;
import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.ajaxproxy.config.model.Variable;
import com.thedeanda.ajaxproxy.mapper.VariableMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VariableService {
    final ConfigFileService configFileService;
    final VariableMapper variableMapper;

    public VariableService(ConfigFileService configFileService, VariableMapper variableMapper) {
        this.configFileService = configFileService;
        this.variableMapper = variableMapper;
    }

    public List<VariableDto> getVariables() {
        Config config = configFileService.getConfig();
        return config.getVariables().stream()
                .map(variableMapper::toDto)
                .collect(Collectors.toList());
    }

    public void remove(String key) {
        Config config = configFileService.getConfig();
        config.getVariables().remove(key);
    }

    public void update(VariableDto var) {
        Config config = configFileService.getConfig();
        String key = var.getKey();
        config.getVariables().stream()
                .filter(v -> StringUtils.equals(key, v.getKey()))
                .findFirst()
                .ifPresent(v -> v.setValue( var.getValue()));
    }

    public void create(VariableDto var) {
        Config config = configFileService.getConfig();
        String key = var.getKey();
        Optional<Variable> existing = config.getVariables().stream()
                .filter(v -> StringUtils.equals(key, v.getKey()))
                .findAny();

        if (existing.isPresent()) {
            throw new IllegalArgumentException("Variable exists: " + key);
        }

        config.getVariables().add(variableMapper.fromDto(var));
    }
}
