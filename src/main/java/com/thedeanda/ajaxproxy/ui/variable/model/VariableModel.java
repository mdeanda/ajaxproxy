package com.thedeanda.ajaxproxy.ui.variable.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class VariableModel {
    @Builder.Default
    List<Variable> variables = new ArrayList<>();
}
