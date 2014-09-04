package com.thedeanda.ajaxproxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.javajson.JsonArray;
import net.sourceforge.javajson.JsonObject;
import net.sourceforge.javajson.JsonValue;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.proxy.AsyncProxyServlet;

public class AjaxProxy implements Runnable {
	private static final Logger log = Logger.getLogger(AjaxProxy.class);
	private int port = 8080;
	private String resourceBase = "";
	private JsonObject config;
	private Server jettyServer;
	private File workingDir;
	private List<ProxyListener> listeners = new ArrayList<ProxyListener>();
	private APFilter apfilter = new APFilter();
	private boolean mergeMode = false;
	private List<MergeServlet> mergeServlets = new ArrayList<MergeServlet>();

	private enum ProxyEvent {
		START, STOP, FAIL
	};

	public static final String PORT = "port";
	public static final String RESOURCE_BASE = "resourceBase";
	public static final String PROXY_ARRAY = "proxy";
	public static final String DOMAIN = "domain";
	public static final String PATH = "path";
	public static final String MERGE_ARRAY = "merge";
	public static final String FILE_PATH = "filePath";
	private static final String MODE = "mode";
	private static final String MINIFY = "minify";

	public static void main(String[] args) throws Exception {
		String config = "config.js";
		if (args.length > 0) {
			config = args[0];
		}
		AjaxProxy main = new AjaxProxy(config);
		new Thread(main).start();
	}

	public AjaxProxy(JsonObject config, File workingDir) throws Exception {
		this.config = config;
		this.workingDir = workingDir;
	}

	public AjaxProxy(String configFile) throws Exception {
		log.info("using config file: " + configFile);
		File cf = new File(configFile);
		if (!cf.exists())
			throw new FileNotFoundException("config file not found");
		File configDir = cf.getParentFile();
		if (configDir == null)
			configDir = new File(".");
		FileInputStream fis = new FileInputStream(cf);
		JsonObject config = JsonObject.parse(fis);
		fis.close();
		this.config = config;
		this.workingDir = configDir;
	}

	public void addProxyListener(ProxyListener pl) {
		synchronized (listeners) {
			listeners.add(pl);
		}
	}

	private void doVar(String variable, String value, JsonValue target) {
		if (target.isJsonArray()) {
			for (JsonValue next : target.getJsonArray()) {
				doVar(variable, value, next);
			}
		} else if (target.isJsonObject()) {
			JsonObject json = target.getJsonObject();
			for (String key : json) {
				doVar(variable, value, json.get(key));
			}
		} else if (target.isString()) {
			String s = target.getString();
			String v = "${" + variable + "}";
			if (s != null && s.indexOf(v) >= 0) {
				s = s.replaceAll(Pattern.quote(v), value);
				target.setString(s);
			}
		}
	}

	private void doVars() {
		JsonObject vars = config.getJsonObject("variables");
		if (vars != null) {
			JsonArray proxy = config.getJsonArray("proxy");
			if (proxy != null)
				doVars(vars, proxy);

			JsonArray merge = config.getJsonArray("merge");
			if (merge != null)
				doVars(vars, merge);
		}
	}

	private void doVars(JsonObject vars, JsonArray target) {
		for (String val : vars) {
			if (vars.isString(val) || vars.isInt(val)) {
				for (JsonValue targetValue : target) {
					doVar(val, vars.getString(val), targetValue);
				}
			}
		}
	}

	private void fireEvent(ProxyEvent evt) {
		ArrayList<ProxyListener> lst = new ArrayList<ProxyListener>();
		synchronized (listeners) {
			lst.addAll(listeners);
		}
		for (ProxyListener pl : lst) {
			switch (evt) {
			case START:
				pl.started();
				break;
			case STOP:
				pl.stopped();
				break;
			case FAIL:
				pl.failed();
				break;
			}
		}
	}

	private void init(JsonObject config, File workingDir) throws Exception {
		this.config = config;
		doVars();

		if (config.isInt(PORT)) {
			port = config.getInt(PORT);
		}
		log.debug("using port: " + port);

		if (config.isString(RESOURCE_BASE)) {
			String rb = config.getString(RESOURCE_BASE);
			resourceBase = workingDir.getPath() + File.separator + rb;
			File tmp = new File(resourceBase);
			if (!tmp.exists()) {
				tmp = new File(rb);
			}
			if (!tmp.exists()) {
				throw new FileNotFoundException("Resource base not found: "
						+ rb);
			}
			resourceBase = tmp.getCanonicalPath();
		} else {
			throw new Exception("resourceBase not defined in config file");
		}
		log.info("using resource base: " + resourceBase);
	}

