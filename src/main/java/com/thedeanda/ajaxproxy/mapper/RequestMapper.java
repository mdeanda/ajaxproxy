package com.thedeanda.ajaxproxy.mapper;

import com.thedeanda.ajaxproxy.api.*;
import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigFile;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigLogger;
import com.thedeanda.ajaxproxy.config.model.proxy.ProxyConfigRequest;
import com.thedeanda.ajaxproxy.service.StoredResource;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface RequestMapper {

    @Mapping(source = "url", target = "path", qualifiedByName = "extractPath")
    public RequestDtoListItem toListItem(StoredResource object);

    @Mapping(source = "url", target = "path", qualifiedByName = "extractPath")
    @Mapping(source = "object", target = "output", qualifiedByName = "output")
    public RequestDto toDto(StoredResource object);

    @Named("extractPath")
    default String extractPath(String url) {
        url = StringUtils.trimToNull(url);

        if (url != null) {
            int i = url.indexOf("//");
            if (i > 0)
                url = url.substring(i+2);

            int q = url.indexOf('?');
            if (q > 0)
                url = url.substring(0, q);
        }

        return url;
    }

    @Named("output")
    default byte[] getOutput(StoredResource storedResource) {
        if (storedResource.getOutputDecompressed() != null)
            return storedResource.getOutputDecompressed();
        else
            return storedResource.getOutput();
    }

}
