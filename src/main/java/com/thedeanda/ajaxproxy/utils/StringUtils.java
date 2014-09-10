package com.thedeanda.ajaxproxy.utils;

public class StringUtils {
	public static boolean isBlank(String string) {
		return string == null || "".equals(string.trim());
	}

	public static String leftPad(String str, int size, String padChar) {
		while (str.length() < size) {
			str = padChar + str;
		}
		return str;
	}

	public static String[] split(String str) {
		if (isBlank(str)) {
			return new String[] {};
		}
		return str.split("\n");
	}

}