	public void run() {
		log.info("starting jetty server");
		try {
			fireEvent(ProxyEvent.START);
			init(config, workingDir);

			jettyServer = new Server(port);

			ContextHandlerCollection contexts = new ContextHandlerCollection();
			jettyServer.setHandler(contexts);

			Context root = new Context(contexts, "/", Context.SESSIONS);
			FilterHolder filterHolder = new FilterHolder(apfilter);
			root.addFilter(filterHolder, "/*", 1);

			ServletHolder servlet;
			DefaultServlet defaultServlet = new DefaultServlet();
			servlet = new ServletHolder(defaultServlet);
			servlet.setInitParameter("dirAllowed", "true");
			servlet.setInitParameter("resourceBase", resourceBase);
			servlet.setInitParameter("maxCacheSize", "0");
			servlet.setName("default servlet");
			servlet.setForcedPath("/");
			root.addServlet(servlet, "/");

			if (!mergeMode && config.isJsonArray(PROXY_ARRAY)) {
				JsonArray pa = config.getJsonArray(PROXY_ARRAY);
				for (JsonValue val : pa) {
					int port = 80;
					String domain = null;
					String path = null;
					String prefix = "";
					JsonObject obj = val.getJsonObject();
					if (obj.isString(DOMAIN))
						domain = obj.getString(DOMAIN);
					if (obj.isString(PATH))
						path = obj.getString(PATH);
					if (obj.isInt(PORT))
						port = obj.getInt(PORT);
					else if (obj.isString(PORT))
						port = Integer.parseInt(obj.getString(PORT));
					if (obj.isString("prefix"))
						prefix = obj.getString("prefix");

					if (domain != null && path != null && port > 0) {
						log.debug("adding proxy servlet: " + domain + ":"
								+ port + " " + path);
						root.addServlet(new ServletHolder(
								new AsyncProxyServlet.Transparent(prefix,
										domain, port)), path);
					}
				}
			}
			if (config.isJsonArray(MERGE_ARRAY)) {
				JsonArray a = config.getJsonArray(MERGE_ARRAY);
				for (JsonValue val : a) {
					if (val.isJsonObject()) {
						JsonObject obj = val.getJsonObject();
						String path = obj.getString(PATH);
						String filePath = obj.getString(FILE_PATH);
						File fPath = new File(resourceBase + File.separator
								+ filePath);
						if (!fPath.exists()) {
							log.warn("file not found: "
									+ fPath.getCanonicalPath());
							fPath = new File(filePath);
						}
						if (!fPath.exists()) {
							throw new FileNotFoundException(
									fPath.getAbsolutePath());
						}
						filePath = fPath.getCanonicalPath();
						log.debug(obj);
						boolean minify = obj.getBoolean(MINIFY);
						MergeMode mode = obj.hasKey(MODE) ? MergeMode
								.valueOf(obj.getString(MODE)) : MergeMode.PLAIN;
						log.debug("adding merge servlet: " + path
								+ " to merge " + filePath);
						MergeServlet ms = new MergeServlet(filePath, mode,
								minify, path);
						mergeServlets.add(ms);
						root.addServlet(new ServletHolder(ms), path);
					}
				}
			}

			if (!mergeMode) {
				try {
					jettyServer.start();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					jettyServer.stop();
					throw e;
				}

				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						try {
							log.info("Shutting down jetty");
							jettyServer.stop();
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					}
				});
			}
		} catch (Exception e) {
			fireEvent(ProxyEvent.FAIL);
			log.error(e.getMessage(), e);
		}
	}

	public void stop() {
		try {
			if (jettyServer != null) {
				jettyServer.stop();
				fireEvent(ProxyEvent.STOP);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public APFilter getApfilter() {
		return apfilter;
	}

	public JsonObject getConfig() {
		return config;
	}

	public List<MergeServlet> getMergeServlets() {
		return mergeServlets;
	}

	public void setMergeMode(boolean mergeMode) {
		this.mergeMode = mergeMode;
	}

	public void addTracker(AccessTracker tracker) {
		apfilter.add(tracker);
	}
}