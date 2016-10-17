package com.thedeanda.ajaxproxy.ui.resourceviewer.list;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;

import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.service.ResourceService;
import com.thedeanda.ajaxproxy.service.StoredResource;
import com.thedeanda.ajaxproxy.ui.SwingUtils;
import com.thedeanda.ajaxproxy.ui.model.Resource;
import com.thedeanda.ajaxproxy.ui.model.ResourceListModel;
import com.thedeanda.ajaxproxy.ui.resourceviewer.ResourceFrame;
import com.thedeanda.ajaxproxy.ui.resourceviewer.ResourceViewerPanel;
import com.thedeanda.ajaxproxy.ui.rest.RestClientFrame;
import com.thedeanda.ajaxproxy.ui.viewer.ResourceCellRenderer;

/**
 * filterable list panel on left side if resource viewer panel
 * 
 * @author mdeanda
 *
 */
public class ResourceListPanel extends JPanel implements ActionListener , RequestListener{
	private static final long serialVersionUID = -4795136826991822425L;
	private JTextField filter;
	private Color filterOkColor;
	private Color filterBadColor;
	private JList<Resource> list;
	private JMenuItem replayMenuItem;
	private JMenuItem removeRequestMenuItem;
	private JMenuItem newWindowMenuItem;
	private JMenuItem clearMenuItem;
	private ResourceListModel model;
	private ResourceService resourceService;
	private boolean enableMonitor;

	private ResourceListPanelListener listener;

	public ResourceListPanel(ResourceService resourceService) {
		SpringLayout layout = new SpringLayout();
		JPanel panel = this;
		panel.setLayout(layout);
		panel.setBorder(BorderFactory.createEmptyBorder());

		this.resourceService = resourceService;
		this.model = new ResourceListModel(resourceService);

		filter = new JTextField(".*");
		SwingUtils.prepJTextField(filter);
		filter.setToolTipText("Filter path by java regex");
		panel.add(filter);

		// Listen for changes in the text
		filterOkColor = filter.getBackground();
		filterBadColor = new Color(250, 210, 200);
		filter.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				resetFilter();
			}

			public void removeUpdate(DocumentEvent e) {
				resetFilter();
			}

			public void insertUpdate(DocumentEvent e) {
				resetFilter();
			}
		});

		final JPopupMenu popup = this.createListPopup();

		list = new JList<Resource>(model);
		list.setCellRenderer(new ResourceCellRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				listItemSelected(evt);
			}
		});
		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				list.setSelectedIndex(list.locationToIndex(e.getPoint()));
				if (e.isPopupTrigger() && list.getSelectedIndex() >= 0) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		list.setBorder(BorderFactory.createEmptyBorder());
		JScrollPane scroll = new JScrollPane(list);
		panel.add(scroll);

		layout.putConstraint(SpringLayout.NORTH, filter, 10, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, filter, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, filter, -10, SpringLayout.EAST, panel);

		layout.putConstraint(SpringLayout.NORTH, scroll, 10, SpringLayout.SOUTH, filter);
		layout.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, scroll, -10, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.SOUTH, scroll, -10, SpringLayout.SOUTH, panel);

	}

	private JPopupMenu createListPopup() {
		final JPopupMenu popup = new JPopupMenu();

		replayMenuItem = new JMenuItem("Replay in Rest Client");
		replayMenuItem.addActionListener(this);
		popup.add(replayMenuItem);

		newWindowMenuItem = new JMenuItem("View in New Window");
		newWindowMenuItem.addActionListener(this);
		popup.add(newWindowMenuItem);

		popup.addSeparator();

		removeRequestMenuItem = new JMenuItem("Remove Request");
		removeRequestMenuItem.addActionListener(this);
		popup.add(removeRequestMenuItem);

		clearMenuItem = new JMenuItem("Clear All");
		clearMenuItem.addActionListener(this);
		popup.add(clearMenuItem);

		return popup;
	}

	private void listItemSelected(ListSelectionEvent evt) {
		if (!evt.getValueIsAdjusting()) {
			Resource resource = (Resource) list.getSelectedValue();
			if (resource != null) {
				showResource(resource);
			}
		}
	}

	private void resetFilter() {
		Pattern filterRegEx = null;
		if (!StringUtils.isBlank(filter.getText())) {
			try {
				filterRegEx = Pattern.compile(filter.getText());
				filter.setBackground(filterOkColor);
			} catch (PatternSyntaxException ex) {
				filterRegEx = null;
				filter.setBackground(filterBadColor);
			}
		}
		final Pattern filter = filterRegEx;
		SwingUtils.executNonUi(new Runnable() {
			@Override
			public void run() {
				model.setFilter(filter);
			}
		});
	}

	public void clear() {
		model.clear();
		showResource(null);
	}

	private void showResource(Resource resource) {
		listener.showResource(resource);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == removeRequestMenuItem) {
			int index = list.getSelectedIndex();
			if (index >= 0) {
				model.remove(index);
				if (model.getSize() > index)
					list.setSelectedIndex(index);
				else if (!model.isEmpty()) {
					list.setSelectedIndex(index - 1);
				}
			}
		} else if (evt.getSource() == replayMenuItem) {
			int index = list.getSelectedIndex();
			if (index >= 0) {
				final UUID resourceId = model.get(index).getId();
				SwingUtils.executNonUi(new Runnable() {
					@Override
					public void run() {
						StoredResource resource = resourceService.get(resourceId);
						RestClientFrame rest = new RestClientFrame();
						rest.fromResource(resource);
						rest.setVisible(true);
					}
				});
			}
		} else if (evt.getSource() == clearMenuItem) {
			clear();
		} else if (evt.getSource() == newWindowMenuItem) {
			int index = list.getSelectedIndex();
			if (index >= 0) {
				final Resource resource = model.get(index);
				ResourceFrame window = new ResourceFrame(resourceService, resource);
				window.setVisible(true);
			}
		}
	}

	public void setListener(ResourceListPanelListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void newRequest(UUID id, String url, String method) {
		if (enableMonitor) {
			final Resource resource = new Resource(id, url, method);
			model.add(resource);
		}
	}

	@Override
	public void startRequest(final UUID id, final URL url, final Header[] requestHeaders, final byte[] data) {
		if (enableMonitor) {
			model.startRequest(id, url, requestHeaders, data);
		}
	}

	@Override
	public void requestComplete(final UUID id, final int status, final String reason, final long duration,
			final Header[] responseHeaders, final byte[] data) {
		if (enableMonitor) {
			model.requestComplete(id, status, reason, duration, responseHeaders, data);
		}
	}

	@Override
	public void error(UUID id, String message, Exception e) {
		model.error(id, message, e);
	}

	public boolean isEnableMonitor() {
		return enableMonitor;
	}

	public void setEnableMonitor(boolean enableMonitor) {
		this.enableMonitor = enableMonitor;
	}
}
