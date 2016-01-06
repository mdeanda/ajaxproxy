package com.thedeanda.ajaxproxy.ui;

import java.awt.Dimension;

import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GeneralPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField port;
	private JTextField resourceBase;
	private JCheckBox indexCheck;

	public GeneralPanel(final SettingsChangedListener listener) {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		// TODO: track changes to text fields

		port = SwingUtils.newJTextField();

		JLabel portLabel = new JLabel("Local Port");
		portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		Dimension size = portLabel.getPreferredSize();
		size.width = 150;
		portLabel.setPreferredSize(size);

		add(portLabel);
		add(port);

		resourceBase = SwingUtils.newJTextField();
		JLabel baseLabel = new JLabel("Resource Base");
		add(baseLabel);
		add(resourceBase);

		indexCheck = new JCheckBox("Show Directory Index");
		add(indexCheck);
		indexCheck.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = ((JCheckBox) e.getSource()).getModel();
				if (model.isPressed()) {
					listener.restartRequired();
				}
			}
		});

		layout.putConstraint(SpringLayout.WEST, portLabel, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.WEST, port, 5, SpringLayout.EAST,
				portLabel);
		layout.putConstraint(SpringLayout.NORTH, port, 60, SpringLayout.NORTH,
				this);
		layout.putConstraint(SpringLayout.EAST, port, -10, SpringLayout.EAST,
				this);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, portLabel, 0,
				SpringLayout.VERTICAL_CENTER, port);

		layout.putConstraint(SpringLayout.NORTH, resourceBase, 20,
				SpringLayout.SOUTH, port);
		layout.putConstraint(SpringLayout.WEST, resourceBase, 0,
				SpringLayout.WEST, port);
		layout.putConstraint(SpringLayout.EAST, resourceBase, 0,
				SpringLayout.EAST, port);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, baseLabel, 0,
				SpringLayout.VERTICAL_CENTER, resourceBase);
		layout.putConstraint(SpringLayout.EAST, baseLabel, 0,
				SpringLayout.EAST, portLabel);

		layout.putConstraint(SpringLayout.NORTH, indexCheck, 20,
				SpringLayout.SOUTH, resourceBase);
		layout.putConstraint(SpringLayout.WEST, indexCheck, 0,
				SpringLayout.WEST, resourceBase);


	}

	public String getResourceBase() {
		return resourceBase.getText();
	}

	public int getPort() {
		return Integer.parseInt(port.getText());
	}

	public void setResourceBase(String rb) {
		resourceBase.setText(rb);
	}

	public void setPort(int port) {
		this.port.setText(String.valueOf(port));
	}

	public void setShowIndex(boolean showIndex) {
		indexCheck.setSelected(showIndex);
	}

	public boolean isShowIndex() {
		return indexCheck.isSelected();
	}

}
