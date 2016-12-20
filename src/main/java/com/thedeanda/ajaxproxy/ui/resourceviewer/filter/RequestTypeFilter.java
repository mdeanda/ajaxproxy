package com.thedeanda.ajaxproxy.ui.resourceviewer.filter;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class RequestTypeFilter extends JComboBox<RequestType> {
	private static final long serialVersionUID = -3605643446869486707L;

	public RequestTypeFilter() {
		DefaultComboBoxModel<RequestType> model = new DefaultComboBoxModel<>();
		for (RequestType rt : RequestType.values()) {
			model.addElement(rt);
		}
		setModel(model);

		setRenderer(new ComboBoxRenderer());
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				itemSelected();
			}
		});
	}

	private void itemSelected() {
		if (getSelectedItem() instanceof JCheckBox) {
			JCheckBox jcb = (JCheckBox) getSelectedItem();
			jcb.setSelected(!jcb.isSelected());
		}
	}

	class ComboBoxRenderer implements ListCellRenderer<RequestType> {
		private JCheckBox checkbox;

		public ComboBoxRenderer() {
			setOpaque(true);
			checkbox = new JCheckBox();
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends RequestType> list, RequestType value, int index,
				boolean isSelected, boolean cellHasFocus) {
			Component c = checkbox;
			if (isSelected) {
				c.setBackground(list.getSelectionBackground());
				c.setForeground(list.getSelectionForeground());
			} else {
				c.setBackground(list.getBackground());
				c.setForeground(list.getForeground());
			}
			return c;
		}

	}
}
