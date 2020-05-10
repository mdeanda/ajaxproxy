package com.thedeanda.ajaxproxy.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServerConfig {
    private Long id;
    private String name;
    private String description;
}
