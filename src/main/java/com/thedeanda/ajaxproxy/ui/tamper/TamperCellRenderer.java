package com.thedeanda.ajaxproxy.ui.tamper;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.thedeanda.ajaxproxy.model.tamper.TamperItem;
import com.thedeanda.ajaxproxy.model.tamper.TamperSelector;

public class TamperCellRenderer extends JPanel implements
		ListCellRenderer<TamperItem> {
	private static final long serialVersionUID = 7193463860745432054L;
	private JLabel name;
	private JLabel path;

	private final Color lightColor = new Color(250, 250, 255);
	private final Color selectedColor = new Color(162, 202, 255);

	public TamperCellRenderer() {
		name = new JLabel();
		path = new JLabel();

		initLayout();
	}

	private void initLayout() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		add(name);
		add(path);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends TamperItem> list, TamperItem value, int index,
			boolean isSelected, boolean cellHasFocus) {
		Color color = list.getBackground();

		if (value != null) {
			TamperSelector selector = value.getSelector();
			name.setText(selector.getName());
			path.setText(selector.getPathRegEx());
		}

		if (isSelected) {
			color = selectedColor;
		} else if (index % 2 == 1) {
			color = lightColor;
		}

		setForeground(list.getForeground());
		setBackground(color);
		return this;
	}

}
