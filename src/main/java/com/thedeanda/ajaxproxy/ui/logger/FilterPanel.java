package com.thedeanda.ajaxproxy.ui.logger;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JButton;
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

public class FilterPanel extends JPanel {
	private static final Logger log = LoggerFactory.getLogger(FilterPanel.class);
	private static final long serialVersionUID = 3266059093827619992L;

	private JTextField filter;
	private CheckComboBox tagFilter;
	private CheckComboBox uidFilter;
	private JButton clearBtn;
	private JButton exportBtn;

	private Color filterOkColor;
	private Color filterBadColor;

	private LoggerTableModel model;
	private ListCheckModel tagModel;
	private ListCheckModel uidModel;

	public FilterPanel() {
		log.warn("new filter panel");

		initButtons();
		initLayout();

		setBorder(new BottomBorder());
	}

	private void initLayout() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		JLabel lbl = new JLabel("Filter");
		add(lbl);

		layout.putConstraint(SpringLayout.BASELINE, lbl, 0, SpringLayout.BASELINE, clearBtn);
		layout.putConstraint(SpringLayout.WEST, lbl, 10, SpringLayout.WEST, this);

		layout.putConstraint(SpringLayout.BASELINE, filter, 0, SpringLayout.BASELINE, lbl);
		layout.putConstraint(SpringLayout.WEST, filter, 10, SpringLayout.EAST, lbl);
		layout.putConstraint(SpringLayout.EAST, filter, 200, SpringLayout.WEST, filter);

		layout.putConstraint(SpringLayout.NORTH, tagFilter, 0, SpringLayout.NORTH, filter);
		layout.putConstraint(SpringLayout.WEST, tagFilter, 10, SpringLayout.EAST, filter);
		layout.putConstraint(SpringLayout.EAST, tagFilter, 100, SpringLayout.WEST, tagFilter);

		layout.putConstraint(SpringLayout.NORTH, uidFilter, 0, SpringLayout.NORTH, filter);
		layout.putConstraint(SpringLayout.WEST, uidFilter, 10, SpringLayout.EAST, tagFilter);
		layout.putConstraint(SpringLayout.EAST, uidFilter, 100, SpringLayout.WEST, uidFilter);

		layout.putConstraint(SpringLayout.NORTH, clearBtn, 20, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, clearBtn, 10, SpringLayout.EAST, uidFilter);
		layout.putConstraint(SpringLayout.NORTH, exportBtn, 0, SpringLayout.NORTH, clearBtn);
		layout.putConstraint(SpringLayout.WEST, exportBtn, 10, SpringLayout.EAST, clearBtn);
	}

	private void initButtons() {
		filter = new JTextField(".*");
		SwingUtils.prepJTextField(filter);
		filter.setToolTipText("Filter path by java regex");
		add(filter);

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

		tagFilter = new CheckComboBox();
		tagFilter.setTextFor(CheckComboBox.NONE, "-- ANY --");
		tagFilter.setTextFor(CheckComboBox.MULTIPLE, "...");
		tagFilter.setTextFor(CheckComboBox.ALL, "-- ALL --");
		add(tagFilter);

		uidFilter = new CheckComboBox();
		uidFilter.setTextFor(CheckComboBox.NONE, "-- ANY --");
		uidFilter.setTextFor(CheckComboBox.MULTIPLE, "...");
		uidFilter.setTextFor(CheckComboBox.ALL, "-- ALL --");
		add(uidFilter);

		clearBtn = new JButton("Clear");

		exportBtn = new JButton("Export");
		exportBtn.setEnabled(false);

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

		add(clearBtn);
		add(exportBtn);
	}

	private List<String> getCheckedItems(CheckComboBox checkbox) {
		final List<String> checkedItems = new ArrayList<>();

		List<Object> checked = checkbox.getModel().getCheckeds();
		if (checked != null && !checked.isEmpty()) {
			for (Object o : checked) {
				checkedItems.add((String) o);
			}
		}

		return checkedItems;
	}

	private void resetFilter() {
		// TODO: throttle processing here since it works as you type

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
		final List<String> checkedTags = getCheckedItems(tagFilter);
		final List<String> checkedUids = getCheckedItems(uidFilter);

		final Pattern filter = filterRegEx;
		SwingUtils.executNonUi(new Runnable() {
			@Override
			public void run() {
				if (model != null) {
					model.setFilter(filter, checkedTags, checkedUids);
				}
			}
		});
	}

	public void setModel(LoggerTableModel model) {
		this.model = model;
		tagModel = tagFilter.getModel();
		model.setTagModel(tagModel);

		tagModel.addListCheckListener(new ListCheckListener() {

			@Override
			public void removeCheck(ListEvent event) {
				resetFilter();
			}

			@Override
			public void addCheck(ListEvent event) {
				resetFilter();
			}
		});

		uidModel = uidFilter.getModel();
		model.setUidModel(uidModel);

		uidModel.addListCheckListener(new ListCheckListener() {

			@Override
			public void removeCheck(ListEvent event) {
				resetFilter();
			}

			@Override
			public void addCheck(ListEvent event) {
				resetFilter();
			}
		});

	}
}
