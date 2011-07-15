package com.thedeanda.ajaxproxy.ui;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;

public class TextAreaWriter extends Writer {
	private JTextArea ta;
	private List<String> buffer = new ArrayList<String>();

	public TextAreaWriter() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				runThread();
			}
		}, "text area writer thread").start();
	}

	private void runThread() {
		while (!Thread.interrupted()) {
			synchronized (buffer) {
				if (buffer.isEmpty()) {
					try {
						buffer.wait();
					} catch (InterruptedException e) {
						return;
					}
				}
			}

			synchronized (buffer) {
				for (String line : buffer) {
					if (ta != null) {
						ta.append(line);
					}
				}
				buffer.clear();
			}
		}
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public void flush() throws IOException {

	}

	public void setTextArea(JTextArea ta) {
		this.ta = ta;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		if (ta != null) {
			String line = new String(cbuf, off, len);
			synchronized (buffer) {
				buffer.add(line);
				buffer.notify();
			}
		}
	}

}
