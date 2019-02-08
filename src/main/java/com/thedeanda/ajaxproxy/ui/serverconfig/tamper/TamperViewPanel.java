package com.thedeanda.ajaxproxy.ui.serverconfig.tamper;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.thedeanda.ajaxproxy.model.tamper.TamperItem;
import com.thedeanda.ajaxproxy.model.tamper.TamperSelector;

public class TamperViewPanel extends JPanel {
	private static final long serialVersionUID = -7865500250217198161L;
	private JLabel valName;
	private JLabel valPath;
	private JLabel lblName;
	private JLabel lblPath;

	public TamperViewPanel() {
		
		lblName = new JLabel("Name");
		lblPath = new JLabel("Path");
		valName = new JLabel();
		valPath = new JLabel();
		
		initLayout();
	}
	
	private void initLayout() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		add(lblName);
		add(valName);
		add(lblPath);
		add(valPath);
		
		layout.putConstraint(SpringLayout.NORTH, lblName, 10,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, lblName, 10, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.EAST, lblName, 80, SpringLayout.WEST,
				this);
		
		layout.putConstraint(SpringLayout.NORTH, valName, 0,
				SpringLayout.NORTH, lblName);
		layout.putConstraint(SpringLayout.WEST, valName, 10, SpringLayout.EAST,
				lblName);
		layout.putConstraint(SpringLayout.EAST, valName, -10, SpringLayout.EAST,
				this);

		
		layout.putConstraint(SpringLayout.NORTH, lblPath, 10,
				SpringLayout.SOUTH, lblName);
		layout.putConstraint(SpringLayout.WEST, lblPath, 10, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.EAST, lblPath, 0, SpringLayout.EAST,
				lblName);
		
		layout.putConstraint(SpringLayout.NORTH, valPath, 0,
				SpringLayout.NORTH, lblPath);
		layout.putConstraint(SpringLayout.WEST, valPath, 10, SpringLayout.EAST,
				lblPath);
		layout.putConstraint(SpringLayout.EAST, valPath, -10, SpringLayout.EAST,
				this);
	}
	
	public void showTamper(TamperItem tamper) {
		if (tamper == null) {
			
		} else {
			TamperSelector selector = tamper.getSelector();
			valName.setText(selector.getName());
			valPath.setText(selector.getPathRegEx());
		}
	}
}
