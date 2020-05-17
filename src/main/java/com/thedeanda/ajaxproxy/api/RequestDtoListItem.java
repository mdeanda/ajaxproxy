package com.thedeanda.ajaxproxy.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDtoListItem {
    private String id;
    private String url;
    private String method;
    private String path;
    private int status;
    private long startTime;
    private long duration;
}
