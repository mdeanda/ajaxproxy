package com.thedeanda.ajaxproxy.config;

import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.ajaxproxy.config.model.ConfigChangedListener;
import com.thedeanda.javajson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

@Slf4j
public class ConfigService {
    private static Logger log = LoggerFactory.getLogger(ConfigService.class);
    private Collection<ConfigChangedListener> listeners = new HashSet<>();

    public void addListener(ConfigChangedListener listener) {
        listeners.add(listener);
    }

    public Config loadConfig(JsonObject config, File workingDir) {
        ConfigLoader cl = new ConfigLoader();
        Config configObject = cl.loadConfig(config, workingDir);

        //notify listeners
        listeners.forEach(l -> {
            try {
                l.configChanged(configObject);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        });

        return configObject;
    }
}
