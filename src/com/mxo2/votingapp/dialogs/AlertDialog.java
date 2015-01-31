package com.mxo2.votingapp.dialogs;

import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mxo2.votingapp.HomeScreen;
import com.mxo2.votingapp.R;
import com.mxo2.votingapp.utils.ImageLoader;

public class AlertDialog {

	private Activity m_Activity;
	private TextView txt_title;
	private EditText edt_input1, edt_input2;
	private ImageView imgLogo;
	private Button btn_only_ok, btn_ok, btn_cancel;
	private LinearLayout lin_btnlayout;
	private Dialog m_dialog;
	private ImageLoader imageLoader;

	public interface DialogOptionSelectionListener {
		public void OnDialogOptionSelected(String[] input);
	}

	public AlertDialog(Activity _activity) {

		m_Activity = _activity;
		m_dialog = new Dialog(m_Activity, R.style.dialog);
		m_dialog.setContentView(R.layout.alert_dialog);
		m_dialog.setCancelable(false);

		txt_title = (TextView) m_dialog.findViewById(R.id.dialog_title);
		edt_input1 = (EditText) m_dialog.findViewById(R.id.input_txt1);
		edt_input2 = (EditText) m_dialog.findViewById(R.id.input_txt2);
		imgLogo = (ImageView) m_dialog.findViewById(R.id.logo_img);
		lin_btnlayout = (LinearLayout) m_dialog
				.findViewById(R.id.btn_linlayout);
		btn_only_ok = (Button) m_dialog.findViewById(R.id.only_ok_btn);
		btn_ok = (Button) m_dialog.findViewById(R.id.ok_btn);
		btn_cancel = (Button) m_dialog.findViewById(R.id.cancel_btn);

		WindowManager.LayoutParams wlmp = m_dialog.getWindow().getAttributes();
		wlmp.gravity = Gravity.CENTER;
		wlmp.width = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
		wlmp.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
		m_dialog.getWindow().setAttributes(wlmp);

	}

