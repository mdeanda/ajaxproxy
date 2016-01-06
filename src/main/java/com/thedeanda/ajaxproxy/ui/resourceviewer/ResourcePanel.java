package com.thedeanda.ajaxproxy.ui.resourceviewer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;

import com.thedeanda.ajaxproxy.LoadedResource;
import com.thedeanda.ajaxproxy.ui.ContentViewer;
import com.thedeanda.ajaxproxy.ui.ResourceFrame;
import com.thedeanda.ajaxproxy.ui.SwingUtils;
import com.thedeanda.ajaxproxy.ui.model.Resource;
import com.thedeanda.ajaxproxy.ui.rest.RestClientFrame;

/**
 * panel to view a single resource.
 * 
 * @author mdeanda
 * 
 */
public class ResourcePanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	private LoadedResource oldResource;

	private JTabbedPane tabs;

	private ContentViewer inputCv;

	private ContentViewer outputCv;
	private JScrollPane generalScroll;
	private JEditorPane headersContent;

	private JPanel generalPanel;

	private JButton popupButton;
	private JButton replayButton;

	private boolean popupMode;

	private Resource newResource;

	public ResourcePanel(boolean popupMode) {
		setLayout(new BorderLayout());
		this.popupMode = popupMode;
		inputCv = new ContentViewer();
		outputCv = new ContentViewer();

		initGeneralPanel();

		tabs = new JTabbedPane();
		tabs.add("General", generalPanel);
		tabs.add("Input", inputCv);
		tabs.add("Output", outputCv);
		tabs.setBorder(BorderFactory.createEmptyBorder());
		add(BorderLayout.CENTER, tabs);
	}

	private void initGeneralPanel() {
		generalPanel = new JPanel();
		generalPanel.setLayout(new BorderLayout());

		if (!popupMode) {
			JToolBar toolBar = new JToolBar("Still draggable");
			toolBar.setFloatable(false);
			generalPanel.add(BorderLayout.NORTH, toolBar);
			popupButton = makeNavigationButton("New Window");
			toolBar.add(popupButton);

			replayButton = makeNavigationButton("Rest Client");
			toolBar.add(replayButton);
		}

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
		//generalScroll
			//	.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
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

	public void setResource(Resource resource) {
		clear();
		oldResource = null;
		this.newResource = resource;

		SwingUtils.executNonUi(new Runnable() {
			@Override
			public void run() {
				if (newResource == null)
					return;

				Resource resource = newResource;

				tryData(inputCv, resource.getInputData());
				tryData(outputCv, resource.getOutputData());
				showGeneralResourceProperties(resource);
			}
		});
	}

	private void showGeneralResourceProperties(Resource resource) {
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
		writeField(output, "Method", resource.getMethod());
		writeField(output, "Duration", String.valueOf(resource.getDuration()));
		writeField(output, "Date",
				new Date(resource.getStartTime()).toString());

		writeField(output, "Status", String.valueOf(resource.getStatus()));
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

	public void setResource(LoadedResource resource) {
		clear();
		newResource = null;
		this.oldResource = resource;

		if (oldResource != null) {
			final StringBuilder output = new StringBuilder();

			SwingUtils.executNonUi(new Runnable() {
				@Override
				public void run() {
					if (oldResource == null)
						return;
					LoadedResource resource = oldResource;

					tryText(inputCv, resource.getInputAsText());
					tryText(outputCv, resource.getOutputAsText());
					// inputCv.setContent(oldResource.getInputAsText());
					// outputCv.setContent(oldResource.getOutputAsText());

					output.append("<html><body>");
					output.append("<p><b>Request Path:</b> ");
					output.append(resource.getPath());
					output.append("</p>");
					output.append("<p><b>Method:</b> ");
					output.append(resource.getMethod());
					output.append("</p>");
					output.append("<p><b>Duration:</b> ");
					output.append(resource.getDuration());
					output.append("</p>");
					output.append("<p><b>Date:</b> ");
					output.append(resource.getDate());
					output.append("<p><b>Character Encoding:</b> ");
					output.append(resource.getCharacterEncoding());
					output.append("</p>");
					writeField(output, "Status",
							String.valueOf(resource.getStatusCode()));
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

					Exception ex = resource.getFilterException();
					if (ex != null) {
						StringWriter sw = new StringWriter();
						ex.printStackTrace(new PrintWriter(sw));
						String[] lines = StringUtils.split(sw.toString(), "\n");

						output.append("<h1>Exception</h1><div class=\"items\">");
						for (String line : lines) {
							output.append("<p>");
							output.append(line);
							output.append("</p>");
						}
						output.append("</div>");
					}

					output.append("</body></html>");

					showHeaders(output.toString());
				}
			});
		}
	}

	private void writeField(StringBuilder output, String name, String value) {
		output.append("<p><b>");
		output.append(name);
		output.append(":</b> ");
		output.append(value);
		output.append("</p>");
	}

	private void loadPopup() {
		if (oldResource != null) {
			ResourceFrame window = new ResourceFrame(oldResource);
			window.setVisible(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == this.popupButton) {
			loadPopup();
		} else if (source == this.replayButton) {
			RestClientFrame rest = new RestClientFrame();
			if (oldResource != null) {
				rest.fromResource(oldResource);
			} else {
				rest.fromResource(newResource);
			}
			rest.setVisible(true);
		}
	}

}
