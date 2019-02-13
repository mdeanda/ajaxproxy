package com.thedeanda.ajaxproxy.ui.serverconfig.variable;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.thedeanda.ajaxproxy.ui.util.SwingUtils;
import com.thedeanda.ajaxproxy.ui.serverconfig.variable.model.Variable;

public class VariableEditor extends JPanel {
	private SpringLayout layout;
	private JLabel keyLabel;
	private JTextField keyField;
	private JLabel valueLabel;
	private JTextField valueField;
	private JButton btn;
	private Variable originalValue;
	private VariablesPanel panel;

	public VariableEditor(VariablesPanel panel) {
		this.panel = panel;
		layout = new SpringLayout();
		setLayout(layout);

		keyLabel = new JLabel("Key");
		keyField = SwingUtils.newJTextField();
		add(keyLabel);
		add(keyField);
		addListeners(keyField);

		valueLabel = new JLabel("Value");
		valueField = SwingUtils.newJTextField();
		add(valueLabel);
		add(valueField);
		addListeners(valueField);

		btn = new JButton("Ok");
		btn.setMargin(new Insets(2, 14, 2, 14));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				commitEdit();
			}
		});
		add(btn);

		initLayout();

	}

	private void commitEdit() {
		panel.changeValue(originalValue.getKey(), keyField.getText(), valueField.getText());
	}

	private void initLayout() {
		JLabel[] labels = new JLabel[] { keyLabel, valueLabel };
		Component[] fields = new Component[] { keyField, valueField };
		int[] cols = new int[] { 350, 350 };

		for (int i = cols.length - 1; i >= 0; i--) {
			JLabel lbl = labels[i];
			Component fld = fields[i];
			if (i == 0) {
				layout.putConstraint(SpringLayout.NORTH, lbl, 0, SpringLayout.NORTH, this);

				layout.putConstraint(SpringLayout.NORTH, fld, 2, SpringLayout.SOUTH, lbl);

				layout.putConstraint(SpringLayout.WEST, fld, 0, SpringLayout.WEST, this);

				layout.putConstraint(SpringLayout.WEST, lbl, 0, SpringLayout.WEST, this);
			} else {
				layout.putConstraint(SpringLayout.BASELINE, fld, 0, SpringLayout.BASELINE, fields[0]);

				layout.putConstraint(SpringLayout.WEST, fld, 10, SpringLayout.EAST, fields[i - 1]);
				if (lbl != null) {
					layout.putConstraint(SpringLayout.VERTICAL_CENTER, lbl, 0, SpringLayout.VERTICAL_CENTER, labels[0]);
					layout.putConstraint(SpringLayout.WEST, lbl, 0, SpringLayout.WEST, fld);
				}
			}
			if (i == cols.length - 1) {
				layout.putConstraint(SpringLayout.EAST, fld, -10, SpringLayout.WEST, btn);
			} else {
				layout.putConstraint(SpringLayout.EAST, fld, cols[i], SpringLayout.WEST, fld);
			}

		}

		layout.putConstraint(SpringLayout.BASELINE, btn, 0, SpringLayout.BASELINE, keyField);
		layout.putConstraint(SpringLayout.EAST, btn, 0, SpringLayout.EAST, this);

	}

	private void addListeners(final JTextField txtField) {
		txtField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					commitEdit();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void startEdit(Variable value) {
		this.originalValue = value;
		
		keyField.setText(value.getKey());
		valueField.setText(value.getValue());
	}
}
