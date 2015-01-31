package com.mxo2.votingapp;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.mxo2.votingapp.dialogs.AlertDialog;
import com.mxo2.votingapp.dialogs.AlertDialog.DialogOptionSelectionListener;
import com.mxo2.votingapp.utils.AppUtils;
import com.mxo2.votingapp.utils.ParentActivity;
import com.mxo2.votingapp.R;

public class HomeScreen extends ParentActivity implements
		DialogOptionSelectionListener {

	private AppUtils m_AppUtils = null;
	private AlertDialog dialogObj = null;
	private Application m_Application = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home_screen);

		m_Application = (MainAppClass) this.getApplication();
		((MainAppClass) m_Application).setContext(HomeScreen.this);

		m_AppUtils = new AppUtils(HomeScreen.this);
		dialogObj = new AlertDialog(HomeScreen.this);

		findViewById(R.id.img_option1).setOnClickListener(clickListener);
		findViewById(R.id.img_option2).setOnClickListener(clickListener);
		findViewById(R.id.img_option3).setOnClickListener(clickListener);
		findViewById(R.id.img_option4).setOnClickListener(clickListener);

		if (!m_AppUtils.isNetworkConnected()) {
			dialogObj.inputMissingDialog(HomeScreen.this.getResources()
					.getString(R.string.network_failure));
			MainAppClass.isOfflineModeOn = true;
		} else {
			MainAppClass.isOfflineModeOn = false;
		}

	}

	@Override
	public void OnDialogOptionSelected(String[] input) {

	}

	@Override
	public void onBackPressed() {
		HomeScreen.this.finish();
	}

	@Override
	public void finish() {
		((MainAppClass) m_Application).unregisterNetworkreceiver();
		super.finish();
	}

	/**
	 * Handle the action when user Skips the option to add available users. In
	 * that case redirect the user to Add/Edit page to manually add the item.
	 **/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (data != null) {
			if (data.hasExtra("KillApp")) {
				if (data.getBooleanExtra("KillApp", false)) {
					HomeScreen.this.finish();
				}
			}
		}

	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.img_option1:
				Intent i = new Intent(HomeScreen.this, ElectionList.class);
				HomeScreen.this.startActivity(i);
				break;
			case R.id.img_option2:
				Intent result = new Intent(HomeScreen.this,
						ResultfElectionList.class);
				HomeScreen.this.startActivity(result);
				break;
			case R.id.img_option3:
				Intent opinionPolling = new Intent(HomeScreen.this,
						OpinionPolling.class);
				HomeScreen.this.startActivity(opinionPolling);
				break;
			case R.id.img_option4:
				Intent aboutUs = new Intent(HomeScreen.this,
						AboutUs.class);
				HomeScreen.this.startActivity(aboutUs);
			default:
				break;
			}
		}
	};

}
