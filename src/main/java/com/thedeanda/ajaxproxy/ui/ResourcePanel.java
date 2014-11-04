package com.thedeanda.ajaxproxy.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;

import com.thedeanda.ajaxproxy.LoadedResource;
import com.thedeanda.ajaxproxy.http.RequestListener;

/**
 * panel to view a single resource.
 * 
 * @author mdeanda
 *
 */
public class ResourcePanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	private LoadedResource resource;

	private JTabbedPane tabs;

	private ContentViewer inputCv;

	private ContentViewer outputCv;
	private JScrollPane generalScroll;
	private JEditorPane headersContent;

	private JPanel generalPanel;

	private JButton popupButton;

	private boolean popupMode;

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
			popupButton = makeNavigationButton("newWindow", "New Window");
			toolBar.add(popupButton);
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
		generalScroll
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		headersContent.setEditorKit(kit);
		Document doc = kit.createDefaultDocument();
		headersContent.setDocument(doc);
		generalPanel.add(BorderLayout.CENTER, generalScroll);
	}

	protected JButton makeNavigationButton(String imageName, String altText) {
		// Look for the image.
		String imgLocation = "images/" + imageName + ".gif";
		URL imageURL = getClass().getResource(imgLocation);

		// Create and initialize the button.
		JButton button = new JButton();
		button.addActionListener(this);

		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else { // no image found
			button.setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}

		return button;
	}

	public void setResource(final LoadedResource resource) {
		this.resource = resource;
		headersContent.setText("");

		if (resource != null) {
			final StringBuilder headers = new StringBuilder();
			final Runnable uiupdate = new Runnable() {
				@Override
				public void run() {
					headersContent.setText(headers.toString());
					headersContent.setCaretPosition(0);
				}
			};
			SwingUtils.executNonUi(new Runnable() {
				@Override
				public void run() {
					inputCv.setContent(resource.getInputAsText());
					outputCv.setContent(resource.getOutputAsText());

					headers.append("<html><body>");
					headers.append("<p><b>Request Path:</b> ");
					headers.append(resource.getPath());
					headers.append("</p>");
					headers.append("<p><b>Method:</b> ");
					headers.append(resource.getMethod());
					headers.append("</p>");
					headers.append("<p><b>Duration:</b> ");
					headers.append(resource.getDuration());
					headers.append("</p>");
					headers.append("<p><b>Date:</b> ");
					headers.append(resource.getDate());
					headers.append("<p><b>Character Encoding:</b> ");
					headers.append(resource.getCharacterEncoding());
					headers.append("</p>");
					writeField(headers, "Status",
							String.valueOf(resource.getStatusCode()));
					headers.append("<h1>Request Headers</h1><div class=\"items\">");
					Map<String, String> map = resource.getRequestHeaders();
					for (String name : map.keySet()) {
						headers.append("<p><b>");
						headers.append(name);
						headers.append(":</b> ");
						headers.append(map.get(name));
						headers.append("</p>");
					}
					headers.append("</div>");

					headers.append("<h1>Response Headers</h1><div class=\"items\">");
					map = resource.getResponseHeaders();
					for (String name : map.keySet()) {
						headers.append("<p><b>");
						headers.append(name);
						headers.append(":</b> ");
						headers.append(map.get(name));
						headers.append("</p>");
					}
					headers.append("</div>");

					Exception ex = resource.getFilterException();
					if (ex != null) {
						StringWriter sw = new StringWriter();
						ex.printStackTrace(new PrintWriter(sw));
						String[] lines = StringUtils.split(sw.toString(), "\n");

						headers.append("<h1>Exception</h1><div class=\"items\">");
						for (String line : lines) {
							headers.append("<p>");
							headers.append(line);
							headers.append("</p>");
						}
						headers.append("</div>");
					}

					headers.append("</body></html>");

					SwingUtilities.invokeLater(uiupdate);
				}
			});
		}
	}

	private void writeField(StringBuilder headers, String name, String value) {
		headers.append("<p><b>");
		headers.append(name);
		headers.append(":</b> ");
		headers.append(value);
		headers.append("</p>");
	}

	private void loadPopup() {
		if (resource != null) {
			ResourceFrame window = new ResourceFrame(resource);
			window.setVisible(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == this.popupButton) {
			loadPopup();
		}
	}

}
