package com.thedeanda.ajaxproxy.health;

import com.codahale.metrics.health.HealthCheck;

public class SampleHealthCheck extends HealthCheck {
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
