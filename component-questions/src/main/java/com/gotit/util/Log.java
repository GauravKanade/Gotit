package com.gotit.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

	private static final Logger logger = Logger.getLogger(Log.class.getName());

	public static void i(String log, Object... param1) {
		logger.log(Level.INFO, log, param1);
	}

	public static void d(String log, Object... objects) {
		logger.log(Level.FINE, log, objects);
	}

	public static void e(String log, Throwable throwable) {
		logger.log(Level.SEVERE, log, throwable);
	}
}
