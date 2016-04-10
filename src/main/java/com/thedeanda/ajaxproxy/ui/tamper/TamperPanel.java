package com.thedeanda.ajaxproxy.ui.tamper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.model.tamper.TamperConvertor;
import com.thedeanda.ajaxproxy.model.tamper.TamperItem;
import com.thedeanda.ajaxproxy.model.tamper.TamperSelector;
import com.thedeanda.ajaxproxy.ui.SwingUtils;
import com.thedeanda.ajaxproxy.ui.border.BottomBorder;
import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;

public class TamperPanel extends JPanel implements ActionListener {
	private static final String JSON_KEY = "items";
	private static final long serialVersionUID = -7117672845529225463L;
	private static final Logger log = LoggerFactory
			.getLogger(TamperPanel.class);
	private JButton clearBtn;
	private JButton exportBtn;
	private JButton importBtn;
	private JPanel topPanel;
	private JButton addBtn;
	private JSplitPane split;
	private JPanel listPanel;
	private TamperListModel model;
	private JList<TamperItem> list;
	private TamperConvertor convertor = new TamperConvertor();

	public TamperPanel() {
		log.debug("new tamper panel");

		initComponents();
		initLayout();
	}

	private void initComponents() {
		initComponentsTopPanel();

		initComponentsListPanel();

		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(listPanel);
		split.setRightComponent(new JButton("right"));
		split.setDividerLocation(300);
		split.setBorder(BorderFactory.createEmptyBorder());
		SwingUtils.flattenSplitPane(split);

	}

	private void initComponentsTopPanel() {
		topPanel = new JPanel();
		addBtn = new JButton("Add");
		clearBtn = new JButton("Clear");
		exportBtn = new JButton("Export");
		importBtn = new JButton("Import");

		exportBtn.setEnabled(false);
		importBtn.setEnabled(false);

		addBtn.addActionListener(this);
	}

	private void initComponentsListPanel() {
		listPanel = new JPanel();

		model = new TamperListModel();
		list = new JList<TamperItem>(model);

		list.setCellRenderer(new TamperCellRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				listItemSelected(evt);
			}

		});
	}

	private void initLayout() {
		initLayoutTop();
		initLayoutListPanel();

		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		add(topPanel);
		add(split);

		layout.putConstraint(SpringLayout.NORTH, topPanel, 0,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.SOUTH, topPanel, 60,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, topPanel, 0, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.EAST, topPanel, 0, SpringLayout.EAST,
				this);

		layout.putConstraint(SpringLayout.NORTH, split, 0, SpringLayout.SOUTH,
				topPanel);
		layout.putConstraint(SpringLayout.WEST, split, 0, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.EAST, split, 0, SpringLayout.EAST,
				this);
		layout.putConstraint(SpringLayout.SOUTH, split, 0, SpringLayout.SOUTH,
				this);

	}

	private void initLayoutTop() {
		SpringLayout layout = new SpringLayout();
		JPanel panel = topPanel;
		panel.setLayout(layout);
		panel.setBorder(new BottomBorder());

		panel.add(addBtn);
		panel.add(clearBtn);
		panel.add(exportBtn);
		panel.add(importBtn);

		layout.putConstraint(SpringLayout.NORTH, addBtn, 20,
				SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, addBtn, 10, SpringLayout.WEST,
				panel);

		layout.putConstraint(SpringLayout.NORTH, clearBtn, 0,
				SpringLayout.NORTH, addBtn);
		layout.putConstraint(SpringLayout.WEST, clearBtn, 10,
				SpringLayout.EAST, addBtn);

		layout.putConstraint(SpringLayout.NORTH, importBtn, 0,
				SpringLayout.NORTH, clearBtn);
		layout.putConstraint(SpringLayout.WEST, importBtn, 10,
				SpringLayout.EAST, clearBtn);

		layout.putConstraint(SpringLayout.NORTH, exportBtn, 0,
				SpringLayout.NORTH, importBtn);
		layout.putConstraint(SpringLayout.WEST, exportBtn, 10,
				SpringLayout.EAST, importBtn);
	}

	private void initLayoutListPanel() {
		SpringLayout layout = new SpringLayout();
		JPanel panel = listPanel;
		panel.setLayout(layout);

		JScrollPane scroll = new JScrollPane(list);
		panel.add(scroll);

		layout.putConstraint(SpringLayout.NORTH, scroll, 10,
				SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST,
				panel);
		layout.putConstraint(SpringLayout.SOUTH, scroll, -10,
				SpringLayout.SOUTH, panel);
		layout.putConstraint(SpringLayout.EAST, scroll, -10, SpringLayout.EAST,
				panel);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == addBtn) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showAddDialog();
				}
			});
		}
	}

	public JsonObject getConfig() {
		JsonObject json = new JsonObject();
		JsonArray array = new JsonArray();
		for (TamperItem element : model.items()) {
			JsonObject item = convertor.convert(element);
			array.add(item);
		}
		json.put(JSON_KEY, array);

		return json;
	}

	public void setConfig(JsonObject jsonObject) {
		model.clear();

		if (jsonObject == null)
			return;
		JsonArray array = jsonObject.getJsonArray(JSON_KEY);
		if (array == null)
			return;

		for (JsonValue val : array) {
			try {
				TamperItem element = convertor.convertToElement(val
						.getJsonObject());
				if (element != null)
					model.add(element);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
			}
		}
	}

	public void showAddDialog() {
		TamperSelector selector = TamperSelectorDialog.showDialog(this);
		log.debug("selector: {}", selector);

		if (selector != null) {
			TamperItem element = new TamperItem();
			element.setSelector(selector);
			model.add(element);
		}
	}

	private void listItemSelected(ListSelectionEvent evt) {
		// TODO Auto-generated method stub

	}
}
