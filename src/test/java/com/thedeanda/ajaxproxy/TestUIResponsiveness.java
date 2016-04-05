package com.thedeanda.ajaxproxy;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.ui.resourceviewer.ContentViewer;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

public class TestUIResponsiveness {
	private static final Logger log = LoggerFactory
			.getLogger(TestUIResponsiveness.class);

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	public static void addComponentsToPane(Container contentPane) {
		contentPane.setLayout(new BorderLayout(10, 10));

		final JTabbedPane tabs = new JTabbedPane();
		
		JButton btn = new JButton("load content");
		contentPane.add(btn, BorderLayout.PAGE_START);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						loadContent(tabs);					
					}
				}).start();
			}
		});

		JButton btn2 = new JButton("load text");
		contentPane.add(btn2, BorderLayout.PAGE_END);
		btn2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadTextBox(tabs);
			}
		});

		ContentViewer viewer = new ContentViewer();
		viewer.setContent("{a:3}".getBytes());
		tabs.add("0", viewer);
		contentPane.add(tabs, BorderLayout.CENTER);
	}

	private static void createAndShowGUI() {
		JFrame frame = new JFrame("test document rendering responsiveness");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set up the content pane and add swing components to it
		addComponentsToPane(frame.getContentPane());

		frame.setPreferredSize(new Dimension(800, 600));
		frame.pack();
		frame.setVisible(true);
	}

	private static void loadContent(JTabbedPane tabs) {
		ContentViewer cv = new ContentViewer();
		tabs.add(String.valueOf(tabs.getTabCount()), cv);

		
		log.debug("loading content");
		Lorem lorem = LoremIpsum.getInstance();
		String content = "<html>" + lorem.getHtmlParagraphs(15000, 15000) + "</html>"; 
		byte [] bytes = content.getBytes();
		log.debug("finished loading content, setting content to content viewer");
		cv.setContent(bytes);
		log.debug("finished setting content");
	}

	private static void loadTextBox(JTabbedPane tabs) {
		JTextArea text = new JTextArea();
		text.setWrapStyleWord(true);
		//text.setLineWrap(true);
		tabs.add(String.valueOf(tabs.getTabCount()), new JScrollPane(text));

		log.debug("loading content");
		Lorem lorem = LoremIpsum.getInstance();
		String content = "<html>" + lorem.getHtmlParagraphs(1000, 1000) + "</html>";
		content = StringUtils.replaceAll(content, "</p>", "</p>\n");
		log.debug("finished loading content, setting content to content viewer");
		text.setText(content);
		log.debug("finished setting content");
		
	}
}
