package com.mxo2.votingapp.utils;

import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mxo2.votingapp.MainAppClass;
import com.mxo2.votingapp.dialogs.NWDialogActivity;

/**
 * This is a BroadCast receiver class to monitor Network Changes. As per the
 * requirement, whenever the internet state changes, online-offline and
 * vice-versa, the user should be alerted with a dialog and redirected back to
 * home page. Moreover, it takes care that no alert should be displayed whenever
 * the application is in background.
 **/
public class NWChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		int NetworkState = -1;
		NetworkInfo networkinfo = ((ConnectivityManager) context
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
		Log.v("MainAppClass.isOfflineModeOn ==> ", MainAppClass.isOfflineModeOn
				+ "");
		if (((!MainAppClass.isOfflineModeOn) && (NetworkState == AppConstants.NETWORK_STATE_UNAVAILABLE))
				|| ((MainAppClass.isOfflineModeOn) && (NetworkState == AppConstants.NETWORK_STATE_AVAILABLE))) {
			showNWChangeAlert(context, NetworkState);
		}
	}

	private void showNWChangeAlert(Context context, int NetworkState) {
		if (isApplicationInForeground(context)) {
			Intent startIntent = new Intent(context, NWDialogActivity.class);
			startIntent.putExtra("Network_State", NetworkState);
			startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(startIntent);
		}
	}

	private boolean isApplicationInForeground(Context context) {

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;
		String packageName = componentInfo.getPackageName();
		Log.v("RunningTask --> packageName ==> ", packageName);
		if (packageName.contains("com.triforce.myshalewell")) {
			Log.v("isApplicationInForeground ", "true");
			return true;
		} else {
			Log.v("isApplicationInForeground ", "false");
			return false;
		}
	}

}