	public void actionDialog(String title,String symbol) {

		txt_title.setText(title);
		imgLogo.setImageDrawable(m_Activity.getResources().getDrawable(
				R.drawable.logo));
		edt_input1.setVisibility(View.GONE);
		edt_input2.setVisibility(View.GONE);
		edt_input1.setText("");
		edt_input2.setText("");
		imgLogo.setVisibility(View.VISIBLE);
		imageLoader = new ImageLoader(m_Activity.getApplicationContext());
		imageLoader.DisplayImage(symbol, imgLogo);
		btn_only_ok.setVisibility(View.GONE);
		btn_ok.setText(m_Activity.getResources().getString(R.string.ok));
		m_dialog.show();

		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogOptionSelectionListener m_Callback = (DialogOptionSelectionListener) m_Activity;
				m_dialog.dismiss();
				String[] inputArray = new String[1];
				inputArray[0] = "action_castVote";
				m_Callback.OnDialogOptionSelected(inputArray);
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_dialog.dismiss();
			}
		});

	}

	public void inputMissingDialog(String title) {

		txt_title.setText(title);
		imgLogo.setImageDrawable(m_Activity.getResources().getDrawable(
				R.drawable.logo));
		btn_only_ok.setVisibility(View.VISIBLE);
		lin_btnlayout.setVisibility(View.GONE);
		m_dialog.show();

		btn_only_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_dialog.dismiss();
				Intent home = new Intent(m_Activity,HomeScreen.class);
				home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				m_Activity.startActivity(home);
				m_Activity.finish();
			}
		});
	}

	public void votingPreventionDialog(String title) {

		txt_title.setText(title);
		imgLogo.setImageDrawable(m_Activity.getResources().getDrawable(
				R.drawable.no_voting));
		btn_only_ok.setVisibility(View.VISIBLE);
		imgLogo.setVisibility(View.VISIBLE);
		lin_btnlayout.setVisibility(View.GONE);
		m_dialog.show();

		btn_only_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_dialog.dismiss();
				Intent home = new Intent(m_Activity,HomeScreen.class);
				home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				m_Activity.startActivity(home);
				m_Activity.finish();
			}
		});
	}

	public void TaskStatusDialog(String title, final boolean taskFinish) {

		txt_title.setText(title);
		imgLogo.setImageDrawable(m_Activity.getResources().getDrawable(
				R.drawable.logo));
		btn_only_ok.setVisibility(View.VISIBLE);
		lin_btnlayout.setVisibility(View.GONE);
		m_dialog.show();

		btn_only_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_dialog.dismiss();
				if (taskFinish) {
					m_Activity.finish();
				}
			}
		});
	}

	public void inputActionDialog(String title) {

		imgLogo.setImageDrawable(m_Activity.getResources().getDrawable(
				R.drawable.logo));
		txt_title.setText(title);
		edt_input1.setHint(m_Activity.getResources().getString(R.string.mobile)
				+ " " + m_Activity.getResources().getString(R.string.no) + " "
				+ m_Activity.getResources().getString(R.string.mob_info));
		edt_input2.setHint(m_Activity.getResources().getString(
				R.string.mob_re_enter));

		TelephonyManager tm = (TelephonyManager) m_Activity
				.getSystemService(Context.TELEPHONY_SERVICE);
		String number = tm.getLine1Number().replace("+", "");
		Log.i("Number is ", number);
		edt_input1.setVisibility(View.VISIBLE);
		edt_input2.setVisibility(View.VISIBLE);
		if (number != null && number.length() > 0) {
			edt_input1.setText(number);
			edt_input2.setText(number);
			Log.i("Number  found","number is  available" + number);
		} else {
			Log.i("Number not found","number is not available");
			edt_input1.setText("");
			edt_input2.setText("");
		}
		edt_input1.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		edt_input2.setImeOptions(EditorInfo.IME_ACTION_DONE);
		edt_input2.setInputType(InputType.TYPE_CLASS_PHONE);
		m_dialog.show();

		btn_only_ok.setVisibility(View.GONE);
		btn_ok.setText(m_Activity.getResources().getString(R.string.ok));
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(edt_input1.getText().toString())) {
					String label = m_Activity.getResources().getString(
							R.string.input_missing,
							m_Activity.getResources()
									.getString(R.string.mobile)
									.toLowerCase(Locale.getDefault()));
					Toast.makeText(m_Activity, label, Toast.LENGTH_SHORT)
							.show();
				} else if (edt_input1.getText().toString().length() < 12) {
					String label = m_Activity.getResources().getString(
							R.string.invalid_mo1);
					Toast.makeText(m_Activity, label, Toast.LENGTH_SHORT)
							.show();
				} else if (!edt_input1.getText().toString().startsWith("91")) {
					String label = m_Activity.getResources().getString(
							R.string.invalid_mo1);
					Toast.makeText(m_Activity, label, Toast.LENGTH_SHORT)
							.show();
				} else if (TextUtils.isEmpty(edt_input2.getText().toString())) {
					String label = m_Activity.getResources().getString(
							R.string.mob_re_enter);
					Toast.makeText(m_Activity, label, Toast.LENGTH_SHORT)
							.show();
				} else if (!(edt_input1.getText().toString()
						.equalsIgnoreCase(edt_input2.getText().toString()))) {
					String label = m_Activity.getResources().getString(
							R.string.invalid_mo2);
					Toast.makeText(m_Activity, label, Toast.LENGTH_SHORT)
							.show();
				} else {
					String[] inputArray = new String[2];
					inputArray[0] = "action_verification";
					inputArray[1] = edt_input1.getText().toString();
					DialogOptionSelectionListener m_Callback = (DialogOptionSelectionListener) m_Activity;
					m_dialog.dismiss();
					m_Callback.OnDialogOptionSelected(inputArray);
				}
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_dialog.dismiss();
			}
		});
	}

}
