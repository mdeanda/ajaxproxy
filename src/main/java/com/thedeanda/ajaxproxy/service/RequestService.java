package com.thedeanda.ajaxproxy.service;

import com.thedeanda.ajaxproxy.api.RequestDto;
import com.thedeanda.ajaxproxy.api.RequestDtoListItem;
import com.thedeanda.ajaxproxy.mapper.RequestMapper;
import com.thedeanda.ajaxproxy.mapper.ServerConfigMapper;

import java.util.List;
import java.util.stream.Collectors;

public class RequestService {
    private final ResourceService resourceService;
    private final RequestMapper mapper;

    public RequestService(ResourceService resourceService, RequestMapper mapper) {
        this.resourceService = resourceService;
        this.mapper = mapper;
    }

    public List<RequestDtoListItem> find() {
        return resourceService.find()
                .stream()
                .map(mapper::toListItem)
                .collect(Collectors.toList());

    }

    public RequestDto get(String id) {
        StoredResource resource = resourceService.get(id);
        if (resource != null) {
            return mapper.toDto(resource);
        }

        return null;
    }
}