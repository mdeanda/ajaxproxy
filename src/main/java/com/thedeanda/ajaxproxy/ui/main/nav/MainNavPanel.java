package com.thedeanda.ajaxproxy.ui.main.nav;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainNavPanel extends JPanel {
	private static final long serialVersionUID = -844239892839389886L;
	private List<JToggleButton> buttons = new ArrayList<>();

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
		createNavButton(this, "Help", "Help", NavItem.Help, 0);

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
		final JToggleButton btn = new JToggleButton(label);
		// btn.setPreferredSize(btn.getMaximumSize());
		buttons.add(btn);
		btn.setToolTipText(tooltip);
		pnl.add(btn);

		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deselectAll(btn);
				fireSelected(navItem, index);
			}
		});

		return btn;
	}

	private void fireSelected(NavItem navItem, int index) {
		log.debug("nav item changed to: {}, {}", navItem, index);
		for (NavListener l : listeners) {
			l.navEvent(navItem, index);
		}
	}

	private void deselectAll(JToggleButton keepSelected) {
		for (JToggleButton btn : buttons) {
			if (btn != keepSelected) {
				btn.setSelected(false);
			}
		}
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

			layout.putConstraint(SpringLayout.WEST, btn, 5, SpringLayout.WEST, panel);
			layout.putConstraint(SpringLayout.EAST, btn, -8, SpringLayout.EAST, panel);

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
