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
    private Collection<ConfigChangedListener> listeners = new HashSet<>();
    private Config config;
    private File workingDir;
    private File configFile;

    public void addListener(ConfigChangedListener listener) {
        listeners.add(listener);
    }

    public void newConfig() {
        this.workingDir = new File(".");
        configFile = null;
        configLoaded(Config.builder()
                .build());
    }

    public Config loadConfig(JsonObject config, File workingDir) {
        ConfigLoader cl = new ConfigLoader();
        Config configObject = cl.loadConfig(config, workingDir);
        this.workingDir = workingDir;
        configFile = null;

        configLoaded(configObject);

        return configObject;
    }

    private void configLoaded(Config config) {
        this.config = config;
        //notify listeners
        listeners.forEach(l -> {
            try {
                l.configChanged(config);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        });
    }
}
