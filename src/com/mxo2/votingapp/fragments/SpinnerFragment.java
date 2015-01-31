package com.mxo2.votingapp.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mxo2.votingapp.db.DatabaseSchema;
import com.mxo2.votingapp.utils.AppConstants;
import com.mxo2.votingapp.utils.AppUtils;
import com.mxo2.votingapp.utils.Log;
import com.mxo2.votingapp.R;

@SuppressLint("NewApi")
public class SpinnerFragment extends Fragment {

	private Activity m_Activity = null;
	private AppUtils m_AppUtils = null;
	private Resources m_Resources = null;
	public Spinner spnr_filter1, spnr_filter2;
	private SpinnerFilterActionListener mCallback = null;

	private JSONArray districtJsonArray = null;
	private JSONArray constJsonArray = null;

	public ArrayList<String> districtIdArray = null;
	public ArrayList<String> districtNameArray = null;
	public ArrayList<String> constIdArray = null;
	public ArrayList<String> constNameArray = null;

	private int mCurrentOption = -1;
	private int mInput = -1;
	public int selDistrictItemPosition = -1;
	public int selConstItemPosition = -1;

	private int m_Action = -1;
	private final int ACTION_COMMON = 1;
	private final int ACTION_DISTRICT = 2;
	private final int ACTION_CONSTITUENCY = 3;
	private final int ACTION_FINISH = 4;

	public interface SpinnerFilterActionListener {
		public void onSpinnerFilterAction();

		public void onSpinnerDataSet();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View fragment_view = inflater.inflate(R.layout.fragment_spinners,
				container, false);

		spnr_filter1 = (Spinner) fragment_view
				.findViewById(R.id.filter_spinner1);
		spnr_filter2 = (Spinner) fragment_view
				.findViewById(R.id.filter_spinner2);

