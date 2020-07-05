package com.thedeanda.ajaxproxy.mapper;

import com.thedeanda.ajaxproxy.api.RequestDto;
import com.thedeanda.ajaxproxy.api.RequestDtoListItem;
import com.thedeanda.ajaxproxy.service.StoredResource;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.nio.charset.StandardCharsets;

@Mapper
public interface RequestMapper {

    @Mapping(source = "url", target = "path", qualifiedByName = "extractPath")
    public RequestDtoListItem toListItem(StoredResource object);

    @Mapping(source = "url", target = "path", qualifiedByName = "extractPath")
    @Mapping(source = "object", target = "input", qualifiedByName = "input")
    @Mapping(source = "object", target = "inputText", qualifiedByName = "inputText")
    @Mapping(source = "object", target = "output", qualifiedByName = "output")
    @Mapping(source = "object", target = "outputText", qualifiedByName = "outputText")
    public RequestDto toDto(StoredResource object);

    @Named("extractPath")
    default String extractPath(String url) {
        url = StringUtils.trimToNull(url);

        if (url != null) {
            int i = url.indexOf("//");
            if (i > 0)
                url = url.substring(i+2);

            i = url.indexOf("/");
            if (i > 0)
                url = url.substring(i);

            int q = url.indexOf('?');
            if (q > 0)
                url = url.substring(0, q);
        }

        return url;
    }

    @Named("input")
    default byte[] getInput(StoredResource storedResource) {
        return storedResource.getInput();
    }

    @Named("inputText")
    default String getInputText(StoredResource storedResource) {
        byte[] bytes = getInput(storedResource);

        if (bytes == null) return "";

        String output = new String(bytes, StandardCharsets.UTF_8);
        return output;
    }

    @Named("output")
    default byte[] getOutput(StoredResource storedResource) {
        if (storedResource.getOutputDecompressed() != null)
            return storedResource.getOutputDecompressed();
        else
            return storedResource.getOutput();
    }
    @Named("outputText")
    default String getOutputText(StoredResource storedResource) {
        byte[] bytes = getOutput(storedResource);

        if (bytes == null) return "";

        String output = new String(bytes, StandardCharsets.UTF_8);
        return output;
    }
}
