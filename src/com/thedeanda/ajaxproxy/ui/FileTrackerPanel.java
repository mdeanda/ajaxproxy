package com.thedeanda.ajaxproxy.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;

import com.thedeanda.ajaxproxy.AccessTracker;
import com.thedeanda.ajaxproxy.AjaxProxy;

/** tracks files that get loaded */
public class FileTrackerPanel extends JPanel implements AccessTracker {
	private static final long serialVersionUID = 1L;
	private JButton clearBtn;
	private JCheckBox toggleBtn;
	private FileTrackerTableModel model;

	public FileTrackerPanel() {
		this.setLayout(new MigLayout("fill", "", "[][fill]"));
		model = new FileTrackerTableModel();

		this.clearBtn = new JButton("Clear");
		toggleBtn = new JCheckBox("Track Files");
		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.clear();
			}
		});

		add(clearBtn);
		add(toggleBtn, "align right, wrap");

		add(new JScrollPane(new JTable(model, new FileTrackerColumnModel())),
				"span 2, growx, growy");
	}

	public void setProxy(AjaxProxy proxy) {
		if (proxy != null)
			proxy.addTracker(this);
	}

	@Override
	public void trackFile(String url, int duration) {
		if (toggleBtn.isSelected()) {
			model.trackFile(url, duration);
		}
	}

}
