package com.thedeanda.ajaxproxy.ui.variable.controller;

import com.thedeanda.ajaxproxy.ui.SettingsChangedListener;
import com.thedeanda.ajaxproxy.ui.variable.model.VariableModel;
import com.thedeanda.javajson.JsonObject;

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

    }

    //temporary, use custom listener instead maybe
    public void addListener(final SettingsChangedListener listener) {
        this.listeners.add(listener);
    }

    public void setVariableModel(VariableModel variableModel) {
        this.variableModel = variableModel;
        //fire events
    }

    public void setConfig(JsonObject jsonObject) {
        this.config = jsonObject;
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
}
