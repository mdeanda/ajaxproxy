package com.thedeanda.ajaxproxy.ui.resourceviewer.list;

import com.thedeanda.ajaxproxy.http.RequestListener;
import com.thedeanda.ajaxproxy.service.ResourceService;
import com.thedeanda.ajaxproxy.service.StoredResource;
import com.thedeanda.ajaxproxy.ui.SwingUtils;
import com.thedeanda.ajaxproxy.ui.model.Resource;
import com.thedeanda.ajaxproxy.ui.model.ResourceListModel;
import com.thedeanda.ajaxproxy.ui.resourceviewer.ResourceFrame;
import com.thedeanda.ajaxproxy.ui.rest.RestClientFrame;
import com.thedeanda.ajaxproxy.ui.viewer.ResourceCellRenderer;
import org.apache.http.Header;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.UUID;

/**
 * filterable list panel on left side if resource viewer panel
 *
 * @author mdeanda
 *
 */
public class ResourceListPanel extends JPanel implements ActionListener, RequestListener {
	private static final long serialVersionUID = -4795136826991822425L;
	private JList<Resource> list;
	private JMenuItem replayMenuItem;
	private JMenuItem removeRequestMenuItem;
	private JMenuItem newWindowMenuItem;
	private JMenuItem clearMenuItem;
	private ResourceListModel model;
	private ResourceService resourceService;

	private ResourceListPanelListener listener;
	private JScrollPane scroll;
	private JToolBar toolbar;

	public ResourceListPanel(ResourceListModel model, ResourceService resourceService) {
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);
		this.setBorder(BorderFactory.createEmptyBorder());

		this.model = model;
		this.resourceService = resourceService;
		resourceService.addListener(this);

		addComponents();
		initLayout(layout);
	}

	private void addComponents() {
		final JPopupMenu popup = this.createListPopup();

		initToolbar();

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
		this.scroll = scroll;
		this.add(scroll);

	}

	private void initToolbar() {
		toolbar = new JToolBar("Toolbar");
		toolbar.setFloatable(false);
		add(toolbar);

		JButton button = null;

		button = makeNavigationButton("textfield_delete.png", "EXCLUDE", "Filter - Exclude", "Exclude");
		toolbar.add(button);

		button = makeNavigationButton("textfield_add.png", "INCLUDE", "Filter - Include", "Include");
		toolbar.add(button);

		button = makeNavigationButton("application_view_list_delete.png", "CLEAR", "Clear List", "Clear");
		toolbar.add(button);
	}

	protected JButton makeNavigationButton(String imageName, String actionCommand, String toolTipText, String altText) {
		// Look for the image.
		String imgLocation = "/images/" + imageName;
		URL imageURL = getClass().getResource(imgLocation);

		// Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else { // no image found
			button.setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}

		return button;
	}

	private void initLayout(SpringLayout layout) {
		JPanel panel = this;

		layout.putConstraint(SpringLayout.NORTH, toolbar, 0, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, toolbar, 10, SpringLayout.WEST, panel);

		layout.putConstraint(SpringLayout.NORTH, scroll, 0, SpringLayout.SOUTH, toolbar);
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
			Resource resource = list.getSelectedValue();
			if (resource != null) {
				showResource(resource);
			}
		}
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
		final Resource resource = new Resource(id, url, method);
		model.add(resource);
	}

	@Override
	public void startRequest(final UUID id, final URL url, final Header[] requestHeaders, final byte[] data) {
		model.startRequest(id, url, requestHeaders, data);
	}

	@Override
	public void requestComplete(final UUID id, final int status, final String reason, final long duration,
			final Header[] responseHeaders, final byte[] data) {
		model.requestComplete(id, status, reason, duration, responseHeaders, data);
	}

	@Override
	public void error(UUID id, String message, Exception e) {
		model.error(id, message, e);
	}

}
