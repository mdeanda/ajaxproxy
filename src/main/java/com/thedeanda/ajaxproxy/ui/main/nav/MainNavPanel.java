package com.thedeanda.ajaxproxy.ui.main.nav;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	public MainNavPanel() {
		JPanel panel = this;

		createNavButton(this, "Server", NavItem.Server, 0);
		createNavButton(this, "Request Viewer", NavItem.RequestViewer, 0);
		createNavButton(this, "Logger", NavItem.Logger, 0);

		resetLayout(this);

		//setBackground(getBackground().darker());
	}

	public void addNavListener(NavListener listener) {
		listeners.add(listener);
	}

	private JComponent createNavButton(JPanel pnl, String label, final NavItem navItem, final int index) {
		final JToggleButton btn = new JToggleButton(label);
		// btn.setPreferredSize(btn.getMaximumSize());
		buttons.add(btn);
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
			layout.putConstraint(SpringLayout.EAST, btn, -5, SpringLayout.EAST, panel);

			lastButton = btn;
		}
		layout.putConstraint(SpringLayout.SOUTH, panel, 10, SpringLayout.SOUTH, lastButton);
	}
}
