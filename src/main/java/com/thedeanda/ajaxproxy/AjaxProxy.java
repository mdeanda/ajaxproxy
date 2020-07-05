package com.thedeanda.ajaxproxy;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AjaxProxy {
	private static final Logger log = LoggerFactory.getLogger(AjaxProxy.class);

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
			boolean runrest = cmd.hasOption("rr");
			boolean run = cmd.hasOption("r");
			boolean runui = cmd.hasOption("ru");
			boolean rm = cmd.hasOption("rm");
			boolean ignore = cmd.hasOption("i");
			Map<String, String> vars = readVariables(cmd.getOptionValues("v"));

			if (config != null) {
				File c = new File(config);
				if (!c.exists()) {
					System.err.println("Config file does not exist");
					System.exit(1);
				}
			}

			if (runrest) {
				//runRest();
			} else if (config != null && merge != null) {
				runMerge(config, merge, rm, ignore);
			} else if (config != null && run) {
				AjaxProxyServer ap = new AjaxProxyServer(config);
				ap.run();
			} else if (merge != null && config == null) {
				printHelp(options);
			} else {
				//showUi(config, runui, vars);
			}

		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			printHelp(options);
		}
	}

	private static Map<String, String> readVariables(String[] vars) {
		Map<String, String> map = new HashMap<>();
		if (vars != null && vars.length > 0) {
			for (String var : vars) {
				if (var.contains(":")) {
					String[] parts = var.split(":", 2);
					map.put(parts[0], parts[1]);
				} else {
					throw new IllegalArgumentException(
							String.format(
									"invalid variable. expects varname:varvalue not {}",
									var));
				}
			}
			log.info("variable map: {}", map);
		}
		return map;
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

		AjaxProxyServer proxy = new AjaxProxyServer(config);
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
		options.addOption("ru", "runui", false, "run ajaxproxy in ui mode");
		options.addOption("rr", "runrest", false, "run rest client only");
		options.addOption("h", "help", false, "print this message");
		options.addOption("c", "config", true, "the config file");
		options.addOption("m", "merge", true,
				"merge to the given folder and exit, requires config parameter");
		options.addOption("rm", "removeDirectory", false,
				"delete existing output folder before merge");
		options.addOption("i", "ignoreDirectory", false,
				"ignore existing output folder and output into existing folder");
		options.addOption("v", true, "add variables in the form name:value");
		return options;
	}

}
