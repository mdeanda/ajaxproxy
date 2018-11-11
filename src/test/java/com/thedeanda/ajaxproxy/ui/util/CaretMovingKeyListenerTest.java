package com.thedeanda.ajaxproxy.ui.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CaretMovingKeyListenerTest {

	private CaretMovingKeyListener listener;

	@Before
	public void init() {
		listener = new CaretMovingKeyListener(null);
	}

	@Test
	public void testCaretEmpty() {
		char[] input = "".toCharArray();
		int start = 0;
		char[] delimter = { ' ', '.', '\t' };

		int next = listener.getNextCursorPosition(input, start, delimter, 1);
		assertEquals(0, next);

		next = listener.getNextCursorPosition(input, start, delimter, -1);
		assertEquals(0, next);
	}

	@Test
	public void testCaretSpacesOnly() {
		char[] input = "the quick".toCharArray();
		int start = 0;
		char[] delimter = { ' ', '.', '\t' };

		int next = listener.getNextCursorPosition(input, start, delimter, 1);
		assertEquals(4, next);

		next = listener.getNextCursorPosition(input, start, delimter, -1);
		assertEquals(0, next);

		start = 9;
		next = listener.getNextCursorPosition(input, start, delimter, 1);
		assertEquals(9, next);

		next = listener.getNextCursorPosition(input, start, delimter, -1);
		assertEquals(3, next);
	}

	@Test
	public void testCaret() {
		char[] input = "the quick.brownfoxjumped.over.the lazy dog".toCharArray();
		char[] delimter = { ' ', '.', '\t' };

		int next = listener.getNextCursorPosition(input, 10, delimter, 1);
		assertEquals(25, next);

		next = listener.getNextCursorPosition(input, 10, delimter, -1);
		assertEquals(3, next);

		next = listener.getNextCursorPosition(input, 29, delimter, 1);
		assertEquals(34, next);

		next = listener.getNextCursorPosition(input, 30, delimter, 1);
		assertEquals(34, next);
	}
}
