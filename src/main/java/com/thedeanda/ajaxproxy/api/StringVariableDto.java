package com.thedeanda.ajaxproxy.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StringVariableDto {
    private String originalValue;
    private String value;
}
