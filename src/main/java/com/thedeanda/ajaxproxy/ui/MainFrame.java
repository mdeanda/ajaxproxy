package com.thedeanda.ajaxproxy.ui;

import com.thedeanda.ajaxproxy.ProxyListener;
import com.thedeanda.ajaxproxy.ui.help.HelpAbout;
import com.thedeanda.ajaxproxy.ui.help.HelpUpdates;
import com.thedeanda.ajaxproxy.ui.windows.*;
import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonException;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.*;

public class MainFrame extends JFrame implements ProxyListener, WindowListListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(MainFrame.class);
	private MainPanel panel;
	final JFileChooser fc = new JFileChooser();
	private List<File> recentFiles;
	private JMenu recentMenu;
	private Image image;
	private File file = null;
	private boolean ignoreSaveSettings = false;
	private JMenuItem stopServerMenuItem2;
	private JMenuItem startServerMenuItem2;
	private String windowId;

	HelpAbout helpAbout = null;


	public MainFrame() {
		this.panel = new MainPanel();
		updateTitle();
		recentFiles = new ArrayList<File>();
		this.initWindow();
		this.initMenuBar();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		initSettings();
		getContentPane().add(panel);
		pack();

		panel.addProxyListener(this);

		this.windowId = Windows.get().addListener(this).add(this);
		this.addWindowListener(new WindowListListenerCleanup(this));
		new WindowMenuHelper(windowId, getJMenuBar());
		addHelpMenu(getJMenuBar());
	}

	private void initSettings() {
		ignoreSaveSettings = true;
		try {
			JsonObject settings = loadSettings();
			if (settings != null) {
				panel.setSettings(settings.getJsonObject("settings"));
				this.loadRecentFilesMenu(settings);
			}
		} finally {
			ignoreSaveSettings = false;
		}
	}

	private void updateTitle() {
		String title = "Ajax Proxy";
		if (file != null) {
			title += " - " + file.getAbsolutePath();
		}
		title = ConfigService.get().generateWindowTitle(title);

		setTitle(title);
		Windows.get().notifyOfChange();
	}

	public void loadFile(final File file) {
		handleStop();
		this.file = file;
		recentFiles.add(file);
		updateTitle();
		panel.setConfigFile(file);
		saveSettings();
	}

	private void initWindow() {
		URL imgUrl = ClassLoader.getSystemClassLoader().getResource("icon.png");
		this.image = Toolkit.getDefaultToolkit().getImage(imgUrl);
		this.setIconImage(image);
		setPreferredSize(new Dimension(1150, 700));
		setMinimumSize(new Dimension(700, 550));
	}

	private void initMenuBar() {
		JMenuBar mb = new JMenuBar();
		this.setJMenuBar(mb);
		JMenu menu;
		JMenuItem mi;

		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		mb.add(menu);

		mi = new JMenuItem("New");
		mi.setMnemonic(KeyEvent.VK_N);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleNew();
			}
		});
		menu.add(mi);

		mi = new JMenuItem("Open");
		mi.setMnemonic(KeyEvent.VK_O);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleOpen();
			}
		});
		menu.add(mi);

		mi = new JMenuItem("Reload");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (file != null) {
					loadFile(file);
				}
			}
		});
		menu.add(mi);

		this.recentMenu = new JMenu("Recent Files");
		menu.add(this.recentMenu);

		menu.addSeparator();

		mi = new JMenuItem("Save");
		mi.setMnemonic(KeyEvent.VK_S);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleSave();
			}
		});
		menu.add(mi);

		mi = new JMenuItem("Save As");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleSaveAs();
			}
		});
		menu.add(mi);

		menu.addSeparator();

		mi = new JMenuItem("Exit");
		mi.setMnemonic(KeyEvent.VK_X);
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleExit();
			}
		});
		menu.add(mi);

		ActionListener menuItemListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == startServerMenuItem2)
					startProxy();
				else if (e.getSource() == stopServerMenuItem2)
					handleStop();
			}
		};

		menu = new JMenu("Server");
		menu.setMnemonic(KeyEvent.VK_S);
		mb.add(menu);
		this.startServerMenuItem2 = mi = new JMenuItem("Start Server");
		mi.setMnemonic(KeyEvent.VK_A);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK));
		mi.addActionListener(menuItemListener);
		menu.add(mi);

		this.stopServerMenuItem2 = mi = new JMenuItem("Stop Server");
		mi.setMnemonic(KeyEvent.VK_O);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.CTRL_MASK));
		mi.addActionListener(menuItemListener);
		menu.add(mi);

	}
	private void addHelpMenu(JMenuBar mb) {
		JMenuItem mi;
		JMenu menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_F);
		mb.add(menu);

		mi = new JMenuItem("About");
		mi.addActionListener(al -> {
			if (helpAbout == null) {
				helpAbout = new HelpAbout(MainFrame.this);
			}
			helpAbout.pack();
			helpAbout.setLocationRelativeTo(MainFrame.this);
			helpAbout.setVisible(true);
		});
		//TODO: bring back "help about"
		//menu.add(mi);

		mi = new JMenuItem("Check for Updates...");
		mi.setMnemonic(KeyEvent.VK_C);
		mi.addActionListener(al -> {
			showHelpUpdates();
		});
		menu.add(mi);
	}

	private void showHelpUpdates() {
		HelpUpdates helpUpdates = new HelpUpdates(MainFrame.this);
		helpUpdates.pack();
		helpUpdates.setLocationRelativeTo(MainFrame.this);
		helpUpdates.setVisible(true);
	}

	private void handleNew() {
		// TODO: prompt for unsaved changes
		handleStop();
		panel.clearAll();
		file = null;
		updateTitle();
	}

	private void handleOpen() {
		if (file != null)
			fc.setCurrentDirectory(file);
		int retVal = fc.showOpenDialog(MainFrame.this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			loadFile(f);
		}
	}

	private void handleSave() {
		if (file == null) {
			handleSaveAs();
			return;
		} else {
			save(file);
		}
	}

	private void handleSaveAs() {
		if (file != null)
			fc.setCurrentDirectory(file);
		int retVal = fc.showSaveDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File dstFile = fc.getSelectedFile();
			save(dstFile);
			this.file = dstFile;
			updateTitle();
		}
	}

	private void save(File file) {
		log.debug("saving to {}", file);
		Writer writer = null;
		Exception ex = null;
		try {
			JsonObject config = panel.getConfig();
			writer = new FileWriter(file);
			writer.write(config.toString(2));
		} catch (IOException io) {
			ex = io;
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				ex = e;
			}
		}

		if (ex != null) {
			JOptionPane.showMessageDialog(this.getComponent(0), ex.getMessage(), "Failed to save",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void startProxy() {
		panel.start();
	}

	private void handleStop() {
		panel.stop();
	}

	private void handleExit() {
		panel.stop();
		saveSettings();
		dispose();
	}

	private JsonObject loadSettings() {
		JsonObject ret = null;
		File f = ConfigService.get().getConfigFile();
		if (f.exists()) {
			InputStream is = null;
			try {
				is = new FileInputStream(f);
				JsonObject json = JsonObject.parse(is);
				if (json.hasKey("lastFile")) {
					File f2 = new File(json.getString("lastFile"));
					if (f2.exists()) {
						loadFile(f2);
					}
				}
				ret = json;
			} catch (FileNotFoundException e) {
				log.error(e.getMessage(), e);
			} catch (JsonException e) {
				log.error(e.getMessage(), e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}

		return ret;
	}

	private void loadRecentFilesMenu(JsonObject settings) {
		recentMenu.removeAll();
		recentFiles.clear();
		if (settings != null) {
			JsonArray array = settings.getJsonArray("recentFiles");
			if (array != null) {
				for (JsonValue v : array) {
					File rf = new File(v.getString());
					if (rf.exists())
						recentFiles.add(rf);
				}
			}
		}

		for (final File rf : recentFiles) {
			if (!rf.exists())
				continue;
			String path = null;

			try {
				path = rf.getCanonicalPath();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				continue;
			}

			JMenuItem mi = new JMenuItem(path);
			recentMenu.add(mi);
			mi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					loadFile(rf);
				}
			});
		}
	}

	private void saveSettings() {
		if (ignoreSaveSettings)
			return;
		log.info("saving recents");

		File f = ConfigService.get().getConfigFile();
		JsonObject json = new JsonObject();
		Set<String> repeats = new HashSet<String>();
		for (File recent : recentFiles) {
			if (recent.exists()) {
				String fn = recent.getAbsolutePath();
				if (!repeats.contains(fn)) {
					json.accumulate("recentFiles", recent.getAbsolutePath());
					repeats.add(fn);
				}
			}
		}
		if (this.file != null) {
			json.put("lastFile", file.getAbsolutePath());
		}
		json.put("settings", this.panel.getSettings());

		OutputStream os = null;
		try {
			os = new FileOutputStream(f);
			PrintWriter pw = new PrintWriter(os);
			pw.write(json.toString(2));
			pw.close();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		log.debug(json.toString(2));
	}

	@Override
	public void started() {
		startServerMenuItem2.setEnabled(false);
		stopServerMenuItem2.setEnabled(true);
	}

	@Override
	public void stopped() {
		startServerMenuItem2.setEnabled(true);
		stopServerMenuItem2.setEnabled(false);
	}

	@Override
	public void failed() {
		stopped();
	}

	public void addVariables(Map<String, String> vars) {
		panel.addVariables(vars);
	}

	@Override
	public void windowsChanged(Collection<WindowContainer> windows) {
		if (!Windows.get().contains(this)) {
			try {
				log.info("window closing");
				panel.stop();
				saveSettings();
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}
		}
	}

}
