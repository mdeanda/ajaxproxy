package com.thedeanda.ajaxproxy.config;

import com.thedeanda.ajaxproxy.config.model.IntVariable;
import com.thedeanda.ajaxproxy.config.model.StringVariable;
import com.thedeanda.ajaxproxy.config.model.Variable;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

public class VariableHandler {
	private List<Variable> variables;

	public VariableHandler(List<Variable> variables) {
		this.variables = variables;
	}

	public String sub(String input) {
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

	public int subForInt(String input, int defaultValue) {
		input = sub(input);
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException ex) {
			return defaultValue;
		}
	}

	public int subForInt(String input) {
		input = sub(input);
		return Integer.parseInt(input);
	}

	public IntVariable varForInt(String input, int defaultValue) {
		int val = subForInt(input, defaultValue);
		return new IntVariable(input, val);
	}

	public StringVariable varForString(String input) {
		String val = sub(input);
		return new StringVariable(input, val);
	}
}
