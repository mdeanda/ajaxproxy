package com.thedeanda.ajaxproxy.ui.resourceviewer;

import java.awt.BorderLayout;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.text.WrappedPlainView;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.ui.resourceviewer.util.DocumentContainer;
import com.thedeanda.ajaxproxy.ui.resourceviewer.util.DocumentParser;

/**
 * this is a content viewer used to show input/ouput content of http requests.
 * it may have a nested tab view to switch between raw, formatted and tree view
 * 
 * @author mdeanda
 * 
 */
public class ContentViewer extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int MAX_TEXT_SIZE = 300000;
	private static final Logger log = LoggerFactory
			.getLogger(ContentViewer.class);
	private JTabbedPane tabs;

	public ContentViewer() {
		setLayout(new BorderLayout());
		tabs = new JTabbedPane();
		add(BorderLayout.CENTER, tabs);
		tabs.add("", new JButton(""));

		setBorder(BorderFactory.createEmptyBorder());
		tabs.setBorder(BorderFactory.createEmptyBorder());
	}

	public void setContent(final byte[] input) {
		tabs.removeAll();

		if (ArrayUtils.isEmpty(input)) {
			log.debug("empty content, stop here");
			return;
		}

		log.debug("setting content");
		new TreeLoader(input).execute();
	}

	private class TreeLoader extends
			SwingWorker<DocumentContainer, DocumentContainer> {

		private String input;
		private byte[] bytes;

		public TreeLoader(byte[] input) {
			this.bytes = input;
		}

		public TreeLoader(String input) {
			this.input = input;
		}

		@Override
		protected DocumentContainer doInBackground() throws Exception {
			log.info("start parsing");

			DocumentParser parser = new DocumentParser();
			DocumentContainer document;
			if (bytes != null) {
				document = parser.parse(bytes);
			} else {
				document = parser.parse(input);
			}

			log.info("done parsing");

			return document;
		}

		@Override
		protected void done() {
			log.info("start of done");
			DocumentContainer data;
			try {
				data = get();
			} catch (InterruptedException | ExecutionException e) {
				log.error(e.getMessage(), e);
				return;
			}

			if (data.formattedText != null) {
				log.info("setting formatted");
				tabs.add("Formatted", data.formattedTextArea);
			}
			if (data.treeNode != null) {
				log.info("setting tree");
				JTree tree = new JTree(new DefaultTreeModel(data.treeNode));
				tree.setBorder(BorderFactory.createEmptyBorder());
				tree.setShowsRootHandles(true);
				JScrollPane scroll = new JScrollPane(tree);
				scroll.setBorder(BorderFactory.createEmptyBorder());
				tabs.add("Tree View", scroll);
			}
			if (data.rawText != null && data.rawText.length() < MAX_TEXT_SIZE) {
				log.info("setting raw: " + data.rawText.length());
				tabs.add("Raw Text", data.rawTextArea);
			}
			if (data.hex != null) {
				log.info("setting hex");
				tabs.add("Hex", data.hex);
			}

			log.info("end of done");
		}

	}

}
