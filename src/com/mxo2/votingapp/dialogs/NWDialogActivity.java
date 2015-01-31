package com.mxo2.votingapp.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mxo2.votingapp.HomeScreen;
import com.mxo2.votingapp.MainAppClass;
import com.mxo2.votingapp.utils.AppConstants;
import com.mxo2.votingapp.utils.ParentActivity;
import com.mxo2.votingapp.R;

public class NWDialogActivity extends ParentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_dialog);

		TextView txt_title = (TextView) findViewById(R.id.dialog_title);
		Button btn_only_ok = (Button) findViewById(R.id.only_ok_btn);
		LinearLayout lin_btnlayout = (LinearLayout) findViewById(R.id.btn_linlayout);

		lin_btnlayout.setVisibility(View.GONE);
		btn_only_ok.setVisibility(View.VISIBLE);

		String Message = "";

		if (getIntent() != null) {
			if (getIntent().hasExtra("Network_State")) {
				int nw_state = getIntent().getIntExtra("Network_State", -1);
				if (nw_state == AppConstants.NETWORK_STATE_AVAILABLE) {
					Message = NWDialogActivity.this.getResources().getString(
							R.string.network_restored);
					MainAppClass.isOfflineModeOn = false;
				} else if (nw_state == AppConstants.NETWORK_STATE_UNAVAILABLE) {
					Message = NWDialogActivity.this.getResources().getString(
							R.string.network_failure);
					MainAppClass.isOfflineModeOn = true;
				}
			}
		}

		txt_title.setText(Message);
		btn_only_ok.setVisibility(View.VISIBLE);

		btn_only_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent startIntent = new Intent(NWDialogActivity.this,
						HomeScreen.class);
				startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startIntent.putExtra("KillApp", true);
				NWDialogActivity.this.startActivity(startIntent);
				NWDialogActivity.this.finish();
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
