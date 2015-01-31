package com.mxo2.votingapp;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.mxo2.votingapp.dialogs.AlertDialog;
import com.mxo2.votingapp.dialogs.Progress_Dialog;
import com.mxo2.votingapp.fragments.SpinnerFragment;
import com.mxo2.votingapp.fragments.SpinnerFragment.SpinnerFilterActionListener;
import com.mxo2.votingapp.utils.AppConstants;
import com.mxo2.votingapp.utils.AppUtils;
import com.mxo2.votingapp.utils.ParentFragmentActivity;
import com.mxo2.votingapp.R;

public class ElectionRegionFilters extends ParentFragmentActivity implements
		SpinnerFilterActionListener {

	private AppUtils m_AppUtils = null;
	private AlertDialog dialogObj = null;
	private SpinnerFragment frag_spinner = null;
	private Resources mResources = null;
	private int electionId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.election_filters);
		m_AppUtils = new AppUtils(ElectionRegionFilters.this);
		dialogObj = new AlertDialog(ElectionRegionFilters.this);
		mResources = ElectionRegionFilters.this.getResources();

		frag_spinner = (SpinnerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.frag_filters);
		findViewById(R.id.action_continue).setOnClickListener(clickListener);
		if (getIntent() != null) {
			if (getIntent().hasExtra("electionId")) {
				electionId = Integer.valueOf(getIntent().getStringExtra(
						"electionId"));
			}
		}
		frag_spinner.setUIelements(m_AppUtils, AppConstants.DISTRICT,
				electionId);
		new FetchListTask(ElectionRegionFilters.this).execute();

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	/**
	 * Overriden callback method of SpinnerFilterActionListener interface of
	 * SpinnerFragment to handle the spinner item selection event of the filter
	 * spinners
	 **/
	@Override
	public void onSpinnerFilterAction() {
		new FetchListTask(ElectionRegionFilters.this).execute();
	}

	/**
	 * Overriden callback method of SpinnerFilterActionListener interface of
	 * SpinnerFragment to indicate that data records have been set on the
	 * spinner
	 **/
	@Override
	public void onSpinnerDataSet() {

	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.action_continue) {
				if (frag_spinner.districtIdArray == null) {
					dialogObj.inputMissingDialog(mResources
							.getString(R.string.district_missing));
					return;
				} else if (frag_spinner.districtIdArray.size() == 0) {
					dialogObj.inputMissingDialog(mResources
							.getString(R.string.district_missing));
					return;
				}
				if (frag_spinner.constIdArray == null) {
					dialogObj.inputMissingDialog(mResources
							.getString(R.string.const_missing));
					return;
				} else if (frag_spinner.constIdArray.size() == 0) {
					dialogObj.inputMissingDialog(mResources
							.getString(R.string.const_missing));
					return;
				}
				int dist_id;
				if (frag_spinner.selDistrictItemPosition != -1) {
					dist_id = frag_spinner.selDistrictItemPosition;
				} else {
					dist_id = 0;
				}
				int c_id;
				if (frag_spinner.selConstItemPosition != -1) {
					c_id = frag_spinner.selConstItemPosition;
				} else {
					c_id = 0;
				}
				String disId = frag_spinner.districtIdArray.get(dist_id);
				String conId = frag_spinner.constIdArray.get(c_id);
				int districtId = -1;
				int constId = -1;
				if (!TextUtils.isEmpty(disId)) {
					districtId = Integer.valueOf(disId);
				}
				if (!TextUtils.isEmpty(conId)) {
					constId = Integer.valueOf(conId);
				}
				if (districtId == -1) {
					dialogObj.inputMissingDialog(mResources
							.getString(R.string.district_missing));
					return;
				} else if (constId == -1) {
					dialogObj.inputMissingDialog(mResources
							.getString(R.string.const_missing));
					return;
				} else {
					Intent _intent = new Intent(ElectionRegionFilters.this,
							PartiesList.class);
					_intent.putExtra("electionId", electionId);
					_intent.putExtra("districtId", districtId);
					_intent.putExtra("constId", constId);
					ElectionRegionFilters.this.startActivity(_intent);
				}
			}
		}
	};

	private class FetchListTask extends AsyncTask<Void, Void, Boolean> {

		private ElectionRegionFilters task_activity = null;
		private Progress_Dialog m_ProgressDialog = null;

		public FetchListTask(ElectionRegionFilters _activity) {
			task_activity = _activity;
			m_ProgressDialog = new Progress_Dialog(task_activity);
		}

		@Override
		protected void onPreExecute() {
			m_ProgressDialog.showProgressDialog();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return frag_spinner.fetchFilterData();
		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (m_ProgressDialog != null) {
				m_ProgressDialog.dismissProgressDialog();
			}
			if (result) {
				frag_spinner.setFilterData();
			}
		}
	}

}
