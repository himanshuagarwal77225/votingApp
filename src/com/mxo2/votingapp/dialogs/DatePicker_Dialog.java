package com.mxo2.votingapp.dialogs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.EditText;

import com.mxo2.votingapp.utils.AppUtils;
import com.mxo2.votingapp.utils.Log;

@SuppressLint("NewApi")
public class DatePicker_Dialog extends DialogFragment {

	private DatePickerDialog m_DatePickerDialog = null;
	private Calendar m_Calendar = null;
	private DateSelectedListener m_Callback = null;
	private EditText m_EdtTextDate = null;
	private String inputDate = "";
	private String referenceDATE = "";
	protected String selectedDATE = "";
	private int Type = -1;
	private int currYEAR = -1;
	private int currMONTH = -1;
	private int currDAY = -1;
	private int refYEAR = -1;
	private int refMONTH = -1;
	private int refDAY = -1;
	private int action = -1;
	public static final int Action_PreventSmall = 1;
	public static final int Action_PreventHigher = 2;
	private boolean dateSet = false;

	public DatePicker_Dialog(EditText edtDate, int type) {
		m_EdtTextDate = edtDate;
		Type = type;
	}

	public DatePicker_Dialog(EditText edtDate, int type, String refDate,
			int _action) {
		m_EdtTextDate = edtDate;
		Type = type;
		referenceDATE = refDate;
		action = _action;
	}

	public interface DateSelectedListener {
		public void onDateSelected(String sel_date, int type);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		try {
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMM dd, yyyy");
			m_Callback = (DateSelectedListener) getActivity();
			dateSet = false;
			inputDate = m_EdtTextDate.getText().toString();

			if (!TextUtils.isEmpty(inputDate))
				/**
				 * Use the date set by the user as the default date in the
				 * picker
				 **/
				m_Calendar = AppUtils.getCalendarFromDateWithCustomFormat(
						inputDate, "MMM dd, yyyy", "dd-MM-yyyy");
			else
				/** Use the current date as the default date in the picker **/
				m_Calendar = Calendar.getInstance();

			if (!TextUtils.isEmpty(inputDate)) {
				currYEAR = m_Calendar.get(Calendar.YEAR);
				currMONTH = m_Calendar.get(Calendar.MONTH);
				currDAY = m_Calendar.get(Calendar.DAY_OF_MONTH);
			}

			if (!TextUtils.isEmpty(referenceDATE)) {
				Date refDate = dateFormat1.parse(referenceDATE);
				refYEAR = (refDate.getYear() + 1900);
				refMONTH = refDate.getMonth();
				refDAY = refDate.getDate();
			} else {
				refYEAR = currYEAR;
				refMONTH = currMONTH;
				refDAY = currDAY;
			}

			/** Create a new instance of DatePickerDialog and return it **/
			m_DatePickerDialog = new DatePickerDialog(
					getActivity(),
					new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view,
								int selectedYear, int selectedMonth,
								int selectedDate) {
							if (!dateSet) {
								String formattedDate = (String) android.text.format.DateFormat
										.format("MMM dd, yyyy", new Date(
												selectedYear - 1900,
												selectedMonth, selectedDate));
								Log.e("onDateSet --> ", formattedDate);
								if (AppUtils.isValidTodayOrFutureDate(
										formattedDate, "MMM dd, yyyy")) {
									DateFormat dateFormat = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss");
									m_EdtTextDate.setText(formattedDate);
									selectedDATE = dateFormat.format(new Date(
											selectedYear - 1900, selectedMonth,
											selectedDate));
									m_Callback.onDateSelected(selectedDATE,
											Type);
									Log.e("selectedDATE --> ", selectedDATE);
									dateSet = !dateSet;
								}
							}
						}
					}, m_Calendar.get(Calendar.YEAR),
					m_Calendar.get(Calendar.MONTH),
					m_Calendar.get(Calendar.DAY_OF_MONTH)) {
				@Override
				public void onDateChanged(DatePicker view, int year, int month,
						int day) {
					if (!TextUtils.isEmpty(referenceDATE)) {
						switch (action) {
						case Action_PreventSmall:
							if (year < refYEAR) {
								view.init(refYEAR, month, day, this);
							} else if (year == refYEAR && month < refMONTH) {
								view.init(refYEAR, refMONTH, day, this);
							} else if (year == refYEAR && month == refMONTH
									&& day < refDAY) {
								view.init(refYEAR, refMONTH, refDAY, this);
							}
							break;
						case Action_PreventHigher:
							if (year > refYEAR) {
								view.init(refYEAR, month, day, this);
							} else if (year == refYEAR && month > refMONTH) {
								view.init(refYEAR, refMONTH, day, this);
							} else if (year == refYEAR && month == refMONTH
									&& day > refDAY) {
								view.init(refYEAR, refMONTH, refDAY, this);
							}
							break;
						}
					}
				}
			};

		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
			return null;
		}
		return m_DatePickerDialog;

	}

}
