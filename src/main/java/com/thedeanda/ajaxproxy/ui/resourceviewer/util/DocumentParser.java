package com.thedeanda.ajaxproxy.ui.resourceviewer.util;

import java.awt.Component;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.fife.ui.hex.swing.HexEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.ui.resourceviewer.TreeBuilder;
import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonObject;

public class DocumentParser {
	private static final Logger log = LoggerFactory
			.getLogger(DocumentParser.class);

	public DocumentContainer parse(byte[] data) {
		log.debug("start parsing byte input");

		DocumentContainer container = parseInternal(data);
		return container;
	}

	public DocumentContainer parse(String input) {
		log.debug("start parsing string input");

		DocumentContainer container = parseInternal(input.getBytes());
		return container;
	}

	private DocumentContainer parseInternal(byte[] data) {
		DocumentContainer container = new DocumentContainer();

		try {
			HexEditor hex = new HexEditor();
			hex.open(new ByteArrayInputStream(data));
			hex.setCellEditable(false);
			container.hex = hex;
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		}

		String input = null;
		try {
			input = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
			input = "";
		}
		container.rawText = input;

		JTextArea textArea = new JTextArea(container.rawText);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		container.rawTextArea = new JScrollPane(textArea);
		DefaultStyledDocument doc = new DefaultStyledDocument();
		AttributeSet a = null;
		try {
			doc.insertString(0, container.rawText, a);
			container.rawDoc = doc;
		} catch (BadLocationException e) {
			log.error(e.getMessage(), e);
		}

		input = StringUtils.trimToEmpty(input);

		tryParseJson(container, input);
		tryParseJsonArray(container, input);
		tryParseXml(container, input);

		log.debug("done parsing input");
		return container;
	}

	private void setFormatted(DocumentContainer container, String formatted) {
		container.formattedText = formatted;
		container.formattedTextArea = new JScrollPane(new JTextArea(formatted));
	}

	private void tryParseJson(DocumentContainer container, String input) {
		if (input.startsWith("{")) {
			log.debug("start parsing json");
			try {
				JsonObject json = JsonObject.parse(input);
				TreeBuilder treeBuilder = new TreeBuilder();
				container.treeNode = treeBuilder.buildTree(json);
				setFormatted(container, json.toString(4));
			} catch (Exception e) {
				log.trace(e.getMessage(), e);
			}
			log.debug("done parsing json");
		}
	}

	private void tryParseJsonArray(DocumentContainer container, String input) {
		if (input.startsWith("[")) {
			log.debug("start parsing json");
			try {
				JsonArray json = JsonArray.parse(input);
				TreeBuilder treeBuilder = new TreeBuilder();
				container.treeNode = treeBuilder.buildTree(json);
				setFormatted(container, json.toString(4));
			} catch (Exception e) {
				log.trace(e.getMessage(), e);
			}
			log.debug("done parsing json");
		}
	}

	private void tryParseXml(DocumentContainer container, String input) {
		if (input.startsWith("<")) {
			log.debug("start parsing xml");
			// try xml formatting
			try {
				Document doc = DocumentHelper.parseText(input);
				TreeBuilder treeBuilder = new TreeBuilder();
				container.treeNode = treeBuilder.buildTree(doc);
				setFormatted(container, formatXml(doc));
			} catch (DocumentException e) {
				log.trace(e.getMessage(), e);
			}
			log.debug("done parsing xml");
		}
	}

	private String formatXml(Document doc) {
		StringWriter out = new StringWriter();
		try {
			OutputFormat outformat = OutputFormat.createPrettyPrint();
			outformat.setEncoding("UTF-8");
			XMLWriter writer = new XMLWriter(out, outformat);
			writer.write(doc);
			writer.flush();
		} catch (Exception e) {
		}
		return out.toString();
	}
}
