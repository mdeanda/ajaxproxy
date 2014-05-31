package com.thedeanda.ajaxproxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.thedeanda.ajaxproxy.ui.MainFrame;

public class Main {
	private static final Logger log = Logger.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		// create the parser
		CommandLineParser parser = new GnuParser();
		Options options = initOptions();
		try {
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption('h')) {
				printHelp(options);
				return;
			}

			String config = cmd.getOptionValue('c', null);
			String merge = cmd.getOptionValue('m', null);
			boolean run = cmd.hasOption("r");
			boolean rm = cmd.hasOption("rm");
			boolean ignore = cmd.hasOption("i");

			if (config != null) {
				File c = new File(config);
				if (!c.exists()) {
					System.err.println("Config file does not exist");
					System.exit(1);
				}
			}

			if (config != null && merge != null) {
				runMerge(config, merge, rm, ignore);
				return;
			} else if (config != null && run) {
				AjaxProxy ap = new AjaxProxy(config);
				ap.run();
			} else if (merge != null && config == null) {
				printHelp(options);
				return;
			} else {
				showUi();
			}

		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			printHelp(options);
		}
	}

	private static void showUi() {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name",
				"Ajaxproxy");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					// Set System L&F
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (Exception ex) {

				}

				MainFrame f = new MainFrame();
				f.setVisible(true);
			}
		});
	}

	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(" ", options);
	}

	private static void runMerge(String config, String output, boolean rm,
			boolean ignore) throws Exception {
		File dir = new File(output);
		if (rm && dir.exists()) {
			FileUtils.deleteDirectory(dir);
		}
		if (dir.exists() && !ignore) {
			System.err
					.println("output directory exists, please remove and try again");
			System.exit(1);
		}

		if (!dir.mkdirs() && !ignore) {
			System.err.println("Failed to create output directory");
			System.exit(1);
		}

		AjaxProxy proxy = new AjaxProxy(config);
		proxy.setMergeMode(true);
		proxy.run();
		List<MergeServlet> servlets = proxy.getMergeServlets();
		for (MergeServlet s : servlets) {
			String content = s.getContent();
			String path = s.getUrlPath();
			writeFile(output, path, content);
		}
		System.out.println("done");
	}

	private static void writeFile(String base, String file, String contents)
			throws IOException {
		log.info("Writing merged file: " + file);
		File f = new File(base + File.separator + file);
		f.getParentFile().mkdirs();
		OutputStream os = null;
		try {
			os = new FileOutputStream(f);
			IOUtils.copy(new StringReader(contents), os);
		} finally {
			if (os != null) {
				os.close();
			}
		}
	}

	private static Options initOptions() {
		Options options = new Options();

		options.addOption("r", "run", false, "run ajaxproxy in headless mode");
		options.addOption("h", "help", false, "print this message");
		options.addOption("c", "config", true, "the config file");
		options.addOption("m", "merge", true,
				"merge to the given folder and exit, requires config parameter");
		options.addOption("rm", "removeDirectory", false,
				"delete existing output folder before merge");
		options.addOption("i", "ignoreDirectory", false,
				"ignore existing output folder and output into existing folder");
		return options;
	}

}
