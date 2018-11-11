package com.thedeanda.ajaxproxy.ui.resourceviewer.util;

import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonObject;
import org.dom4j.Document;
import org.fife.ui.hex.swing.HexEditor;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.tree.TreeNode;
import java.awt.image.BufferedImage;

public class DocumentContainer {
	public String rawText;
	public String formattedText;

	public TreeNode treeNode;
	public JComponent rawTextArea;
	public JComponent formattedTextArea;

	public Document xmlDocument;
	public JsonObject json;
	public JsonArray jsonArray;
	public DefaultStyledDocument rawDoc;

	public HexEditor hex;
	
	public BufferedImage image;
}
