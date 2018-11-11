package com.thedeanda.ajaxproxy.ui.update;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReleaseVersionTest {

	@Test
	public void testParseSnapshot() {
		ReleaseVersion rv = new ReleaseVersion("1.24-SNAPSHOT");
		assertEquals(1, rv.getMajor());
		assertEquals(24, rv.getMinor());
		assertEquals(0, rv.getPatch());
	}

	@Test
	public void testParse0() {
		ReleaseVersion rv = new ReleaseVersion("23");
		assertEquals(23, rv.getMajor());
		assertEquals(0, rv.getMinor());
		assertEquals(0, rv.getPatch());
	}

	@Test
	public void testParse1() {
		ReleaseVersion rv = new ReleaseVersion("1.23");
		assertEquals(1, rv.getMajor());
		assertEquals(23, rv.getMinor());
		assertEquals(0, rv.getPatch());
	}

	@Test
	public void testParse2() {
		ReleaseVersion rv = new ReleaseVersion("1.23.4");
		assertEquals(1, rv.getMajor());
		assertEquals(23, rv.getMinor());
		assertEquals(4, rv.getPatch());
	}

	@Test
	public void testSimpleParse() {
		ReleaseVersion rv = new ReleaseVersion(1, 23, 4);
		assertEquals(1, rv.getMajor());
		assertEquals(23, rv.getMinor());
		assertEquals(4, rv.getPatch());
	}

	@Test
	public void testCompare1() {
		ReleaseVersion rv1 = new ReleaseVersion(1, 0, 0);
		ReleaseVersion rv2 = new ReleaseVersion(2, 0, 0);

		assertEquals(-1, rv1.compareTo(rv2));
		assertEquals(1, rv2.compareTo(rv1));
	}

	@Test
	public void testCompare2() {
		ReleaseVersion rv1 = new ReleaseVersion(1, 3, 0);
		ReleaseVersion rv2 = new ReleaseVersion(1, 4, 0);

		assertEquals(-1, rv1.compareTo(rv2));
		assertEquals(1, rv2.compareTo(rv1));
	}

	@Test
	public void testCompare3() {
		ReleaseVersion rv1 = new ReleaseVersion(1, 3, 5);
		ReleaseVersion rv2 = new ReleaseVersion(1, 4, 7);

		assertEquals(-1, rv1.compareTo(rv2));
		assertEquals(1, rv2.compareTo(rv1));
	}

	@Test
	public void testCompareEqual() {
		ReleaseVersion rv1 = new ReleaseVersion(1, 3, 5);
		ReleaseVersion rv2 = new ReleaseVersion(1, 3, 5);

		assertEquals(0, rv1.compareTo(rv2));
		assertEquals(0, rv2.compareTo(rv1));
	}
}
