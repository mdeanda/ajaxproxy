package com.thedeanda.ajaxproxy.ui.update;

import com.thedeanda.ajaxproxy.http.SimpleHttpClient;
import com.thedeanda.ajaxproxy.ui.ConfigService;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class UpdateCheckWorker extends SwingWorker<Boolean, Void> {
	private static final Logger log = LoggerFactory.getLogger(UpdateCheckWorker.class);
	public static String RELEASE_CHECK_URL = "https://github.com/mdeanda/ajaxproxy/releases.atom";
	public static String RELEASE_URL = "https://github.com/mdeanda/ajaxproxy/releases";

	private static final long MIN_DELAY = 5000;
	private static final long RANDOM_DELAY = 25000;

	private String message = null;
	private Set<ReleaseEntry> entries = new TreeSet<>();

	@Override
	protected Boolean doInBackground() throws Exception {
		boolean retVal = false;
		String version = ConfigService.get().getVersionString();
		if (StringUtils.isBlank(version)) {
			return false;
		}
		try {
			long delay = (System.currentTimeMillis() % RANDOM_DELAY) + MIN_DELAY;
			Thread.sleep(delay);
			loadAtomFeed();
			ReleaseEntry entry = getEntry();
			retVal = verifyUpdateAvailable(entry, version);
			log.warn("latest: {}", entry);
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}

		return retVal;
	}

	private boolean verifyUpdateAvailable(ReleaseEntry entry, String version) {
		boolean retVal = false;
		if (entry != null) {
			ReleaseVersion currentVersion = new ReleaseVersion(version);

			if (currentVersion.compareTo(entry.version) < 0) {
				message = String.format("Version %s is available for download.", entry.version);
				retVal = true;
			}
		}
		return retVal;
	}

	private ReleaseEntry getEntry() {
		ReleaseEntry entry = null;
		if (entries != null && !entries.isEmpty())
			entry = entries.iterator().next();

		return entry;
	}

	private void loadAtomFeed() throws DocumentException {
		SimpleHttpClient client = new SimpleHttpClient();
		String data = client.getString(RELEASE_CHECK_URL);

		if (data == null)
			return;

		Document doc = DocumentHelper.parseText(data);
		@SuppressWarnings("unchecked")
		List<Element> elements = doc.getRootElement().elements();

		log.debug("nodes: {}", elements);
		for (Element element : elements) {

			if ("ENTRY".equals(element.getName().toUpperCase())
					&& DefaultElement.class.isAssignableFrom(element.getClass())) {
				log.trace("element: {}", element);
				ReleaseEntry entry = parseEntry((DefaultElement) element);
				if (entry != null) {
					entries.add(entry);
				}
			}
		}
	}

	private ReleaseEntry parseEntry(DefaultElement element) {
		ReleaseEntry entry = new ReleaseEntry();
		Element el = null;
		el = element.element("id");
		if (el == null)
			return null;
		entry.id = StringUtils.trimToEmpty(el.getText());

		el = element.element("link");
		if (el == null)
			return null;
		entry.link = StringUtils.trimToEmpty(el.attributeValue("href"));

		int trimStart = StringUtils.lastIndexOf(entry.link, '/') + 1;
		String version = entry.link.substring(trimStart);
		if (StringUtils.isBlank(version)) {
			return null;
		}
		entry.version = new ReleaseVersion(version);
		return entry;
	}

	public String getMessage() {
		return message;
	}

	private class ReleaseEntry implements Comparable<ReleaseEntry> {
		public String id;
		public String link;
		public ReleaseVersion version;

		@Override
		public int compareTo(ReleaseEntry o) {
			// we flip the compare here to get newest first
			return o.version.compareTo(version);
		}
	}
}