		return fragment_view;

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			m_Activity = activity;
			mCallback = (SpinnerFilterActionListener) activity;
			m_Resources = m_Activity.getResources();
		} catch (ClassCastException e) {
			Log.v("Exception ==> ", e.toString());
		}
	}

	public void setUIelements(AppUtils appUtils, int moduleType, int input) {

		m_AppUtils = appUtils;
		mCurrentOption = moduleType;
		mInput = input;
		switch (mCurrentOption) {
		case AppConstants.DISTRICT:
			setUIfor_CommonFilters();
			m_Action = ACTION_DISTRICT;
			break;
		case AppConstants.CONSTITUENCY:
			m_Action = ACTION_CONSTITUENCY;
			break;
		default:
			setUIfor_CommonFilters();
			m_Action = ACTION_COMMON;
			break;
		}

	}

	private void setUIfor_CommonFilters() {

		spnr_filter1.setOnItemSelectedListener(null);
		spnr_filter2.setOnItemSelectedListener(null);

	}

	private OnItemSelectedListener spinner_selection_listener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			switch (parent.getId()) {
			case R.id.filter_spinner1:
				handleFirstFilterItemSelection(position);
				break;
			case R.id.filter_spinner2:
				selConstItemPosition = position;
				m_Action = ACTION_FINISH;
				mCallback.onSpinnerFilterAction();
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	protected void handleFirstFilterItemSelection(int position) {

		selDistrictItemPosition = position;
		selConstItemPosition = -1;
		spnr_filter2.setOnItemSelectedListener(null);
		m_Action = ACTION_CONSTITUENCY;
		mCallback.onSpinnerFilterAction();

	}

	public boolean fetchFilterData() {

		boolean isSuccessful = false;
		try {
			switch (m_Action) {
			case ACTION_COMMON:
				isSuccessful = getCommonSpinnerData();
				break;
			case ACTION_DISTRICT:
				if (fetchDistrictList()) {
					fetchConstituencyList();
					isSuccessful = true;
				}
				break;
			case ACTION_CONSTITUENCY:
				isSuccessful = fetchConstituencyList();
				break;
			case ACTION_FINISH:
				isSuccessful = true;
				break;
			}
		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
			return false;
		}
		return isSuccessful;

	}

	public void setFilterData() {

		try {
			switch (m_Action) {
			case ACTION_COMMON:
				setCommon(m_Activity);
				break;
			case ACTION_DISTRICT:
				fillDistrictSpinner(m_Activity);
				fillConstituencySpinner(m_Activity);
				break;
			case ACTION_CONSTITUENCY:
				fillConstituencySpinner(m_Activity);
				break;
			case ACTION_FINISH:
				break;
			}
			mCallback.onSpinnerDataSet();
		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
		}

	}

	public boolean getCommonSpinnerData() {
		try {
			if (fetchDistrictList()) {
				fetchConstituencyList();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.v("Exception --> ", e.toString());
			return false;
		}
	}

	public boolean fetchDistrictList() {

		try {
			districtJsonArray = null;
			constJsonArray = null;
			districtIdArray = null;
			districtNameArray = null;
			constIdArray = null;
			constNameArray = null;

			districtJsonArray = m_AppUtils.getDistrictList(mInput);
			if (districtJsonArray == null) {
				return false;
			}
			districtIdArray = new ArrayList<String>();
			districtNameArray = new ArrayList<String>();
			if (districtJsonArray.length() > 0) {
				districtIdArray.add("-1");
				districtNameArray.add(m_Resources.getString(
						R.string.select_default,
						m_Resources.getString(R.string.district)));
			}
			for (int index = 0; index < districtJsonArray.length(); index++) {
				JSONObject districtItem = districtJsonArray
						.optJSONObject(index);
				districtIdArray.add(districtItem
						.optString(DatabaseSchema.DISTRICT_MASTER.ID));
				districtNameArray.add(districtItem
						.optString(DatabaseSchema.DISTRICT_MASTER.NAME));
			}
			return true;
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
			return false;
		}

	}

	public boolean fetchConstituencyList() {

		try {
			constJsonArray = null;
			constIdArray = null;
			constNameArray = null;

			if (districtIdArray == null) {
				return false;
			} else if (districtIdArray.size() == 0) {
				return false;
			}
			int district_id = 0;
			if (selDistrictItemPosition != -1) {
				if (selDistrictItemPosition == 0) {
					district_id = 1;
				} else {
					district_id = selDistrictItemPosition;
				}
			} else {
				district_id = 1;
			}
			constJsonArray = m_AppUtils.getConstituencyList(mInput,
					Integer.valueOf(districtIdArray.get(district_id)));
			if (constJsonArray == null) {
				return false;
			}
			constIdArray = new ArrayList<String>();
			constNameArray = new ArrayList<String>();
			if (constJsonArray.length() > 0) {
				constIdArray.add("-1");
				constNameArray.add(m_Resources.getString(
						R.string.select_default,
						m_Resources.getString(R.string.constituency)));
			}
			for (int index = 0; index < constJsonArray.length(); index++) {
				JSONObject const_item = constJsonArray.optJSONObject(index);
				constIdArray.add(const_item
						.optString(DatabaseSchema.CONSTITUENCY_MASTER.ID));
				constNameArray.add(const_item
						.optString(DatabaseSchema.CONSTITUENCY_MASTER.NAME));
			}
			return true;
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
			return false;
		}

	}

	public void setCommon(Activity task_activity) {
		fillDistrictSpinner(task_activity);
		fillConstituencySpinner(task_activity);
	}

	public void fillDistrictSpinner(Activity task_activity) {

		try {
			if (districtNameArray == null) {
				clearItemsAndVariables(ACTION_DISTRICT, task_activity);
			} else if (districtNameArray.size() == 0) {
				clearItemsAndVariables(ACTION_DISTRICT, task_activity);
			} else {
				restoreItemsAndVariables(ACTION_DISTRICT, task_activity);
				ArrayAdapter<String> DistrictAdapter = new ArrayAdapter<String>(
						task_activity, R.layout.spinnerrow, R.id.item,
						districtNameArray);
				spnr_filter1.setAdapter(DistrictAdapter);
				if (selDistrictItemPosition != -1) {
					spnr_filter1.setSelection(selDistrictItemPosition, false);
				} else {
					spnr_filter1.setSelection(0, false);
					selDistrictItemPosition = 0;
				}
				DistrictAdapter.notifyDataSetChanged();
				spnr_filter1
						.setOnItemSelectedListener(spinner_selection_listener);
			}
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
		}

	}

	public void fillConstituencySpinner(Activity task_activity) {

		try {
			if (constNameArray == null) {
				clearItemsAndVariables(ACTION_CONSTITUENCY, task_activity);
			} else if (constNameArray.size() == 0) {
				clearItemsAndVariables(ACTION_CONSTITUENCY, task_activity);
			} else {
				restoreItemsAndVariables(ACTION_CONSTITUENCY, task_activity);
				ArrayAdapter<String> ConstAdapter = new ArrayAdapter<String>(
						task_activity, R.layout.spinnerrow, R.id.item,
						constNameArray);
				spnr_filter2.setAdapter(ConstAdapter);
				if (selConstItemPosition != -1) {
					spnr_filter2.setSelection(selConstItemPosition, false);
				} else {
					spnr_filter2.setSelection(0, false);
					selConstItemPosition = 0;
				}
				ConstAdapter.notifyDataSetChanged();
				spnr_filter2
						.setOnItemSelectedListener(spinner_selection_listener);
			}
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
		}

	}

	private void clearItemsAndVariables(int action, Activity task_activity) {

		switch (action) {
		case ACTION_DISTRICT:
			spnr_filter1.setEnabled(false);
			spnr_filter2.setEnabled(false);
			spnr_filter2.setAdapter(m_AppUtils.clearSpinner(task_activity));
			break;
		case ACTION_CONSTITUENCY:
			spnr_filter2.setEnabled(false);
			spnr_filter2.setAdapter(m_AppUtils.clearSpinner(task_activity));
			break;
		}

	}

	private void restoreItemsAndVariables(int action, Activity task_activity) {

		switch (action) {
		case ACTION_DISTRICT:
			spnr_filter1.setEnabled(true);
			spnr_filter2.setEnabled(true);
			break;
		case ACTION_CONSTITUENCY:
			spnr_filter2.setEnabled(true);
			break;
		}

	}

}
