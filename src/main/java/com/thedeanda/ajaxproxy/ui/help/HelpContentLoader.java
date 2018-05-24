package com.thedeanda.ajaxproxy.ui.help;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelpContentLoader implements HyperlinkListener {

	public HelpContentLoader() {
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		JEditorPane pane = (JEditorPane) e.getSource();
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			log.debug("url: " + e.getURL() + " - " + e.getDescription());
			String page = e.getDescription();
			if (!StringUtils.isBlank(page)) {
				loadPage(pane, page);
			}
		}
	}

	public void loadPage(JEditorPane pane, String page) {
		try {
			String contents = loadMarkdown("/help/" + page + ".md");
			pane.setContentType("text/html");
			pane.setText(contents);
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		}
	}

	private String loadMarkdown(String resource) throws IOException {
		try (InputStream is = getClass().getResourceAsStream(resource);
				InputStreamReader isr = new InputStreamReader(is)) {

			StringWriter output = new StringWriter();
			IOUtils.copy(isr, output);
			return output.toString();
		}
	}
}
