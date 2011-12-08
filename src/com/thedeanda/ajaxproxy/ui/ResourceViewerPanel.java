package com.thedeanda.ajaxproxy.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import net.miginfocom.swing.MigLayout;

import com.thedeanda.ajaxproxy.AccessTracker;
import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.LoadedResource;

/** tracks files that get loaded */
public class ResourceViewerPanel extends JPanel implements AccessTracker {
	private static final long serialVersionUID = 1L;
	private JButton clearBtn;
	private JCheckBox toggleBtn;
	private DefaultListModel model;
	private JList list;
	private JTabbedPane tabs;
	private JTextArea outputContent;
	private JScrollPane outputScroll;
	private JTextArea inputContent;
	private JScrollPane inputScroll;

	public ResourceViewerPanel() {
		this.setLayout(new MigLayout("fill", "", "[][fill]"));
		model = new DefaultListModel();

		this.clearBtn = new JButton("Clear");
		toggleBtn = new JCheckBox("Monitor Resources");
		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.clear();
			}
		});

		add(clearBtn);
		add(toggleBtn, "align right, wrap");

		list = new JList(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				listItemSelected(evt);
			}
		});

		inputContent = new JTextArea();
		inputContent.setEditable(false);
		inputScroll = new JScrollPane(inputContent);
		
		outputContent = new JTextArea();
		outputContent.setEditable(false);
		outputScroll = new JScrollPane(outputContent);

		tabs = new JTabbedPane();
		tabs.add("Overview", new JPanel());
		tabs.add("Input", inputScroll);
		tabs.add("Output", outputScroll);
		tabs.setBorder(BorderFactory.createEmptyBorder());

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(new JScrollPane(list));
		split.setRightComponent(tabs);
		split.setDividerLocation(200);
		split.setBorder(BorderFactory.createEmptyBorder());
		flattenSplitPane(split);
		add(split, "span 2, growx, growy");
	}

	public static void flattenSplitPane(JSplitPane jSplitPane) {
		jSplitPane.setUI(new BasicSplitPaneUI() {
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					private static final long serialVersionUID = 1L;

					public void setBorder(Border b) {
					}
				};
			}
		});
		jSplitPane.setBorder(null);
	}

	private void listItemSelected(ListSelectionEvent evt) {
		if (!evt.getValueIsAdjusting()) {
			LoadedResource lr = (LoadedResource) list.getSelectedValue();
			if (lr != null) {
				outputContent.setText(lr.getOutputAsText());
				inputContent.setText(lr.getInputAsText());
			}
		}
	}

	public void setProxy(AjaxProxy proxy) {
		if (proxy != null)
			proxy.addTracker(this);
	}

	@Override
	public void trackFile(LoadedResource res) {
		if (toggleBtn.isSelected()) {
			model.addElement(res);
		}
	}

}
