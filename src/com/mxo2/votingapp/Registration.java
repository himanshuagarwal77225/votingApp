package com.mxo2.votingapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;

import com.mxo2.votingapp.dialogs.AlertDialog;
import com.mxo2.votingapp.dialogs.DatePicker_Dialog;
import com.mxo2.votingapp.dialogs.DatePicker_Dialog.DateSelectedListener;
import com.mxo2.votingapp.dialogs.Progress_Dialog;
import com.mxo2.votingapp.utils.AppUtils;
import com.mxo2.votingapp.utils.Log;
import com.mxo2.votingapp.utils.ParentFragmentActivity;
import com.mxo2.votingapp.webservice.WebserviceClient;

public class Registration extends ParentFragmentActivity implements
		DateSelectedListener {

	private EditText edt_firstname, edt_lastname, edt_email, edt_password,
			edt_dob, edt_mobile, edt_voter, edt_pan;
	private RadioButton rd_male, rd_female;
	private String date_of_birth = "";
	private static final int DOB_DATEPICKER_DIALOG = 1;

	private AlertDialog dialogSmall = null;
	private Resources appResource = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.registration);

		dialogSmall = new AlertDialog(Registration.this);
		appResource = Registration.this.getResources();

		edt_firstname = (EditText) findViewById(R.id.input_firstname);
		edt_lastname = (EditText) findViewById(R.id.input_lastname);
		edt_email = (EditText) findViewById(R.id.input_email);
		edt_password = (EditText) findViewById(R.id.input_password);
		edt_dob = (EditText) findViewById(R.id.input_dob);
		edt_mobile = (EditText) findViewById(R.id.input_mobile);
		edt_voter = (EditText) findViewById(R.id.input_voterid);
		edt_pan = (EditText) findViewById(R.id.input_pan);

		rd_male = (RadioButton) findViewById(R.id.rd_male);
		rd_female = (RadioButton) findViewById(R.id.rd_female);
		rd_male.setChecked(true);

		edt_firstname.setHint(appResource.getString(R.string.first)
				+ " "
				+ appResource.getString(R.string.name).toLowerCase(
						Locale.getDefault()));
		edt_lastname.setHint(appResource.getString(R.string.last)
				+ " "
				+ appResource.getString(R.string.name).toLowerCase(
						Locale.getDefault()));
		edt_email.setHint(appResource.getString(R.string.email)
				+ appResource.getString(R.string.star));
		edt_password.setHint(appResource.getString(R.string.password)
				+ appResource.getString(R.string.star));
		edt_dob.setHint(appResource.getString(R.string.dob)
				+ appResource.getString(R.string.star));
		edt_mobile.setHint(appResource.getString(R.string.mobile)
				+ appResource.getString(R.string.number)
				+ appResource.getString(R.string.star));
		edt_voter.setHint(appResource.getString(R.string.voter_id) + " "
				+ appResource.getString(R.string.number));
		edt_pan.setHint(appResource.getString(R.string.pan) + " "
				+ appResource.getString(R.string.card)
				+ appResource.getString(R.string.number));

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String number = tm.getLine1Number();
		if (number != null && number.length() > 0) {
			edt_mobile.setText(number);
		}

		if (getEmail(Registration.this) != null) {
			edt_email.setText(getEmail(Registration.this));
		}

		findViewById(R.id.submit).setOnClickListener(clickListener);
		edt_dob.setOnClickListener(clickListener);

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		date_of_birth = dateFormat.format(cal.getTime());

	}

	@Override
	public void onDateSelected(String sel_date, int type) {
		try {
			switch (type) {
			case DOB_DATEPICKER_DIALOG:
				date_of_birth = sel_date;
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date date = (Date) formatter.parse(date_of_birth);
				String convertedDate = (String) android.text.format.DateFormat
						.format("MMM dd, yyyy", date);
				edt_dob.setText(convertedDate);
				break;
			}
		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
		}
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.submit) {
				if (areAllFieldsValid()) {
					ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
					postParameters.add(new BasicNameValuePair("fname",
							edt_firstname.getText().toString()));
					postParameters.add(new BasicNameValuePair("lname",
							edt_lastname.getText().toString()));
					postParameters.add(new BasicNameValuePair("email",
							edt_email.getText().toString()));
					postParameters.add(new BasicNameValuePair("pass",
							edt_password.getText().toString()));
					postParameters.add(new BasicNameValuePair("dob",
							date_of_birth));
					postParameters.add(new BasicNameValuePair("mobile",
							edt_mobile.getText().toString()));
					postParameters.add(new BasicNameValuePair("vid", edt_voter
							.getText().toString()));
					postParameters.add(new BasicNameValuePair("pan", edt_pan
							.getText().toString()));
					new RegistrationTask(Registration.this, postParameters)
							.execute();
				}
			} else if (v.getId() == R.id.input_dob) {
				String currentDate = (String) android.text.format.DateFormat
						.format("MMM dd, yyyy",
								new Date(System.currentTimeMillis()));
				DatePicker_Dialog dateDialogFragment = new DatePicker_Dialog(
						edt_dob, DOB_DATEPICKER_DIALOG, currentDate,
						DatePicker_Dialog.Action_PreventHigher);
				dateDialogFragment.show(
						Registration.this.getSupportFragmentManager(),
						"datePicker");
			}

		}
	};

	/** validate the input **/
	private boolean areAllFieldsValid() {

		boolean isAllFieldsValid = true;
		String label = "";
		try {
			if (edt_email != null) {
				label = appResource.getString(R.string.email);
				if (TextUtils.isEmpty(edt_email.getText().toString())) {
					edt_email.requestFocus();
					dialogSmall.inputMissingDialog(appResource.getString(
							R.string.input_missing,
							label.toLowerCase(Locale.getDefault())));
					return false;
				} else if (!Validation.isValidEmailAddress(edt_email.getText()
						.toString())) {
					edt_email.setText("");
					edt_email.requestFocus();
					dialogSmall.inputMissingDialog(appResource.getString(
							R.string.input_invalid,
							label.toLowerCase(Locale.getDefault())));
					return false;
				}
			}
			if (edt_password != null) {
				label = appResource.getString(R.string.password);
				if (TextUtils.isEmpty(edt_password.getText().toString())) {
					edt_password.requestFocus();
					dialogSmall.inputMissingDialog(appResource.getString(
							R.string.input_missing,
							label.toLowerCase(Locale.getDefault())));
					return false;
				} else if (edt_password.getText().toString().length() < 6) {
					edt_password.requestFocus();
					edt_password.setText("");
					dialogSmall.inputMissingDialog(appResource
							.getString(R.string.pswrd_invalid));
					return false;
				} else if (Validation.containsDigit(edt_password.getText()
						.toString()) == false) {
					edt_password.setText("");
					edt_password.requestFocus();
					dialogSmall.inputMissingDialog(appResource
							.getString(R.string.pswrd_invalid));
					return false;
				} else if (Validation.containsLetter(edt_password.getText()
						.toString()) == false) {
					edt_password.setText("");
					edt_password.requestFocus();
					dialogSmall.inputMissingDialog(appResource
							.getString(R.string.pswrd_invalid));
					return false;
				}
			}
			if (edt_dob != null) {
				label = appResource.getString(R.string.dob);
				if (TextUtils.isEmpty(edt_email.getText().toString())) {
					edt_email.requestFocus();
					dialogSmall.inputMissingDialog(appResource.getString(
							R.string.input_missing,
							label.toLowerCase(Locale.getDefault())));
					return false;
				}
			}
			if (edt_mobile != null) {
				label = appResource.getString(R.string.mobile) + " "
						+ appResource.getString(R.string.number);
				if (TextUtils.isEmpty(edt_mobile.getText().toString())) {
					edt_mobile.requestFocus();
					dialogSmall.inputMissingDialog(appResource.getString(
							R.string.input_missing,
							label.toLowerCase(Locale.getDefault())));
					return false;
				} else if (!Validation.isValidNumeric(edt_mobile.getText()
						.toString())) {
					edt_mobile.setText("");
					edt_mobile.requestFocus();
					dialogSmall.inputMissingDialog(appResource.getString(
							R.string.input_invalid,
							label.toLowerCase(Locale.getDefault())));
					return false;
				}
			}
		} catch (Exception e) {
			Log.v("Exception ==> ", e.toString());
		}
		return isAllFieldsValid;
	}

	private class RegistrationTask extends AsyncTask<Void, Void, Boolean> {

		private Registration task_activity = null;
		private AlertDialog dialogObj = null;
		private Progress_Dialog m_ProgressDialog = null;
		private ArrayList<NameValuePair> postParameters = null;
		private String reg_result = "";

		public RegistrationTask(Registration _activity,
				ArrayList<NameValuePair> postParameters) {
			task_activity = _activity;
			dialogObj = new AlertDialog(task_activity);
			m_ProgressDialog = new Progress_Dialog(task_activity);
			this.postParameters = postParameters;
		}

		@Override
		protected void onPreExecute() {
			m_ProgressDialog.showProgressDialog();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (postParameters == null) {
				return false;
			}
			AppUtils m_AppUtils = new AppUtils(task_activity);
			String reg_result = new WebserviceClient(m_AppUtils)
					.doRegistration(postParameters);
			if (TextUtils.isEmpty(reg_result)) {
				return false;
			}
			return true;
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

	static String getEmail(Context context) {
		AccountManager accountManager = AccountManager.get(context);
		Account account = getAccount(accountManager);

		if (account == null) {
			return null;
		} else {
			return account.name;
		}
	}

	private static Account getAccount(AccountManager accountManager) {
		Account[] accounts = accountManager.getAccountsByType("com.google");
		Account account;
		if (accounts.length > 0) {
			account = accounts[0];
		} else {
			account = null;
		}
		return account;
	}

}
