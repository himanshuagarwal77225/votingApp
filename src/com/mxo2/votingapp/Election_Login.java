package com.mxo2.votingapp;

import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.mxo2.votingapp.dialogs.AlertDialog;
import com.mxo2.votingapp.dialogs.Progress_Dialog;
import com.mxo2.votingapp.dialogs.AlertDialog.DialogOptionSelectionListener;
import com.mxo2.votingapp.utils.AppConstants;
import com.mxo2.votingapp.utils.AppUtils;
import com.mxo2.votingapp.utils.Log;
import com.mxo2.votingapp.utils.ParentActivity;
import com.mxo2.votingapp.webservice.WebserviceClient;
import com.mxo2.votingapp.R;

public class Election_Login extends ParentActivity implements
		DialogOptionSelectionListener {

	private EditText edt_email, edt_password;

	private AppUtils m_AppUtils = null;
	private AlertDialog dialogSmall;
	private Resources appResource = null;

	private String login_id = "";
	private String login_password = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.election_page);

		m_AppUtils = new AppUtils(Election_Login.this);
		dialogSmall = new AlertDialog(Election_Login.this);
		appResource = Election_Login.this.getResources();

		edt_email = (EditText) findViewById(R.id.input_email);
		edt_password = (EditText) findViewById(R.id.input_password);

		findViewById(R.id.login).setOnClickListener(clickListener);
		findViewById(R.id.register).setOnClickListener(clickListener);

		if (!m_AppUtils.isNetworkConnected()) {
			dialogSmall.inputMissingDialog(appResource
					.getString(R.string.network_failure));
		}

	}

	@Override
	public void OnDialogOptionSelected(String[] input) {
		Election_Login.this.finish();
	}

	public Boolean verifyLoginCredentials() {

		try {
			WebserviceClient m_WSclient = new WebserviceClient(m_AppUtils);
			JSONObject login_jsonObj = m_WSclient.checkLoginCredentials(
					login_id, login_password);
			if (login_jsonObj != null) {
				m_AppUtils.settings_editor.putString(
						AppConstants.ActionKeys.emailid,
						login_jsonObj.optString("email"));
				m_AppUtils.settings_editor.putString(
						AppConstants.ActionKeys.password,
						login_jsonObj.optString("pass"));
				m_AppUtils.settings_editor.putString(
						AppConstants.ActionKeys.firstname,
						login_jsonObj.optString("fname"));
				m_AppUtils.settings_editor.putString(
						AppConstants.ActionKeys.lastname,
						login_jsonObj.optString("lname"));
				m_AppUtils.settings_editor.putString(
						AppConstants.ActionKeys.mobile,
						login_jsonObj.optString("mobile"));
				m_AppUtils.settings_editor.putString(
						AppConstants.ActionKeys.address,
						login_jsonObj.optString("address"));
				m_AppUtils.settings_editor.putString(
						AppConstants.ActionKeys.voterid,
						login_jsonObj.optString("voter_id"));
				m_AppUtils.settings_editor.putString(
						AppConstants.ActionKeys.pancard,
						login_jsonObj.optString("pan"));
				m_AppUtils.settings_editor.putString(
						AppConstants.ActionKeys.dob,
						login_jsonObj.optString("dob"));
				m_AppUtils.settings_editor.commit();
				return true;
			}
		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
			return false;
		}
		return false;

	}

	/**
	 * Verify whether the user has provided the input properly or not, whether
	 * the username or password input fields are not empty. In case the user has
	 * not typed any input, show a dialog and alert him
	 **/
	protected boolean areAllFieldsValid() {
		boolean areAllFieldsValid = true;
		String label = "";
		try {
			if (edt_email != null) {
				label = appResource.getString(R.string.email);
				login_id = edt_email.getText().toString();
				if (TextUtils.isEmpty(login_id)) {
					edt_email.requestFocus();
					dialogSmall.inputMissingDialog(appResource.getString(
							R.string.input_missing,
							label.toLowerCase(Locale.getDefault())));
					return false;
				}
			}
			if (edt_password != null) {
				label = appResource.getString(R.string.password);
				login_password = edt_password.getText().toString();
				if (login_password.trim().equals("")
						|| login_password.trim() == null) {
					edt_password.requestFocus();
					dialogSmall.inputMissingDialog(appResource.getString(
							R.string.input_missing,
							label.toLowerCase(Locale.getDefault())));
					return false;
				}
			}
		} catch (Exception e) {
			Log.v("Exception ==> ", e.toString());
		}
		return areAllFieldsValid;
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			View view = Election_Login.this.getWindow().getCurrentFocus();
			/** if the soft keyboard is visible, hide it **/
			if (view != null) {
				InputMethodManager imm = (InputMethodManager) Election_Login.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
			switch (v.getId()) {
			case R.id.login:
				if (areAllFieldsValid()) {
					new LoginTask(Election_Login.this).execute();
				}
				break;
			case R.id.register:
				Intent i = new Intent(Election_Login.this, Registration.class);
				Election_Login.this.startActivity(i);
				break;
			default:
				break;
			}
		}
	};

	private class LoginTask extends AsyncTask<Void, Void, Boolean> {

		private Election_Login task_activity = null;
		private AlertDialog dialogObj = null;
		private Progress_Dialog m_ProgressDialog = null;

		public LoginTask(Election_Login _activity) {
			task_activity = _activity;
			dialogObj = new AlertDialog(task_activity);
			m_ProgressDialog = new Progress_Dialog(task_activity);
		}

		@Override
		protected void onPreExecute() {
			m_ProgressDialog.showProgressDialog();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return verifyLoginCredentials();
		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (m_ProgressDialog != null) {
				m_ProgressDialog.dismissProgressDialog();
			}
			if (result) {
				Intent i = new Intent(task_activity, HomeScreen.class);
				task_activity.startActivity(i);
				task_activity.finish();
			} else {
				dialogObj.inputMissingDialog(task_activity.getResources()
						.getString(R.string.invalid_login));
			}
		}
	}
}
