package at.ameise.moodtracker.app.util;

import android.util.Log;

import at.ameise.moodtracker.app.Setting;

/**
 * Wraps the methods of the Android logging framework {@link android.util.Log}. Before
 * logging, {@link android.util.Log#isLoggable(String, int)} is tested to make sure we leak
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
	 * @see android.util.Log#wtf(String, String)
	 * @param tag
	 * @param message
	 */
	public static void wtf(String tag, String message) {
		if (Log.isLoggable(tag, Log.ASSERT) || Setting.LOG_MODE_DEBUG)
			Log.wtf(tag, message);
	}
	
	/**
	 * @see android.util.Log#wtf(String, String, Throwable)
	 * @param tag
	 * @param message
	 */
	public static void wtf(String tag, String message, Throwable throwable) {
		if (Log.isLoggable(tag, Log.ASSERT) || Setting.LOG_MODE_DEBUG)
			Log.wtf(tag, message, throwable);
	}

	/**
	 * @see android.util.Log#e(String, String)
	 * @param tag
	 * @param message
	 */
	public static void error(String tag, String message) {
		if (Log.isLoggable(tag, Log.ERROR) || Setting.LOG_MODE_DEBUG)
			Log.e(tag, message);
	}

	/**
	 * @see android.util.Log#e(String, String, Throwable)
	 * @param tag
	 * @param message
	 * @param throwable
	 */
	public static void error(String tag, String message, Throwable throwable) {
		if (Log.isLoggable(tag, Log.ERROR) || Setting.LOG_MODE_DEBUG)
			Log.e(tag, message, throwable);
	}
	

	/**
	 * @see android.util.Log#w(String, String)
	 * @param tag
	 * @param message
	 */
	public static void warn(String tag, String message) {
		if (Log.isLoggable(tag, Log.WARN) || Setting.LOG_MODE_DEBUG)
			Log.w(tag, message);
	}
	
	/**
	 * @see android.util.Log#w(String, String, Throwable)
	 * @param tag
	 * @param message
	 * @param throwable
	 */
	public static void warn(String tag, String message, Throwable throwable) {
		if (Log.isLoggable(tag, Log.WARN) || Setting.LOG_MODE_DEBUG)
			Log.w(tag, message, throwable);
	}

	/**
	 * @see android.util.Log#i(String, String)
	 * @param tag
	 * @param message
	 */
	public static void info(String tag, String message) {
		if (Log.isLoggable(tag, Log.INFO) || Setting.LOG_MODE_DEBUG)
			Log.i(tag, message);
	}

	/**
	 * @see android.util.Log#d(String, String)
	 * @param tag
	 * @param message
	 */
	public static void debug(String tag, String message) {
		if (Log.isLoggable(tag, Log.DEBUG) || Setting.LOG_MODE_DEBUG)
			Log.d(tag, message);
	}

	/**
	 * @see android.util.Log#v(String, String)
	 * @param tag
	 * @param message
	 */
	public static void verbose(String tag, String message) {
		if (Log.isLoggable(tag, Log.VERBOSE) || Setting.LOG_MODE_DEBUG)
			Log.v(tag, message);
	}
}
