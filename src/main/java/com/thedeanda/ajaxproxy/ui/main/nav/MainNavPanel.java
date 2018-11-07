package com.thedeanda.ajaxproxy.ui.main.nav;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.*;

import com.thedeanda.ajaxproxy.ui.border.BottomBorder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainNavPanel extends JPanel {
	private static final long serialVersionUID = -844239892839389886L;
	private List<JComponent> buttons = new ArrayList<>();

	private Set<NavListener> listeners = new HashSet<>();

	private boolean started = false;
	private JButton startButton;
	private static final String START_LABEL = "Start";
	private static final String STOP_LABEL = "Stop";

	public MainNavPanel() {
		JPanel panel = this;

		createNavButton(this, "Server", "Server", NavItem.Server, 0);
		createNavButton(this, "Requests", "Request Viewer", NavItem.RequestViewer, 0);
		createNavButton(this, "Logger", "Logger", NavItem.Logger, 0);

		initStartButton(panel);

		resetLayout(this);
	}

	public void selectNavItem(NavItem ni, int index) {
		if (ni == NavItem.Start || ni == NavItem.Stop) {
			started = (ni == NavItem.Start);
			updateStartButton();
		} else {
			// TODO: implement this mode
		}
	}

	private void initStartButton(JPanel panel) {
		startButton = new JButton(START_LABEL);
		panel.add(startButton);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleStart();
			}
		});
	}

	private void toggleStart() {
		started = !started;
		if (started) {
			fireSelected(NavItem.Start, 0);
		} else {
			fireSelected(NavItem.Stop, 0);
		}
		updateStartButton();
	}

	private void updateStartButton() {
		if (started) {
			startButton.setText(STOP_LABEL);
		} else {
			startButton.setText(START_LABEL);
		}

	}

	public void addNavListener(NavListener listener) {
		listeners.add(listener);
	}

	private JComponent createNavButton(JPanel pnl, String label, final String tooltip, final NavItem navItem,
			final int index) {
		JComponent newButton = null;
		if (navItem == NavItem.Server) {
			newButton = createServerButton(pnl, label, tooltip, navItem, index);
		} else {
			final JToggleButton btn = new JToggleButton(label);
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					deselectAll(btn);
					fireSelected(navItem, index);
				}
			});
			btn.setToolTipText(tooltip);
			newButton = btn;
		}
		buttons.add(newButton);
		pnl.add(newButton);
		return newButton;
	}

	private JComponent createServerButton(JPanel pnl, String label, final String tooltip, final NavItem navItem,
										  final int index) {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		panel.setBorder(new BottomBorder());


		JLabel lbl = new JLabel(label);
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lbl);

		JToolBar toolbar = new JToolBar("Toolbar");
		toolbar.setFloatable(false);
		toolbar.setBorderPainted(false);
		add(toolbar);


		JButton editButton = makeNavigationButton("server_edit.png", "EDIT", "Server Settings");
		/*
		view.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deselectAll(view);
				fireSelected(navItem, index);
			}
		});
		*/
		toolbar.add(editButton);

		JButton editButton2 = makeNavigationButton("control_play.png", "RUN", "Start Server");
		toolbar.add(editButton2);
		JButton editButton3 = makeNavigationButton("control_stop.png", "STOP", "Stop Server");
		toolbar.add(editButton3);
		editButton3.setEnabled(false);



		panel.add(toolbar);


		layout.putConstraint(SpringLayout.NORTH, lbl, 0, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, lbl, 0, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, lbl, 0, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, lbl, 25, SpringLayout.NORTH, lbl);

		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, toolbar, 0, SpringLayout.HORIZONTAL_CENTER, panel);
		//layout.putConstraint(SpringLayout.WEST, editButton, 0, SpringLayout.WEST, panel);
		//layout.putConstraint(SpringLayout.EAST, editButton, 0, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, toolbar, 0, SpringLayout.SOUTH, panel);

		panel.setPreferredSize(new Dimension(100, 60));
		return panel;
	}

	protected JButton makeNavigationButton(String imageName, String actionCommand, String toolTipText) {
		// Look for the image.
		String imgLocation = "/images/" + imageName;
		URL imageURL = getClass().getResource(imgLocation);

		// Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		//button.addActionListener(this);

		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, toolTipText));
		} else { // no image found
			button.setText(toolTipText);
			log.warn("Resource not found: {}", imgLocation);
		}

		return button;
	}

	private void fireSelected(NavItem navItem, int index) {
		log.debug("nav item changed to: {}, {}", navItem, index);
		for (NavListener l : listeners) {
			l.navEvent(navItem, index);
		}
	}

	private void deselectAll(JToggleButton keepSelected) {
		/*
		for (JToggleButton btn : buttons) {
			if (btn != keepSelected) {
				btn.setSelected(false);
			}
		}
		//*/
	}

	private void resetLayout(JPanel panel) {
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);

		JComponent lastButton = null;
		for (JComponent btn : buttons) {
			if (lastButton == null) {
				layout.putConstraint(SpringLayout.NORTH, btn, 15, SpringLayout.NORTH, panel);
			} else {
				layout.putConstraint(SpringLayout.NORTH, btn, 10, SpringLayout.SOUTH, lastButton);
			}
			// layout.putConstraint(SpringLayout.SOUTH, btn, 0, SpringLayout.SOUTH, panel);

			layout.putConstraint(SpringLayout.WEST, btn, 0, SpringLayout.WEST, panel);
			layout.putConstraint(SpringLayout.EAST, btn, -3, SpringLayout.EAST, panel);

			lastButton = btn;
		}

		layout.putConstraint(SpringLayout.WEST, startButton, 5, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, startButton, -8, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, startButton, -10, SpringLayout.SOUTH, panel);

		// layout.putConstraint(SpringLayout.SOUTH, panel, 100, SpringLayout.SOUTH,
		// lastButton);

		// TODO: calculate this based on total number of buttons
		setPreferredSize(new Dimension(1, 200));
	}
}
