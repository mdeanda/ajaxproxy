package com.thedeanda.ajaxproxy.ui.viewer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonObject;

/**
 * convenience methods and data structure to load and hold parsed data
 * 
 * @author mdeanda
 *
 */
public class ParsedData {
	private static final Logger log = LoggerFactory.getLogger(ParsedData.class);
	public String raw;
	public JsonObject json;
	public JsonArray jsonArray;
	public String formattedText;
	public BufferedImage bufferedImage;
	public Document xml;

	private Set<String> imageTypes = new HashSet<String>();
	private byte[] data;

	public ParsedData() {
		imageTypes.add("image/jpeg");
		imageTypes.add("image/gif");
		imageTypes.add("image/png");
	}
	
	public void parseString(String data) {
		this.raw = data;
		loadJson();
		loadXml();
	}

	public void parse(byte[] data, String contentType) {
		this.data = data;
		if (!StringUtils.isBlank(contentType)) {
			contentType = contentType.toLowerCase().trim();
			int semi = contentType.indexOf(";");
			if (semi >= 0) {
				contentType = contentType.substring(0, semi);
			}
		}
		log.warn("content type: " + contentType);

		if (isTextType(contentType)) {
			loadPlain(data);
			parseString(this.raw);
		}
		if (isImageType(contentType)) {
			loadImage(data);
		}

	}

	private boolean isImageType(String contentType) {
		return imageTypes.contains(contentType);
	}

	private boolean isTextType(String contentType) {
		boolean ret = true;
		if (imageTypes.contains(contentType)) {
			ret = false;
		}
		return ret;
	}

	private void loadPlain(byte[] data) {
		if (data != null) {
			try {
				InputStreamReader isr = new InputStreamReader(
						new ByteArrayInputStream(data), "UTF-8");
				StringWriter sw = new StringWriter();
				IOUtils.copy(isr, sw);
				raw = sw.toString();
			} catch (IOException e) {
				log.debug(e.getMessage(), e);
			}
		}
	}

	private void loadJson() {
		if (StringUtils.isBlank(raw) || raw.trim().length() < 2)
			return;
		char firstChar = raw.trim().charAt(0);

		if (firstChar == '{') {
			try {
				json = JsonObject.parse(raw);
				formattedText = json.toString(4);
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}
		}
		if (formattedText == null && firstChar == '[') {
			try {
				jsonArray = JsonArray.parse(raw);
				formattedText = jsonArray.toString(4);
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}
		}
	}

	private void loadXml() {
		if (StringUtils.isBlank(raw) || raw.trim().length() < 4)
			return;
		char firstChar = raw.trim().charAt(0);

		if (firstChar == '<') {
			// try xml formatting
			try {
				xml = DocumentHelper.parseText(raw);
			} catch (DocumentException e) {
				log.debug(e.getMessage(), e);
			}
		}
	}

	private void loadImage(byte[] data) {
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(data);
			bufferedImage = ImageIO.read(stream);
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
	}

	public byte[] getData() {
		return data;
	}
}
