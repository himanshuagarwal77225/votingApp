package com.mxo2.votingapp.utils;

/**
 * Logs are used within applications to facilitate development by displaying
 * messages thereby allowing user to track the flow. This app also uses Logs to
 * display messages in almost all files. But using logs, and that too in such a
 * large number to display large chunks of data, disturbs the application speed.
 * So to prevent the 10-15% memory consumption by logging, it is advisable to
 * turn logs off when releasing the application. To implement this, this custom
 * Log class is used. It contains methods to display logs using the default
 * android.util.Log class. The static boolean variable LOG decides whether the
 * Logs should be displayed or not. When preparing Live build or when providing
 * .apk for testing, the developer must make the variable LOG false.
 **/
public class Log {

	// TODO:
	/**
	 * during development mode --> LOG = true Relesing application or giving
	 * build for testing --> LOG = false
	 **/
	static final boolean LOG = false;

	public static void i(String tag, String string) {
		if (LOG)
			android.util.Log.i(tag, string);
	}

	public static void e(String tag, String string) {
		if (LOG)
			android.util.Log.e(tag, string);
	}

	public static void d(String tag, String string) {
		if (LOG)
			android.util.Log.d(tag, string);
	}

	public static void v(String tag, String string) {
		if (LOG)
			android.util.Log.v(tag, string);
	}

	public static void w(String tag, String string) {
		if (LOG)
			android.util.Log.w(tag, string);
	}

}