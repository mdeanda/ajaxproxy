package com.thedeanda.ajaxproxy.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AjaxProxyStatus {
    public enum Status { RUNNING, STOPPED }

    private Status status;
}
