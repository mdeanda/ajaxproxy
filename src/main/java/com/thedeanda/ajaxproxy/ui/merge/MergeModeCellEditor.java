package com.thedeanda.ajaxproxy.ui.merge;

import com.thedeanda.ajaxproxy.config.model.MergeMode;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MergeModeCellEditor extends AbstractCellEditor implements
		TableCellEditor, TableCellRenderer {
	private static final long serialVersionUID = 1L;
	private JComboBox<Object> comp;
	private DefaultComboBoxModel<Object> model;

	public MergeModeCellEditor() {
		model = new DefaultComboBoxModel<Object>();
		model.addElement("");
		for (MergeMode mm : MergeMode.values()) {
			model.addElement(mm);
		}

		comp = new JComboBox<>(model);

	}

	@Override
	public Object getCellEditorValue() {

		return model.getSelectedItem();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean selected, int rowIndex, int colIndex) {
		// TODO Auto-generated method stub
		return comp;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean selected, boolean focus, int rowIndex, int colIndex) {

		return comp;
	}

}
