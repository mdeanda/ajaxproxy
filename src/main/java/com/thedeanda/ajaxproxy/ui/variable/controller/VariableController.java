package com.thedeanda.ajaxproxy.ui.variable.controller;

import com.thedeanda.ajaxproxy.ui.SettingsChangedListener;
import com.thedeanda.ajaxproxy.ui.variable.model.Variable;
import com.thedeanda.ajaxproxy.ui.variable.model.VariableModel;
import com.thedeanda.javajson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class VariableController implements SettingsChangedListener {
    private JsonObject config;
    private Collection<SettingsChangedListener> listeners = new HashSet<>();
    private Collection<VariableChangeListener> variableChangeListeners = new HashSet<>();
    private Map<String, String> datanset = new TreeMap<>();

    private VariableModel variableModel;

    public VariableController() {
        this.variableModel = VariableModel.builder().build();

    }

    private void fireSettingsChanged() {
        listeners.forEach(l -> l.settingsChanged());
    }

    //temporary, use custom listener instead maybe
    public void addListener(final SettingsChangedListener listener) {
        this.listeners.add(listener);
    }

    public void setVariableModel(VariableModel variableModel) {
        this.variableModel = variableModel;
        //fire events
        fireSettingsChanged();
    }

    public void clear() {
        this.variableModel = VariableModel.builder().build();
        fireSettingsChanged();
    }

    public void setConfig(JsonObject jsonObject) {
        this.config = jsonObject;
    }

    public JsonObject getConfig() {
        JsonObject ret = new JsonObject();

        variableModel.getVariables().forEach(var -> {
            ret.put(var.getKey(), var.getValue());
        });
        return ret;
    }

    @Override
    public void settingsChanged() {

    }

    @Override
    public void restartRequired() {

    }

    public int getSize() {
        return variableModel==null ? 0 : variableModel.getVariables().size();
    }

    public Variable get(int index) {
        return (variableModel == null || variableModel.getVariables().size()<index)
                ? null : variableModel.getVariables().get(index);
    }

    public void remove(String key) {
        variableModel.getVariables().removeIf(var -> StringUtils.equals(key, var.getKey()));
        fireSettingsChanged();
    }

    public void set(String key, String value) {
        variableModel.getVariables().add(Variable.builder()
                .key(key)
                .value(value)
                .build());
        fireSettingsChanged();
    }
}
