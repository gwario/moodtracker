package at.ameise.moodtracker.wear.util;

import android.util.Log;

import at.ameise.moodtracker.wear.Settings;

/**
 * Wraps the methods of the Android logging framework {@link Log}. Before
 * logging, {@link Log#isLoggable(String, int)} is tested to make sure we leak
 * no log messages in production.
 *
 * @author Mario Gastegger <mgastegger AT buzzmark DOT com>
 */
public final class Logger {

	/**
	 * .ctor
	 */
	private Logger() {
	}

	/**
	 * @see Log#wtf(String, String)
	 * @param tag
	 * @param message
	 */
	public static void wtf(String tag, String message) {
		if (Log.isLoggable(tag, Log.ASSERT) || Settings.LOG_MODE_DEBUG)
			Log.wtf(tag, message);
	}

	/**
	 * @see Log#wtf(String, String, Throwable)
	 * @param tag
	 * @param message
	 */
	public static void wtf(String tag, String message, Throwable throwable) {
		if (Log.isLoggable(tag, Log.ASSERT) || Settings.LOG_MODE_DEBUG)
			Log.wtf(tag, message, throwable);
	}

	/**
	 * @see Log#e(String, String)
	 * @param tag
	 * @param message
	 */
	public static void error(String tag, String message) {
		if (Log.isLoggable(tag, Log.ERROR) || Settings.LOG_MODE_DEBUG)
			Log.e(tag, message);
	}

	/**
	 * @see Log#e(String, String, Throwable)
	 * @param tag
	 * @param message
	 * @param throwable
	 */
	public static void error(String tag, String message, Throwable throwable) {
		if (Log.isLoggable(tag, Log.ERROR) || Settings.LOG_MODE_DEBUG)
			Log.e(tag, message, throwable);
	}


	/**
	 * @see Log#w(String, String)
	 * @param tag
	 * @param message
	 */
	public static void warn(String tag, String message) {
		if (Log.isLoggable(tag, Log.WARN) || Settings.LOG_MODE_DEBUG)
			Log.w(tag, message);
	}

	/**
	 * @see Log#w(String, String, Throwable)
	 * @param tag
	 * @param message
	 * @param throwable
	 */
	public static void warn(String tag, String message, Throwable throwable) {
		if (Log.isLoggable(tag, Log.WARN) || Settings.LOG_MODE_DEBUG)
			Log.w(tag, message, throwable);
	}

	/**
	 * @see Log#i(String, String)
	 * @param tag
	 * @param message
	 */
	public static void info(String tag, String message) {
		if (Log.isLoggable(tag, Log.INFO) || Settings.LOG_MODE_DEBUG)
			Log.i(tag, message);
	}

	/**
	 * @see Log#d(String, String)
	 * @param tag
	 * @param message
	 */
	public static void debug(String tag, String message) {
		if (Log.isLoggable(tag, Log.DEBUG) || Settings.LOG_MODE_DEBUG)
			Log.d(tag, message);
	}

	/**
	 * @see Log#v(String, String)
	 * @param tag
	 * @param message
	 */
	public static void verbose(String tag, String message) {
		if (Log.isLoggable(tag, Log.VERBOSE) || Settings.LOG_MODE_DEBUG)
			Log.v(tag, message);
	}
}
