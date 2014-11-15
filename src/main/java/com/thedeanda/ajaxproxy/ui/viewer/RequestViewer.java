package com.thedeanda.ajaxproxy.ui.viewer;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.ui.SwingUtils;

/**
 * this panel is the new implementation of the resource viewer tab. it will be
 * more event driven to allow it to monitor multiple requests as they are
 * happening instead of waiting for them to complete, possibly displaying them
 * out of order.
 * 
 * @author mdeanda
 *
 */
public class RequestViewer extends JPanel implements RequestListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(RequestViewer.class);
	private JTextArea headersField;
	private JLabel dataLabel;
	private JTabbedPane dataTabs;
	private JScrollPane headerScroll;

	public RequestViewer() {
		super();
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		JLabel headersLabel = SwingUtils.newJLabel("Headers");
		headersField = SwingUtils.newJTextArea();
		headersField.setEditable(false);
		headerScroll = new JScrollPane(headersField);
		add(headersLabel);
		add(headerScroll);

		dataLabel = SwingUtils.newJLabel("Data");
		add(dataLabel);
		dataTabs = new JTabbedPane();
		add(dataTabs);
		dataTabs.setBorder(BorderFactory.createEmptyBorder());

		// headers label
		layout.putConstraint(SpringLayout.WEST, headersLabel, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, headersLabel, 20,
				SpringLayout.NORTH, this);

		// headers field
		layout.putConstraint(SpringLayout.WEST, headerScroll, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, headerScroll, -10,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, headerScroll, 20,
				SpringLayout.SOUTH, headersLabel);
		layout.putConstraint(SpringLayout.SOUTH, headerScroll, 123,
				SpringLayout.SOUTH, headersLabel);

		// data label
		layout.putConstraint(SpringLayout.WEST, dataLabel, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, dataLabel, 20,
				SpringLayout.SOUTH, headerScroll);

		// data tabs
		layout.putConstraint(SpringLayout.WEST, dataTabs, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, dataTabs, -10,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, dataTabs, 20,
				SpringLayout.SOUTH, dataLabel);
		layout.putConstraint(SpringLayout.SOUTH, dataTabs, -10,
				SpringLayout.SOUTH, this);
	}

	@Override
	public void newRequest(UUID id, URL url, Header[] requestHeaders,
			byte[] data) {
		log.info("new request: {} {} {}", id, url, requestHeaders);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				headersField.setText("");
				dataTabs.removeAll();
			}
		});
	}

	@Override
	public void requestComplete(UUID id, int status, Header[] responseHeaders,
			byte[] data) {
		log.info("request complete: {} {} {}", id, status, responseHeaders);

		final StringBuilder headers = new StringBuilder();
		if (responseHeaders != null) {
			for (Header h : responseHeaders) {
				headers.append(String.format("%s: %s\n", h.getName(),
						h.getValue()));
			}
		}

		String tmpTextData = null;
		if (data != null) {
			try {
				InputStreamReader isr = new InputStreamReader(
						new ByteArrayInputStream(data), "UTF-8");
				StringWriter sw = new StringWriter();
				IOUtils.copy(isr, sw);
				tmpTextData = sw.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		final String textData = tmpTextData;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				headersField.setText(headers.toString().trim());
				scrollUp(headerScroll);
				if (textData != null) {
					JTextArea txtField = SwingUtils.newJTextArea();
					JScrollPane scroll = new JScrollPane(txtField);
					txtField.setText(textData);
					txtField.setEditable(false);
					txtField.setWrapStyleWord(true);
					txtField.setLineWrap(true);
					dataTabs.add("Text", scroll);
					scrollUp(scroll);
				}
			}
		});

	}

	private void scrollUp(final JScrollPane scroll) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JScrollBar vscroll = scroll.getVerticalScrollBar();
				vscroll.setValue(vscroll.getMinimum());
			}
		});
	}
}
