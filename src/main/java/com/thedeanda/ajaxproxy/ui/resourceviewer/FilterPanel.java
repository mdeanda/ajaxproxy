package com.thedeanda.ajaxproxy.ui.resourceviewer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.StringUtils;
import org.japura.gui.CheckComboBox;
import org.japura.gui.event.ListCheckListener;
import org.japura.gui.event.ListEvent;
import org.japura.gui.model.ListCheckModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.ui.SwingUtils;
import com.thedeanda.ajaxproxy.ui.border.BottomBorder;
import com.thedeanda.ajaxproxy.ui.model.ResourceListModel;
import com.thedeanda.ajaxproxy.ui.resourceviewer.filter.RequestType;

public class FilterPanel extends JPanel {
	private static final Logger log = LoggerFactory.getLogger(FilterPanel.class);
	private static final long serialVersionUID = 3266059093827619992L;

	private JTextField filterOut;
	private JTextField filter;
	private CheckComboBox rtFilter;
	private JButton clearBtn;
	private JButton exportBtn;
	private JCheckBox toggleBtn;

	private Color filterOkColor;
	private Color filterBadColor;

	private ResourceListModel model;
	private ListCheckModel requestTypeFilterModel;

	public FilterPanel() {
		log.warn("new filter panel");

		initButtons();
		initLayout();

		setBorder(new BottomBorder());
	}

	private void initLayout() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		JLabel lbl = new JLabel("Exclude");
		add(lbl);

		JLabel lbl2 = new JLabel("Include");
		add(lbl2);

		layout.putConstraint(SpringLayout.BASELINE, lbl, 0, SpringLayout.BASELINE, clearBtn);
		layout.putConstraint(SpringLayout.WEST, lbl, 10, SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.BASELINE, filterOut, 0, SpringLayout.BASELINE, lbl);
		layout.putConstraint(SpringLayout.WEST, filterOut, 10, SpringLayout.EAST, lbl);
		layout.putConstraint(SpringLayout.EAST, filterOut, 150, SpringLayout.WEST, filterOut);

		layout.putConstraint(SpringLayout.BASELINE, lbl2, 0, SpringLayout.BASELINE, lbl);
		layout.putConstraint(SpringLayout.WEST, lbl2, 20, SpringLayout.EAST, filterOut);

		layout.putConstraint(SpringLayout.BASELINE, filter, 0, SpringLayout.BASELINE, lbl);
		layout.putConstraint(SpringLayout.WEST, filter, 10, SpringLayout.EAST, lbl2);
		layout.putConstraint(SpringLayout.EAST, filter, 150, SpringLayout.WEST, filter);

		layout.putConstraint(SpringLayout.NORTH, rtFilter, 0, SpringLayout.NORTH, filter);
		layout.putConstraint(SpringLayout.WEST, rtFilter, 10, SpringLayout.EAST, filter);
		layout.putConstraint(SpringLayout.EAST, rtFilter, 100, SpringLayout.WEST, rtFilter);

		layout.putConstraint(SpringLayout.NORTH, clearBtn, 20, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, clearBtn, 40, SpringLayout.EAST, rtFilter);
		layout.putConstraint(SpringLayout.NORTH, exportBtn, 0, SpringLayout.NORTH, clearBtn);
		layout.putConstraint(SpringLayout.WEST, exportBtn, 5, SpringLayout.EAST, clearBtn);
		layout.putConstraint(SpringLayout.BASELINE, toggleBtn, 0, SpringLayout.BASELINE, clearBtn);
		layout.putConstraint(SpringLayout.EAST, toggleBtn, -10, SpringLayout.EAST, this);

	}

	private void initButtons() {
		filter = new JTextField(".*");
		SwingUtils.prepJTextField(filter);
		filter.setToolTipText("Filter path by java regex. Include items that match");
		add(filter);
		
		
		filterOut = new JTextField("");
		SwingUtils.prepJTextField(filterOut);
		filterOut.setToolTipText("Filter path by java regex. Exclude items that match.");
		add(filterOut);

		// Listen for changes in the text
		filterOkColor = filter.getBackground();
		filterBadColor = new Color(250, 210, 200);

		filter.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				resetFilter();
			}

			public void removeUpdate(DocumentEvent e) {
				resetFilter();
			}

			public void insertUpdate(DocumentEvent e) {
				resetFilter();
			}
		});

		filterOut.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				resetFilter();
			}

			public void removeUpdate(DocumentEvent e) {
				resetFilter();
			}

			public void insertUpdate(DocumentEvent e) {
				resetFilter();
			}
		});

		rtFilter = new CheckComboBox();
		rtFilter.setTextFor(CheckComboBox.NONE, "-- ANY --");
		rtFilter.setTextFor(CheckComboBox.MULTIPLE, "...");
		rtFilter.setTextFor(CheckComboBox.ALL, "-- ALL --");

		requestTypeFilterModel = rtFilter.getModel();
		for (RequestType color : RequestType.values()) {
			requestTypeFilterModel.addElement(color);
		}
		requestTypeFilterModel.addListCheckListener(new ListCheckListener() {
			@Override
			public void removeCheck(ListEvent event) {
				resetFilter();
			}

			@Override
			public void addCheck(ListEvent event) {
				resetFilter();
			}
		});

		// rtFilter = new RequestTypeFilter();
		add(rtFilter);
		// rtFilter.setEnabled(false);

		clearBtn = new JButton("Clear");

		exportBtn = new JButton("Export");
		exportBtn.setEnabled(false);

		toggleBtn = new JCheckBox("Monitor Resources");
		toggleBtn.setEnabled(false);

		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (model != null) {
					model.clear();
				}
			}
		});
		exportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// export();
			}
		});
		toggleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// handleMonitorCheckboxChanged();
			}
		});

		add(clearBtn);
		add(exportBtn);
		add(toggleBtn);
	}

	private void resetFilter() {
		// TODO: throttle processing here since it works as you type

		Pattern filterRegExOut = null;
		if (!StringUtils.isBlank(filterOut.getText())) {
			try {
				filterRegExOut = Pattern.compile(filterOut.getText());
				filterOut.setBackground(filterOkColor);
			} catch (PatternSyntaxException ex) {
				filterRegExOut = null;
				filterOut.setBackground(filterBadColor);
			}
		}

		Pattern filterRegEx = null;
		if (!StringUtils.isBlank(filter.getText())) {
			try {
				filterRegEx = Pattern.compile(filter.getText());
				filter.setBackground(filterOkColor);
			} catch (PatternSyntaxException ex) {
				filterRegEx = null;
				filter.setBackground(filterBadColor);
			}
		}

		List<Object> checked = requestTypeFilterModel.getCheckeds();
		final List<RequestType> checkedItems = new ArrayList<>();
		if (checked != null && !checked.isEmpty()) {
			for (Object o : checked) {
				checkedItems.add((RequestType) o);
			}
		}

		final Pattern filter = filterRegEx;
		final Pattern filterOut = filterRegExOut;
		SwingUtils.executNonUi(new Runnable() {
			@Override
			public void run() {
				if (model != null) {
					model.setFilter(filterOut, filter, checkedItems);
				}
			}
		});
	}

	public void setModel(ResourceListModel model) {
		this.model = model;
	}
}
