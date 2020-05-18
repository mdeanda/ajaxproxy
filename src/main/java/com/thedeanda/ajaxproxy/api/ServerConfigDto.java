package com.thedeanda.ajaxproxy.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerConfigDto {
    private int id;
    private IntVariableDto port;
    private StringVariableDto resourceBase;
    private boolean showIndex;

    private String[] baseUrls;
}
