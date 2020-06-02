package com.thedeanda.ajaxproxy.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private String id;
    private String url;
    private String method;
    private String path;
    private String headers;
    private byte[] input;
    private String inputText;
    private int status;
    private String reason;
    private long startTime;
    private long duration;
    private String responseHeaders;
    private byte[] output;
    private String outputText;
    private String errorMessage;
    private String contentEncoding;
}
