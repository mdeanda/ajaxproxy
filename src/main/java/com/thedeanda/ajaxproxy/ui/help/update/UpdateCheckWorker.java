package com.thedeanda.ajaxproxy.ui.help.update;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.SwingWorker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import com.thedeanda.ajaxproxy.http.SimpleHttpClient;

@Slf4j
public class UpdateCheckWorker extends SwingWorker<ReleaseEntry, Void> {
	public static String RELEASE_CHECK_URL = "https://github.com/mdeanda/ajaxproxy/releases.atom";
	public static String RELEASE_URL = "https://github.com/mdeanda/ajaxproxy/releases";

	private String message = null;
	private Set<ReleaseEntry> entries = new TreeSet<>();

	@Override
	protected ReleaseEntry doInBackground() throws Exception {
		ReleaseEntry retVal = null;
		//String version = ConfigService.get().getVersionString();
		try {
			loadAtomFeed();
			ReleaseEntry entry = getEntry();
			log.warn("latest: {}", entry);
			retVal = entry;
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
		}

		return retVal;
	}

	private boolean verifyUpdateAvailable(ReleaseEntry entry, String version) {
		boolean retVal = false;
		if (entry != null) {
			ReleaseVersion currentVersion = new ReleaseVersion(version);

			if (currentVersion.compareTo(entry.getVersion()) < 0) {
				message = String.format("Version %s is available for download.", entry.getVersion());
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
		entry.setId(StringUtils.trimToEmpty(el.getText()));

		el = element.element("link");
		if (el == null)
			return null;
		entry.setLink(StringUtils.trimToEmpty(el.attributeValue("href")));

		int trimStart = StringUtils.lastIndexOf(entry.getLink(), '/') + 1;
		String version = entry.getLink().substring(trimStart);
		if (StringUtils.isBlank(version)) {
			return null;
		}
		entry.setVersion(new ReleaseVersion(version));
		return entry;
	}

	public String getMessage() {
		return message;
	}

}
