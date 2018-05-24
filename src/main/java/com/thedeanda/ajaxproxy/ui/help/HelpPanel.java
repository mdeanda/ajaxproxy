package com.thedeanda.ajaxproxy.ui.help;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JPanel;

public class HelpPanel extends JPanel {
	private static final String HTML = "<html><body>test<b>bold</b><img src=\"http://placehold.it/300x300\"/><a href=\"https://google.com/\">google</a></body></html>";
	private JEditorPane viewer = new JEditorPane();

	public HelpPanel() {
		setLayout(new BorderLayout());

		HelpContentLoader loader = new HelpContentLoader();
		viewer.setEditable(false);
		viewer.setEditorKit(new LocalImageHTMLEditorKit());
		viewer.addHyperlinkListener(loader);

		add(viewer, BorderLayout.CENTER);
		loader.loadPage(viewer, "index");
	}

}
