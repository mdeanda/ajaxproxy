package com.thedeanda.ajaxproxy.ui.update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ReleaseVersion implements Comparable<ReleaseVersion> {
	private int major;
	private int minor;
	private int patch;

	private String input;

	public ReleaseVersion(int major, int minor, int patch) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}

	public ReleaseVersion(String version) {
		input = version;

		if (StringUtils.endsWith(version, "-SNAPSHOT")) {
			version = version.substring(0, version.length() - 9);
		}

		if (!StringUtils.isEmpty(version))
			return;

		Pattern reg = Pattern.compile("(\\d+)(\\.(\\d+)(\\.(\\d+)(-SNAPSHOT)?)?)?");
		Matcher matcher = reg.matcher(version);
		if (!matcher.matches())
			return;

		major = parse(matcher.group(1));
		minor = parse(matcher.group(3));
		patch = parse(matcher.group(5));
	}

	private int parse(String input) {
		if (StringUtils.isBlank(input))
			return 0;
		return Integer.parseInt(input);
	}

	@Override
	public int compareTo(ReleaseVersion rv) {
		if (major < rv.major)
			return -1;
		else if (rv.major < major)
			return 1;

		if (minor < rv.minor)
			return -1;
		else if (rv.minor < minor)
			return 1;

		if (patch < rv.patch)
			return -1;
		else if (rv.patch < patch)
			return 1;

		return 0;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getPatch() {
		return patch;
	}

	public String toString() {
		if (!StringUtils.isBlank(input)) {
			return input;
		} else {
			return String.format("%d.%d.%d", major, minor, patch);
		}
	}
}
