package com.thedeanda.ajaxproxy.ui.variable;

public class VariableController {
    private static VariableController instance = new VariableController();

    private VariableController() {

    }

    public static VariableController get() { return instance; }


}
