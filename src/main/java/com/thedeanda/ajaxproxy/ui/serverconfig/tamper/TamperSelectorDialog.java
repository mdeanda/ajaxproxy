package com.thedeanda.ajaxproxy.ui.serverconfig.tamper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.model.tamper.TamperSelector;
import com.thedeanda.ajaxproxy.ui.SwingUtils;

public class TamperSelectorDialog extends JDialog implements ActionListener {
	private static final Logger log = LoggerFactory
			.getLogger(TamperSelectorDialog.class);
	private static final long serialVersionUID = 1L;
	private TamperSelector value = null;
	private JButton saveButton;
	private JButton cancelButton;
	private JPanel buttonPane;
	private JTextField txtName;
	private JPanel formPanel;
	private JTextField txtPath;

	public static TamperSelector showDialog(Component frameComp) {

		String title = "Add Tamper";
		TamperSelector initialValue = new TamperSelector();

		Frame frame = JOptionPane.getFrameForComponent(frameComp);
		TamperSelectorDialog dialog = new TamperSelectorDialog(frame,
				frameComp, title, initialValue);
		dialog.setVisible(true);
		return dialog.getValue();
	}

	private TamperSelectorDialog(Frame frame, Component locationComp,
			String title, TamperSelector initialValue) {
		super(frame, title, true);

		Container contentPane = getContentPane();
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		initComponenets();
		initLayout(panel);
		getRootPane().setDefaultButton(saveButton);

		// Initialize values.
		// setValue(initialValue);

		setMinimumSize(new Dimension(250, 180));
		setPreferredSize(new Dimension(400, 180));
		pack();
		setLocationRelativeTo(locationComp);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object source = e.getSource();
		if (source == cancelButton) {
			doCancel();
		} else if (source == saveButton) {
			doSave();
		}
	}

	private void initComponenets() {
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);

		saveButton = new JButton("Save");
		saveButton.setActionCommand("Save");
		saveButton.addActionListener(this);

		buttonPane = new JPanel();

		formPanel = new JPanel();
		txtName = SwingUtils.newJTextField();
		txtPath = SwingUtils.newJTextField();
	}

	private void initLayout(Container panel) {
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		initFormLayout();
		initButtonLayout();

		panel.add(formPanel);
		panel.add(buttonPane);

		layout.putConstraint(SpringLayout.NORTH, formPanel, 10,
				SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.SOUTH, formPanel, -10,
				SpringLayout.NORTH, buttonPane);
		layout.putConstraint(SpringLayout.WEST, formPanel, 10,
				SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, formPanel, -10,
				SpringLayout.EAST, panel);

		layout.putConstraint(SpringLayout.SOUTH, buttonPane, 0,
				SpringLayout.SOUTH, panel);
		layout.putConstraint(SpringLayout.WEST, buttonPane, 0,
				SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, buttonPane, 0,
				SpringLayout.EAST, panel);
	}

	private void initFormLayout() {
		JPanel panel = formPanel;
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		JLabel lbl = new JLabel("Tamper Name");
		panel.add(lbl);
		panel.add(txtName);

		JLabel lblPath = new JLabel("Path");
		panel.add(lblPath);
		panel.add(txtPath);

		layout.putConstraint(SpringLayout.VERTICAL_CENTER, lbl, 0,
				SpringLayout.VERTICAL_CENTER, txtName);
		layout.putConstraint(SpringLayout.WEST, lbl, 0, SpringLayout.WEST,
				panel);
		layout.putConstraint(SpringLayout.EAST, lbl, 100, SpringLayout.WEST,
				panel);

		layout.putConstraint(SpringLayout.NORTH, txtName, 10,
				SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, txtName, 10, SpringLayout.EAST,
				lbl);
		layout.putConstraint(SpringLayout.EAST, txtName, 0, SpringLayout.EAST,
				panel);

		layout.putConstraint(SpringLayout.VERTICAL_CENTER, lblPath, 0,
				SpringLayout.VERTICAL_CENTER, txtPath);
		layout.putConstraint(SpringLayout.WEST, lblPath, 0, SpringLayout.WEST,
				panel);
		layout.putConstraint(SpringLayout.EAST, lblPath, 0, SpringLayout.EAST,
				lbl);

		layout.putConstraint(SpringLayout.NORTH, txtPath, 20,
				SpringLayout.SOUTH, txtName);
		layout.putConstraint(SpringLayout.WEST, txtPath, 0, SpringLayout.WEST,
				txtName);
		layout.putConstraint(SpringLayout.EAST, txtPath, 0, SpringLayout.EAST,
				panel);

	}

	private void initButtonLayout() {
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(cancelButton);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(saveButton);
	}
	
	public void doCancel() {
		value = null;
		setVisible(false);		
	}
	
	public void doSave() {
		//TODO: validation and block
		
		value = new TamperSelector();
		value.setName(txtName.getText());
		value.setPathRegEx(txtPath.getText());
		setVisible(false);
	}

	public TamperSelector getValue() {
		return value;
	}
}
