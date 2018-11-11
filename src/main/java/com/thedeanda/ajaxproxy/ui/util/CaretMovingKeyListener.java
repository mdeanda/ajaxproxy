package com.thedeanda.ajaxproxy.ui.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CaretMovingKeyListener implements KeyListener {
	private static final Logger log = LoggerFactory.getLogger(CaretMovingKeyListener.class);
	private JTextField field;

	public CaretMovingKeyListener(JTextField field) {
		log.debug("new instance");
		this.field = field;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		//log.debug("{}, {}, {}", e.getModifiers(), (e.getModifiers() & KeyEvent.CTRL_MASK), keyCode);
		if ((e.getModifiers() & KeyEvent.CTRL_MASK) == KeyEvent.CTRL_MASK) {
			if (KeyEvent.VK_RIGHT == keyCode) {
				moveCursor(e, 1);
			} else if (KeyEvent.VK_LEFT == keyCode) {
				moveCursor(e, -1);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	public int getNextCursorPosition(char[] input, int start, char[] delimeter, int direction) {
		int loopStart = start + direction;
		if (direction < 0)
			loopStart += direction;
		if (start >= input.length)
			loopStart = input.length - 1;
		else if (start < 0)
			loopStart = 0;
		for (int i = loopStart; i < input.length && i >= 0; i += direction) {
			char c = input[i];
			for (int j = 0; j < delimeter.length; j++) {
				if (c == delimeter[j]) {
					if (direction > 0)
						return i + direction;
					else
						return i;
				}
			}
		}
		return start;
	}

	private void moveCursor(KeyEvent e, int direction) {
		String origText = field.getText();
		int position = field.getCaretPosition();
		char[] delimeter = " /.\t".toCharArray();
		int next = getNextCursorPosition(origText.toCharArray(), position, delimeter, direction);
		if (next != position) {
			if ((e.getModifiers() & KeyEvent.SHIFT_MASK) == KeyEvent.SHIFT_MASK) {
				int start = field.getSelectionStart();
				int end = field.getSelectionEnd();
				if (direction > 0) {
					field.setCaretPosition(next);
					field.setSelectionStart(start);
					field.setSelectionEnd(next);
				} else {
					field.setSelectionEnd(end);
					field.setSelectionStart(next);
					field.setCaretPosition(next);
				}
			} else {
				field.setCaretPosition(next);
			}
			e.consume();
		}
	}

}
