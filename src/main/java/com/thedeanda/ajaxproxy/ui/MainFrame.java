package com.thedeanda.ajaxproxy.ui;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.ajaxproxy.ProxyListener;
import com.thedeanda.ajaxproxy.ui.json.JsonViewerFrame;
import com.thedeanda.ajaxproxy.ui.rest.RestClientFrame;
import com.thedeanda.javajson.JsonArray;
import com.thedeanda.javajson.JsonException;
import com.thedeanda.javajson.JsonObject;
import com.thedeanda.javajson.JsonValue;

public class MainFrame extends JFrame implements ProxyListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(MainFrame.class);
	private static final boolean USE_TRAY = true;
	private MainPanel panel;
	final JFileChooser fc = new JFileChooser();
	private TrayIcon trayIcon;
	private List<File> recentFiles;
	private JMenu recentMenu;
	private Image image;
	private File file = null;
	private boolean ignoreSaveSettings = false;
	private MenuItem stopServerMenuItem;
	private JMenuItem stopServerMenuItem2;
	private MenuItem startServerMenuItem;
	private JMenuItem startServerMenuItem2;
	private JMenuItem saveAsMenuItem;
	private JMenuItem saveMenuItem;
	private MenuItem showFrameMenuItem;
	private MenuItem newRestClientMenuItem;
	private MenuItem newJsonViewerMenuItem;

	public MainFrame() {
		this.panel = new MainPanel();
		updateTitle();
		recentFiles = new ArrayList<File>();
		this.initWindow();
		this.initMenuBar();
		this.initTray();
		if (!USE_TRAY) {
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		}

		JsonObject settings = loadSettings();
		if (settings != null) {
			try {
				panel.setSettings(settings.getJsonObject("settings"));
				ignoreSaveSettings = true;
				this.loadRecent(settings);
			} finally {
				ignoreSaveSettings = false;
			}
		}
		getContentPane().add(panel);
		pack();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					panel.stop();
					saveRecent();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		});
		panel.addProxyListener(this);
	}

	private void updateTitle() {
		if (file == null)
			setTitle("Ajax Proxy");
		else
			setTitle("Ajax Proxy - " + file.getAbsolutePath());
	}

	public void loadFile(final File file) {
		handleStop();
		this.file = file;
		recentFiles.add(file);
		updateTitle();
		panel.setConfigFile(file);
		saveRecent();
	}

	private void initWindow() {
		URL imgUrl = ClassLoader.getSystemClassLoader().getResource("icon.png");
		this.image = Toolkit.getDefaultToolkit().getImage(imgUrl);
		this.setIconImage(image);
		setPreferredSize(new Dimension(980, 700));
	}

	private void initTray() {
		if (SystemTray.isSupported()) {

			MouseListener mouseListener = new MouseListener() {

				public void mouseClicked(MouseEvent e) {
				}

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {
				}

				public void mousePressed(MouseEvent e) {
				}

				public void mouseReleased(MouseEvent e) {
				}
			};

			ActionListener menuItemListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == startServerMenuItem) {
						startProxy();
					} else if (e.getSource() == stopServerMenuItem) {
						handleStop();
					} else if (e.getSource() == showFrameMenuItem) {
						handleShowWindow();
					} else if (e.getSource() == newRestClientMenuItem) {
						handleRest();
					} else if (e.getSource() == newJsonViewerMenuItem) {
						handleJson();
					} else
						handleExit();
				}

			};

			PopupMenu popup = new PopupMenu();
			this.startServerMenuItem = new MenuItem("Start Server");
			startServerMenuItem.addActionListener(menuItemListener);
			popup.add(startServerMenuItem);

			this.stopServerMenuItem = new MenuItem("Stop Server");
			stopServerMenuItem.addActionListener(menuItemListener);
			popup.add(stopServerMenuItem);

			this.showFrameMenuItem = new MenuItem("Show Window");
			showFrameMenuItem.addActionListener(menuItemListener);
			popup.add(showFrameMenuItem);

			this.newRestClientMenuItem = new MenuItem("New Rest Client");
			newRestClientMenuItem.addActionListener(menuItemListener);
			popup.add(newRestClientMenuItem);

			this.newJsonViewerMenuItem = new MenuItem("New Json Viewer");
			newJsonViewerMenuItem.addActionListener(menuItemListener);
			popup.add(newJsonViewerMenuItem);

			popup.addSeparator();

			MenuItem defaultItem = new MenuItem("Exit");
			defaultItem.addActionListener(menuItemListener);
			popup.add(defaultItem);

			trayIcon = new TrayIcon(image, "AjaxProxy", popup);

			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MainFrame self = MainFrame.this;
					self.setVisible(!MainFrame.this.isVisible());
					if (self.isVisible()) {
						self.requestFocus();
					}
				}
			};

			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(actionListener);
			trayIcon.addMouseListener(mouseListener);

			if (USE_TRAY) {
				try {
					SystemTray tray = SystemTray.getSystemTray();
					tray.add(trayIcon);
				} catch (AWTException e) {
					log.error("TrayIcon could not be added.", e);
				}
			}
		}
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
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				ActionEvent.CTRL_MASK));
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleNew();
			}
		});
		menu.add(mi);

		mi = new JMenuItem("Open");
		mi.setMnemonic(KeyEvent.VK_O);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.CTRL_MASK));
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
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK));
		saveMenuItem = mi;
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleSave();
			}
		});
		menu.add(mi);

		mi = new JMenuItem("Save As");
		saveAsMenuItem = mi;
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleSaveAs();
			}
		});
		menu.add(mi);

		menu.addSeparator();

		mi = new JMenuItem("Rest Client");
		mi.setMnemonic(KeyEvent.VK_R);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				ActionEvent.CTRL_MASK));
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleRest();
			}
		});
		menu.add(mi);

		mi = new JMenuItem("Json Viewer");
		mi.setMnemonic(KeyEvent.VK_J);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J,
				ActionEvent.CTRL_MASK));
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handleJson();
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
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.CTRL_MASK));
		mi.addActionListener(menuItemListener);
		menu.add(mi);

		this.stopServerMenuItem2 = mi = new JMenuItem("Stop Server");
		mi.setMnemonic(KeyEvent.VK_O);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
				ActionEvent.CTRL_MASK));
		mi.addActionListener(menuItemListener);
		menu.add(mi);

	}

	private void handleRest() {
		RestClientFrame frame = new RestClientFrame();
		frame.setVisible(true);
	}

	private void handleJson() {
		JsonViewerFrame frame = new JsonViewerFrame();
		frame.setVisible(true);
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
			JOptionPane.showMessageDialog(this.getComponent(0),
					ex.getMessage(), "Failed to save",
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
		try {
			panel.stop();
		} finally {
			System.exit(0);
		}
	}

	private File getRecentFile() {
		String recentFilePath = System.getProperty("user.home")
				+ File.separator + ".ajaxproxy";
		File f = new File(recentFilePath);
		return f;
	}

	private JsonObject loadSettings() {
		JsonObject ret = null;
		File f = getRecentFile();
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

	private void loadRecent(JsonObject settings) {
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
			try {
				JMenuItem mi = new JMenuItem(rf.getCanonicalPath());
				recentMenu.add(mi);
				mi.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						loadFile(rf);
					}
				});
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private void saveRecent() {
		if (ignoreSaveSettings)
			return;

		File f = getRecentFile();
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
		startServerMenuItem.setEnabled(false);
		stopServerMenuItem.setEnabled(true);

		startServerMenuItem2.setEnabled(false);
		stopServerMenuItem2.setEnabled(true);
	}

	@Override
	public void stopped() {
		startServerMenuItem.setEnabled(true);
		stopServerMenuItem.setEnabled(false);

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

	private void handleShowWindow() {
		this.setVisible(true);
	}

}
