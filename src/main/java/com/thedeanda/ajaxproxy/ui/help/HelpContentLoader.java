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

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

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
			contents = parseMarkdown(contents);
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

	private String parseMarkdown(String md) {
		MutableDataSet options = new MutableDataSet();
		// options.set(Parser.EXTENSIONS, Arrays.asList(WikiLinkExtension.create()));

		// uncomment to set optional extensions
		// options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(),
		// StrikethroughExtension.create()));

		// uncomment to convert soft-breaks to hard breaks
		// options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

		Parser parser = Parser.builder(options).build();
		HtmlRenderer renderer = HtmlRenderer.builder(options).build();

		// You can re-use parser and renderer instances
		Node document = parser.parse(md);
		String html = renderer.render(document); // "<p>This is <em>Sparta</em></p>\n"

		return html;
	}
}
