package com.thedeanda.ajaxproxy.ui.tracker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;

import com.thedeanda.ajaxproxy.AccessTracker;
import com.thedeanda.ajaxproxy.AjaxProxy;
import com.thedeanda.ajaxproxy.LoadedResource;
import com.thedeanda.javajson.JsonObject;

/** tracks files that get loaded */
public class FileTrackerPanel extends JPanel implements AccessTracker {
	private static final long serialVersionUID = 1L;
	private JButton clearBtn;
	private JCheckBox toggleBtn;
	private FileTrackerTableModel model;

	public FileTrackerPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		model = new FileTrackerTableModel();

		this.clearBtn = new JButton("Clear");
		toggleBtn = new JCheckBox("Track Files");
		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.clear();
			}
		});
		JScrollPane scroll = new JScrollPane(new JTable(model,
				new FileTrackerColumnModel()));

		add(clearBtn);
		add(toggleBtn);
		add(scroll);

		layout.putConstraint(SpringLayout.NORTH, clearBtn, 20,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, clearBtn, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, toggleBtn, 20,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, toggleBtn, -10,
				SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, scroll, 30,
				SpringLayout.SOUTH, clearBtn);
		layout.putConstraint(SpringLayout.SOUTH, scroll, -10, SpringLayout.SOUTH,
				this);
		layout.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.EAST, scroll, -10, SpringLayout.EAST,
				this);
	}

	public void setProxy(AjaxProxy proxy) {
		//if (proxy != null)
		//	proxy.addTracker(this);
	}

	@Override
	public void trackFile(LoadedResource res) {
		if (toggleBtn.isSelected()) {
			model.trackFile(res.getPath(), res.getDuration());
		}
	}

	public JsonObject getConfig() {
		JsonObject data = new JsonObject();
		data.put("track", toggleBtn.isSelected());
		return data;
	}

	public void setConfig(JsonObject config) {
		if (config == null)
			return;
		toggleBtn.setSelected(config.getBoolean("track"));
	}

}
