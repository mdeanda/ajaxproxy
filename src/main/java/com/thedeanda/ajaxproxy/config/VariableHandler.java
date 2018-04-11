package com.thedeanda.ajaxproxy.config;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.thedeanda.ajaxproxy.config.model.Variable;
import com.thedeanda.ajaxproxy.config.model.VariableValue;

public class VariableHandler {
	public String sub(List<Variable> variables, String input) {
		String s = StringUtils.trimToEmpty(input);

		if (variables != null) {
			for (Variable var : variables) {
				String variable = var.getKey();
				String v = "${" + variable + "}";
				if (s != null && s.indexOf(v) >= 0) {
					s = s.replaceAll(Pattern.quote(v), var.getValue());
				}
			}
		}
		return s;
	}

	public int subForInt(List<Variable> variables, String input) {
		input = sub(variables, input);
		return Integer.parseInt(input);
	}
	
	public VariableValue varForInt(List<Variable> variables, String input) {
		int val = subForInt(variables, input);
		return new VariableValue(input, val);
	}
}
