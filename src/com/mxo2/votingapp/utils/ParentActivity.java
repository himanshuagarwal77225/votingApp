package com.mxo2.votingapp.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

import com.mxo2.votingapp.MainAppClass;

/**
 * This class will be the parent class which will be extended by all Activity
 * type of classes in the application. This has been used to monitor the
 * application presence, i.e., to know whether the application is in foreground
 * or background.
 **/
public class ParentActivity extends Activity {

	private boolean hasStarted = false;
	private boolean mParentActivityOpened = false;

	@Override
	public void onPause() {
		super.onPause();
		if (!isFinishing() && (getApplication() instanceof MainAppClass))
			((MainAppClass) getApplication()).onActivityPause(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (hasStarted && (getApplication() instanceof MainAppClass)) {
			((MainAppClass) getApplication()).onActivityResume(this);
			mParentActivityOpened = false;
		} else {
			hasStarted = true;
		}
	}

	/**
	 * Overriding the default method startActivity of Activity, just to ensure
	 * that activity flow continues normally. Allow starting an Activity
	 * normally if MindGym application is on.
	 **/
	@Override
	public void startActivity(Intent intent) {
		checkIfParentActivity(intent);
		super.startActivity(intent);
	}

	/**
	 * Overriding the default method startActivityForResult of Activity, just to
	 * ensure that activity flow continues normally Allow starting an Activity
	 * normally if MindGym application is on.
	 **/
	@Override
	public void startActivityForResult(Intent intent, int request) {
		checkIfParentActivity(intent);
		super.startActivityForResult(intent, request);
	}

	/**
	 * Whenever we call our own activity, the component and it's package name is
	 * set. If we call an activity from another package, or an open intent
	 * (leaving android to resolve) component has a different package name or it
	 * is null.
	 **/
	private void checkIfParentActivity(Intent intent) {
		ComponentName component = intent.getComponent();
		mParentActivityOpened = false;
		if (component != null
				&& component.getPackageName() != null
				&& component.getPackageName().startsWith(
						AppConstants.APP_PACKAGE_NAME)) {
			mParentActivityOpened = true;
		}
	}

	public boolean isParentActivityOpened() {
		return mParentActivityOpened;
	}

	public boolean isApplicationInBackground() {
		return MainAppClass.isApplicationInBackground();
	}

}
