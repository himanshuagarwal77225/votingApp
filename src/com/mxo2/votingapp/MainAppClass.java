package com.mxo2.votingapp;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.mxo2.votingapp.dialogs.NWDialogActivity;
import com.mxo2.votingapp.utils.AppConstants;
import com.mxo2.votingapp.utils.AppUtils;
import com.mxo2.votingapp.utils.Log;
import com.mxo2.votingapp.utils.NWChangeReceiver;
import com.mxo2.votingapp.utils.ParentActivity;
import com.mxo2.votingapp.utils.ParentFragmentActivity;

/**
 * This is the main Application class. It remains active throughout the
 * application from start to finish. This class is handling features which are
 * important for entire application. It registers a broadcast receiver to
 * monitor Internet changes, initializes shared preferences,monitors the app
 * visibility, i.e., when the application goes to background and comes in
 * foreground.
 **/
public class MainAppClass extends Application {

	private Context m_Context = null;
	private static SharedPreferences sharedPreferences = null;
	private AppUtils m_AppUtils = null;
	private NWChangeReceiver m_NWchangereceiver = null;

	public static final String APP_directory = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator
			+ AppConstants.APP_FOLDER;
	// public static String APP_Images_directory = MainAppClass.APP_directory +
	// File.separator + AppConstants.APP_IMAGES_FOLDER;
	// public static String APP_Files_directory = MainAppClass.APP_directory +
	// File.separator + AppConstants.APP_FILES_FOLDER;
	static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

	/**
	 * isAppInBackground --> boolean variable used throughout the application to
	 * indicate whether application is in foreground or background
	 **/
	public static boolean isAppInBackground = false;

	/**
	 * isOfflineModeOn --> boolean variable used throughout the application to
	 * indicate whether application is in Online mode or Offline mode
	 **/
	public static boolean isOfflineModeOn = false;

	/**
	 * SubscriptionReminderHandled --> boolean variable used throughout the
	 * application to indicate whether an alert has already been shown to the
	 * user to remind him of his subscription expiration
	 **/
	public static boolean SubscriptionReminderHandled = false;

	/**
	 * isReceiverRegistered --> boolean variable used throughout the application
	 * to indicate whether the network change receiver has been registered or
	 * not, so as to know whether an alert is to be shown or not for Network
	 * change
	 **/
	public boolean isReceiverRegistered = false;

	private static boolean mInBackground = false;

	public Context getContext() {
		return m_Context;
	}

	public void setContext(Context context) {
		this.m_Context = context;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		m_Context = getApplicationContext();
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(m_Context);
		m_AppUtils = new AppUtils(m_Context);
		m_NWchangereceiver = new NWChangeReceiver();
		if (m_AppUtils.isNetworkConnected()) {
			isOfflineModeOn = false;
		} else {
			isOfflineModeOn = true;
		}
		/** create the application directory **/
		if (m_AppUtils.makeDirectory(APP_directory)) {

		}
	}

	public static SharedPreferences getSharedPref() {
		return sharedPreferences;
	}

	/**
	 * Register Broadcast Receiver to monitor network state - On/Off
	 **/
	public void registerNetworkreceiver() {
		if (!isReceiverRegistered) {
			IntentFilter filter = new IntentFilter(ACTION);
			this.registerReceiver(m_NWchangereceiver, filter);
			isReceiverRegistered = true;
		}
	}

	/**
	 * Unregister the registered network monitoring receiver, so that it does
	 * not show an alert now
	 **/
	public void unregisterNetworkreceiver() {
		if ((m_NWchangereceiver != null) && (isReceiverRegistered)) {
			this.unregisterReceiver(m_NWchangereceiver);
			isReceiverRegistered = false;
		}
	}

	@Override
	public void onTerminate() {
		unregisterNetworkreceiver();
		super.onTerminate();
	}

	// When any activity pauses with the new activity not being ours.
	public void onActivityPause(ParentActivity activity) {
		if (activity.isParentActivityOpened())
			return;
		mInBackground = true;
		Log.v("Application ParentActivity --> Pause", "mInBackground = "
				+ mInBackground);
	}

	// When any activity resumes with a previous activity not being ours.
	public void onActivityResume(ParentActivity activity) {
		if (activity.isParentActivityOpened())
			return;
		mInBackground = false;
		Log.v("Application ParentActivity --> Resume", "mInBackground = "
				+ mInBackground);
		checkNWStatus();
	}

	// When any activity pauses with the new activity not being ours.
	public void onActivityPause(ParentFragmentActivity activity) {
		if (activity.isParentActivityOpened())
			return;
		mInBackground = true;
		Log.v("Application ParentFragmentActivity --> Pause",
				"mInBackground = " + mInBackground);
	}

	// When any activity resumes with a previous activity not being ours.
	public void onActivityResume(ParentFragmentActivity activity) {
		if (activity.isParentActivityOpened())
			return;
		mInBackground = false;
		Log.v("Application ParentFragmentActivity --> Resume",
				"mInBackground = " + mInBackground);
		checkNWStatus();
	}

	// Helper to find if the Application is in background from any activity.
	public static boolean isApplicationInBackground() {
		return mInBackground;
	}

	private void checkNWStatus() {
		int NetworkState = -1;
		NetworkInfo networkinfo = ((ConnectivityManager) m_Context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (networkinfo == null) {
			NetworkState = AppConstants.NETWORK_STATE_UNAVAILABLE;
			Log.v("MyShaleWell - NW state ==> ", "DISCONNECTED");
		} else if (networkinfo.isConnectedOrConnecting()) {
			NetworkState = AppConstants.NETWORK_STATE_AVAILABLE;
			Log.v("MyShaleWell - NW state ==> ", "CONNECTED");
		} else {
			NetworkState = AppConstants.NETWORK_STATE_UNAVAILABLE;
			Log.v("MyShaleWell - NW state ==> ", "DISCONNECTED");
		}
		/**
		 * if ((Application is running in Online mode AND Internet is
		 * unavailable)) OR ((Application is running in Offline mode AND
		 * Internet is now available)), then show the alert
		 **/
		if (((!isOfflineModeOn) && (NetworkState == AppConstants.NETWORK_STATE_UNAVAILABLE))
				|| ((isOfflineModeOn) && (NetworkState == AppConstants.NETWORK_STATE_AVAILABLE))) {
			Log.v("isApplicationInBackground", isApplicationInBackground() + "");
			if (!isApplicationInBackground()) {
				Intent startIntent = new Intent(m_Context,
						NWDialogActivity.class);
				startIntent.putExtra("Network_State", NetworkState);
				startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				m_Context.startActivity(startIntent);
			}
		}
	}

}
