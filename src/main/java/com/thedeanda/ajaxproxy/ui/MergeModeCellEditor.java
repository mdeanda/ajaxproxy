package com.thedeanda.ajaxproxy.ui;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.thedeanda.ajaxproxy.MergeMode;

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
