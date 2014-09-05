/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.slf4j.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * An implementation of {@link ILoggerFactory} which always returns
 * {@link ListenerSupportedSimpleLogger} instances.
 * 
 * Originally copied from SimpleLogger implementation
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class ListenerSupportedSimpleLoggerFactory implements ILoggerFactory {

	ConcurrentMap<String, Logger> loggerMap;

	private static Set<LogListener> listeners = new HashSet<LogListener>();

	public static void addListener(LogListener listener) {
		listeners.add(listener);
	}

	public ListenerSupportedSimpleLoggerFactory() {
		loggerMap = new ConcurrentHashMap<String, Logger>();
	}

	/**
	 * Return an appropriate {@link ListenerSupportedSimpleLogger} instance by
	 * name.
	 */
	public Logger getLogger(String name) {
		Logger simpleLogger = loggerMap.get(name);
		if (simpleLogger != null) {
			return simpleLogger;
		} else {
			Logger newInstance = new ListenerSupportedSimpleLogger(this, name);
			Logger oldInstance = loggerMap.putIfAbsent(name, newInstance);
			return oldInstance == null ? newInstance : oldInstance;
		}
	}

	/**
	 * Clear the internal logger cache.
	 *
	 * This method is intended to be called by classes (in the same package) for
	 * testing purposes. This method is internal. It can be modified, renamed or
	 * removed at any time without notice.
	 *
	 * You are strongly discouraged from calling this method in production code.
	 */
	void reset() {
		loggerMap.clear();
	}

	public void write(String message) {
		// TODO: make threadsafe with add
		try {
			for (LogListener log : listeners) {
				log.write(message);
			}
		} catch (Exception e) {
			// not sure where to log loggering errors. :)
		}
	}
}
