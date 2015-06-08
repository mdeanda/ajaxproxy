package com.thedeanda.ajaxproxy.ui.viewer;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.thedeanda.ajaxproxy.ui.model.Resource;

public class ResourceCellRenderer extends JPanel implements ListCellRenderer {
	private static final long serialVersionUID = -3020786707630237791L;
	private JLabel path;

	public ResourceCellRenderer() {
		setLayout(new BorderLayout());

		path = new JLabel();
		add(path, BorderLayout.CENTER);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		
		Resource resource = (Resource) value;
		
		path.setText(resource.getLoadedResource().getPath());

		return this;
	}

}
