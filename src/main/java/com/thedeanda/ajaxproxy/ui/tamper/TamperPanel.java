package com.thedeanda.ajaxproxy.ui.tamper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.model.tamper.TamperSelector;
import com.thedeanda.ajaxproxy.ui.SwingUtils;
import com.thedeanda.ajaxproxy.ui.border.BottomBorder;

public class TamperPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7117672845529225463L;
	private static final Logger log = LoggerFactory
			.getLogger(TamperPanel.class);
	private JButton clearBtn;
	private JButton exportBtn;
	private JButton importBtn;
	private JPanel topPanel;
	private JButton addBtn;
	private JSplitPane split;

	public TamperPanel() {
		log.debug("new tamper panel");

		initComponents();
		initLayout();
	}

	private void initComponents() {
		initComponentsTopPanel();

		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(new JButton("left"));
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

		addBtn.addActionListener(this);
	}

	private void initLayout() {
		initLayoutTop();

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

	public void showAddDialog() {
		TamperSelector selector = TamperSelectorDialog.showDialog(this, this);
		log.debug("selector: {}", selector);
	}
}
