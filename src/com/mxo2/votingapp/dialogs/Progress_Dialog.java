package com.mxo2.votingapp.dialogs;

import android.app.Activity;
import android.app.ProgressDialog;

import com.mxo2.votingapp.R;

public class Progress_Dialog {

	private Activity m_Activity;
	private ProgressDialog m_Progress;

	public Progress_Dialog(Activity _activity) {
		m_Activity = _activity;
	}

	public void showProgressDialog() {
		m_Progress = new ProgressDialog(m_Activity);
		m_Progress.setCancelable(false);
		m_Progress.show();
		m_Progress.setContentView(R.layout.progress_dialog);
	}

	public void dismissProgressDialog() {
		if (m_Progress != null && m_Progress.isShowing()) {
			m_Progress.dismiss();
		}
	}

}
