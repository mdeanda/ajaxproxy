package com.thedeanda.ajaxproxy.config;

import com.thedeanda.ajaxproxy.config.model.Config;
import com.thedeanda.ajaxproxy.config.model.ConfigChangeListener;
import com.thedeanda.javajson.JsonException;
import com.thedeanda.javajson.JsonObject;
import lombok.Getter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * config service will manage loading, saving and updates to a single config file
 */
public class ConfigFileService {
    private List<ConfigChangeListener> listeners = new ArrayList<>();

    @Getter
    private Config config;

    public ConfigFileService() {

    }

    public ConfigFileService(File configFile) throws IOException, JsonException {
        loadConfigFile(configFile);
    }

    public void addConfigChangeListener(ConfigChangeListener listener) {
        listeners.add(listener);
    }

    private void configChanged() {
        listeners.stream().forEach(l -> l.configChanged(config));
    }
    
    public void loadConfigFile(File configFile) throws IOException, JsonException {
        Config co;
        try (InputStream is = new FileInputStream(configFile)) {
            JsonObject json = JsonObject.parse(is);

            ConfigLoader cl = new ConfigLoader();
            co = cl.loadConfig(json, configFile.getAbsoluteFile().getParentFile());
        }

        this.config = co;
        configChanged();
    }
}
