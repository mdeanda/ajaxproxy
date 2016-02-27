package com.thedeanda.ajaxproxy.ui.resourceviewer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.service.ResourceService;
import com.thedeanda.ajaxproxy.service.StoredResource;
import com.thedeanda.ajaxproxy.ui.ContentViewer;
import com.thedeanda.ajaxproxy.ui.model.Resource;

/**
 * panel to view a single resource.
 * 
 * @author mdeanda
 * 
 */
public class ResourcePanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(ResourcePanel.class);

	private JTabbedPane tabs;

	private ContentViewer inputCv;

	private ContentViewer outputCv;
	private JScrollPane generalScroll;
	private JEditorPane headersContent;

	private JPanel generalPanel;

	private Resource newResource;
	private ResourceService resourceService;

	public ResourcePanel(ResourceService resourceService, boolean popupMode) {
		this.resourceService = resourceService;

		setLayout(new BorderLayout());
		inputCv = new ContentViewer();
		outputCv = new ContentViewer();

		initGeneralPanel();

		tabs = new JTabbedPane();
		tabs.add("General", generalPanel);
		tabs.add("Input", wrap(inputCv));
		tabs.add("Output", wrap(outputCv));
		tabs.setBorder(BorderFactory.createEmptyBorder());
		add(BorderLayout.CENTER, tabs);
	}

	private JPanel wrap(JPanel comp) {
		SpringLayout layout = new SpringLayout();
		JPanel panel = new JPanel(layout);

		panel.add(comp);

		layout.putConstraint(SpringLayout.NORTH, comp, 10, SpringLayout.NORTH,
				panel);
		layout.putConstraint(SpringLayout.SOUTH, comp, -10, SpringLayout.SOUTH,
				panel);
		layout.putConstraint(SpringLayout.WEST, comp, 10, SpringLayout.WEST,
				panel);
		layout.putConstraint(SpringLayout.EAST, comp, -10, SpringLayout.EAST,
				panel);

		return panel;
	}

	private void initGeneralPanel() {
		generalPanel = new JPanel();
		generalPanel.setLayout(new BorderLayout());

		HTMLEditorKit kit = new HTMLEditorKit();
		StyleSheet styleSheet = kit.getStyleSheet();
		styleSheet
				.addRule("body {color:#000000; margin: 4px; font-size: 10px; font-family: sans-serif; }");
		styleSheet.addRule("h1 { margin: 4px 0; font-size: 12px; }");
		styleSheet.addRule("div.items { margin-left: 10px;}");
		styleSheet.addRule("p { margin: 0; font-family: monospace;}");
		styleSheet.addRule("b { font-family: sans-serif; color: #444444;}");

		headersContent = new JEditorPane();
		headersContent.setEditable(false);
		generalScroll = new JScrollPane(headersContent);
		// generalScroll
		// .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		headersContent.setEditorKit(kit);
		Document doc = kit.createDefaultDocument();
		headersContent.setDocument(doc);
		generalPanel.add(BorderLayout.CENTER, generalScroll);
	}

	protected JButton makeNavigationButton(String altText) {
		// Create and initialize the button.
		JButton button = new JButton();
		button.addActionListener(this);
		button.setText(altText);

		return button;
	}

	private void clear() {
		inputCv.setContent(null);
		outputCv.setContent(null);
		headersContent.setText("");
	}

	private void tryData(final ContentViewer cv, byte[] data) {
		if (data == null) {
			tryText(cv, null);
		} else {
			StringWriter sw = new StringWriter();
			try {
				IOUtils.copy(new InputStreamReader(new ByteArrayInputStream(
						data)), sw);
				tryText(cv, sw.toString());
			} catch (IOException e) {
				// log.warn(e.getMessage(), e);
			}
		}
	}

	private void tryText(final ContentViewer cv, String text) {
		cv.setContent(text);
	}

	private void showHeaders(final String markup) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				headersContent.setText(markup);
				headersContent.setCaretPosition(0);
			}
		});
	}

	public void setResource(final Resource resource) {
		clear();
		this.newResource = resource;

		if (newResource == null)
			return;

		new SwingWorker<StoredResource, StoredResource>() {
			StoredResource storedResource;
			
			@Override
			protected StoredResource doInBackground() throws Exception {
				storedResource = resourceService.get(newResource
						.getId());
				return storedResource;
			}

			@Override
			protected void done() {
				if (storedResource == null) {
					log.warn("couldn't load resource: {}", newResource.getId());
					return;
				}

				tryData(inputCv, storedResource.getInput());
				tryData(outputCv, storedResource.getOutput());
				showGeneralResourceProperties(storedResource, resource);
			}
		}.execute();
	}

	private void showGeneralResourceProperties(StoredResource storedResource,
			Resource resource) {
		final StringBuilder output = new StringBuilder();

		output.append("<html><body>");

		writeField(output, "Request URL", resource.getUrl());
		if (resource.getUrlObject() != null) {
			URL uo = resource.getUrlObject();
			writeField(output, "Request Path", uo.getPath());
			if (uo.getQuery() != null) {
				writeField(output, "Query String", uo.getQuery());
			}
		}
		// writeField(headers, "", );
		writeField(output, "Method", storedResource.getMethod());
		writeField(output, "Duration",
				String.valueOf(storedResource.getDuration()));
		writeField(output, "Date",
				new Date(storedResource.getStartTime()).toString());

		writeField(output, "Status", String.valueOf(storedResource.getStatus()));
		output.append("<h1>Request Headers</h1><div class=\"items\">");
		Header[] reqHeaders = resource.getRequestHeaders();
		if (reqHeaders != null) {
			for (Header hdr : reqHeaders) {
				writeField(output, hdr.getName(), hdr.getValue());
			}
		}
		output.append("</div>");

		output.append("<h1>Response Headers</h1><div class=\"items\">");
		Header[] respHeaders = resource.getResponseHeaders();
		if (respHeaders != null) {
			for (Header hdr : respHeaders) {
				writeField(output, hdr.getName(), hdr.getValue());
			}
		}
		output.append("</div>");

		/*
		 * Exception ex = resource.getFilterException(); if (ex != null) {
		 * StringWriter sw = new StringWriter(); ex.printStackTrace(new
		 * PrintWriter(sw)); String[] lines = StringUtils.split(sw.toString(),
		 * "\n");
		 * 
		 * headers.append("<h1>Exception</h1><div class=\"items\">"); for
		 * (String line : lines) { headers.append("<p>"); headers.append(line);
		 * headers.append("</p>"); } headers.append("</div>"); } //
		 */

		output.append("</body></html>");

		showHeaders(output.toString());
	}

	private void writeField(StringBuilder output, String name, String value) {
		output.append("<p><b>");
		output.append(name);
		output.append(":</b> ");
		output.append(value);
		output.append("</p>");
	}

	private void loadPopup() {
		ResourceFrame window = new ResourceFrame(resourceService, newResource);
		window.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

}
