package com.mxo2.votingapp;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.Legend.LegendForm;
import com.github.mikephil.charting.utils.Legend.LegendPosition;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.github.mikephil.charting.utils.YLabels;
import com.github.mikephil.charting.utils.YLabels.YLabelPosition;
import com.mxo2.votingapp.dialogs.AlertDialog;
import com.mxo2.votingapp.dialogs.Progress_Dialog;
import com.mxo2.votingapp.pojo.Constituency_Result_Model;
import com.mxo2.votingapp.pojo.OpinionPollingAnswerModel;
import com.mxo2.votingapp.pojo.Party_pojo;
import com.mxo2.votingapp.slidingmenu.CollapseAnimation;
import com.mxo2.votingapp.slidingmenu.ExpandAnimation;
import com.mxo2.votingapp.utils.AppUtils;
import com.mxo2.votingapp.utils.Log;
import com.mxo2.votingapp.webservice.WebserviceClient;

public class OpinionPolling extends Activity implements
		OnSeekBarChangeListener, OnChartValueSelectedListener {

	private List<OpinionPollingAnswerModel> resultList;
	private int totalPrevAns = 0;
	ArrayList<BarEntry> yVals1;
	private BarChart mChart;
	protected String[] mPrevAns;
	private TextView txt_noData;// , txt_info;

	private int prevQueID = -1;
	private SeekBar mSeekBarX, mSeekBarY;
	private TextView tvX, tvY;
	private TextView textTitle3, textTitle4;
	private RadioGroup rgOpinion;
	private Button btnSubmit;
	private Resources appResource = null;
	private String previousQue;
	private TextView prevQuestionTextView;

	private AlertDialog dialogObj = null;

	// menu variable start
	// Declare
	private LinearLayout slidingPanel;
	private boolean isExpanded;
	private DisplayMetrics metrics;
	// private ListView listView;
	private FrameLayout headerPanel;
	private LinearLayout menuPanel;
	private int panelWidth;
	private ImageView menuViewButton;
	private LinearLayout textitem1, textitem2, textitem3, textitem4, textitem5,
			textitem6;
	private TextView textTitle1, textTitle2;
	private ImageView backIconButton;

	FrameLayout.LayoutParams menuPanelParameters;
	FrameLayout.LayoutParams slidingPanelParameters;
	LinearLayout.LayoutParams headerPanelParameters;
	LinearLayout.LayoutParams listViewParameters;

	// menu variable end

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.opinion_polling);

		rgOpinion = (RadioGroup) findViewById(R.id.rgOpinion);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		txt_noData = (TextView) findViewById(R.id.noData_txt);
		textTitle3 = (TextView) findViewById(R.id.txt_title3);
		textTitle4 = (TextView) findViewById(R.id.txt_title4);
		mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
		mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
		prevQuestionTextView = (TextView) findViewById(R.id.prevQuestionTextView);
		mChart = (BarChart) findViewById(R.id.chart1);
		mChart.setOnChartValueSelectedListener(this);
		new FetchListTask(OpinionPolling.this, "getResult").execute(String
				.valueOf(prevQueID));

		textTitle3.setText("Yesterday's");
		textTitle4.setText("Result");
		dialogObj = new AlertDialog(OpinionPolling.this);
		appResource = OpinionPolling.this.getResources();

		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				RadioButton selectRadio = (RadioButton) findViewById(rgOpinion
						.getCheckedRadioButtonId());
				String opinion = selectRadio.getText().toString();

				if (!TextUtils.isEmpty(opinion)) {

					/*
					 * dialogObj.actionDialog(appResource.getString(
					 * R.string.polling_confirmation, opinion),"");
					 */

					Toast.makeText(OpinionPolling.this,
							"Your Opinion is : " + opinion, Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(OpinionPolling.this,
							"Please select your Opinion", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		initMenu("Opinion", "Polling");
	}

	private class FetchListTask extends AsyncTask<String, Void, Boolean> {

		private OpinionPolling task_activity = null;
		private Progress_Dialog m_ProgressDialog = null;
		private AlertDialog dialogObj = null;
		private ArrayList<Party_pojo> partyList = null;
		private AppUtils _appUtils = null;
		private String taskType;
		private String message;

		public FetchListTask(OpinionPolling _activity, String _type) {
			task_activity = _activity;
			m_ProgressDialog = new Progress_Dialog(task_activity);
			dialogObj = new AlertDialog(task_activity);
			_appUtils = new AppUtils(task_activity);
			taskType = _type;
		}

		@Override
		protected void onPreExecute() {
			if (m_ProgressDialog != null) {
				m_ProgressDialog.showProgressDialog();
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {

			boolean isSuccessful = false;
			if (taskType.equalsIgnoreCase("getResult")) {
				WebserviceClient wsClient = new WebserviceClient(_appUtils);
				resultList = wsClient.fetchOpinionResult(Integer
						.valueOf(params[0]));
				if (resultList == null || resultList.isEmpty()) {
					isSuccessful = false;
				} else {

					isSuccessful = true;
				}
			}
			return isSuccessful;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (m_ProgressDialog != null) {
				m_ProgressDialog.dismissProgressDialog();
			}
			if (taskType.equalsIgnoreCase("getResult")) {
				if (result) {
					totalPrevAns = resultList.size();
					mPrevAns = new String[totalPrevAns];
					yVals1 = new ArrayList<BarEntry>();
					for (int i = 0; i < resultList.size(); i++) {
						OpinionPollingAnswerModel resultModel = resultList
								.get(i);
						mPrevAns[i] = resultModel.getAnsID();
						yVals1.add(new BarEntry(Float.parseFloat(resultModel
								.getTotal()), i));
						previousQue = resultModel.getQuestion();
						Log.i("Question name", mPrevAns[i]);
					}

					prevQuestionTextView.setText(previousQue);
					initGraph();
				} else {
					showNoDataUI(task_activity);
				}
			}

		}

	}

	private void showNoDataUI(OpinionPolling task_activity) {

		// txt_info.setVisibility(View.GONE);
		txt_noData.setVisibility(View.VISIBLE);
		txt_noData.setText(task_activity.getResources().getString(
				R.string.no_data));

	}

	public void initGraph() {
		// enable the drawing of values
		mChart.setDrawYValues(true);

		mChart.setDrawValueAboveBar(true);

		mChart.setDescription("");

		// if more than 60 entries are displayed in the chart, no values will be
		// drawn
		mChart.setMaxVisibleValueCount(60);

		// disable 3D
		mChart.set3DEnabled(false);

		mChart.setScaleMinima(1.5f, 0);

		// scaling can now only be done on x- and y-axis separately
		mChart.setPinchZoom(false);

		// draw shadows for each bar that show the maximum value
		// mChart.setDrawBarShadow(true);

		mChart.setUnit("");

		// mChart.setDrawXLabels(false);

		mChart.setDrawGridBackground(false);
		mChart.setDrawHorizontalGrid(true);
		mChart.setDrawVerticalGrid(false);

		// mChart.setDrawLegend(false);

		// mChart.setDrawYLabels(false);

		// sets the text size of the values inside the chart
		mChart.setValueTextSize(16f);

		mChart.setDrawBorder(false);
		// mChart.setBorderPositions(new BorderPosition[] {BorderPosition.LEFT,
		// BorderPosition.RIGHT});

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"OpenSans-Regular.ttf");

		XLabels xl = mChart.getXLabels();
		xl.setPosition(XLabelPosition.BOTTOM);
		xl.setCenterXLabelText(true);
		xl.setTypeface(tf);
		xl.setTextSize(16f);

		YLabels yl = mChart.getYLabels();
		yl.setTypeface(tf);
		yl.setLabelCount(8);
		yl.setPosition(YLabelPosition.LEFT);

		mChart.setValueTypeface(tf);

		Log.i("total Answers", totalPrevAns + "");
		setData(totalPrevAns, 50);

		// setting data
		mSeekBarY.setProgress(50);
		mSeekBarX.setProgress(totalPrevAns);

		mSeekBarY.setOnSeekBarChangeListener(this);
		mSeekBarX.setOnSeekBarChangeListener(this);

		Legend l = mChart.getLegend();
		// l.setPosition(LegendPosition.RIGHT_OF_CHART_INSIDE);
		l.setXEntrySpace(4f);
		l.setFormSize(10f); // set the size of the legend forms/shapes
		l.setForm(LegendForm.CIRCLE); // set what type of form/shape should be
										// used
		l.setPosition(LegendPosition.BELOW_CHART_LEFT);
		l.setXEntrySpace(5f); // set the space between the legend entries on the
								// x-axis
		l.setYEntrySpace(5f); // set the space between the legend entries on the
								// y-axis

		// mChart.setDrawLegend(false)
	}

	private void setData(int count, float range) {

		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			xVals.add(mPrevAns[i]);
		}

		// ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

		for (int i = 0; i < count; i++) {
			float mult = (range + 1);
			float val = (float) (Math.random() * mult);
			// yVals1.add(new BarEntry(val, i));
		}

		BarDataSet set1 = new BarDataSet(yVals1, "Percentage (%)");
		set1.setBarSpacePercent(40f);
		// set1.setBarSpacePercent(20f);
		set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
		set1.setBarShadowColor(Color.rgb(203, 203, 203));

		ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
		dataSets.add(set1);

		BarData data = new BarData(xVals, dataSets);

		mChart.setData(data);
		mChart.invalidate();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

		tvX.setText("" + (mSeekBarX.getProgress() + 1));
		tvY.setText("" + (mSeekBarY.getProgress()));

		setData(mSeekBarX.getProgress(), mSeekBarY.getProgress());
		mChart.invalidate();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@SuppressLint("NewApi")
	@Override
	public void onValueSelected(Entry e, int dataSetIndex) {

		if (e == null)
			return;

		RectF bounds = mChart.getBarBounds((BarEntry) e);
		PointF position = mChart.getPosition(e);

		Log.i("bounds", bounds.toString());
		Log.i("position", position.toString());
	}

	public void onNothingSelected() {
	};

	public void initMenu(String title1, String title2) {
		// Initialize
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		panelWidth = (int) ((metrics.widthPixels) * 0.80);

		headerPanel = (FrameLayout) findViewById(R.id.header);
		headerPanelParameters = (LinearLayout.LayoutParams) headerPanel
				.getLayoutParams();
		headerPanelParameters.width = metrics.widthPixels;
		headerPanel.setLayoutParams(headerPanelParameters);

		menuPanel = (LinearLayout) findViewById(R.id.menuPanel);
		menuPanelParameters = (FrameLayout.LayoutParams) menuPanel
				.getLayoutParams();
		menuPanelParameters.gravity = Gravity.RIGHT;
		menuPanelParameters.width = panelWidth;

		menuPanel.setLayoutParams(menuPanelParameters);

		slidingPanel = (LinearLayout) findViewById(R.id.slidingPanel);
		slidingPanelParameters = (FrameLayout.LayoutParams) slidingPanel
				.getLayoutParams();
		slidingPanelParameters.width = metrics.widthPixels;
		slidingPanelParameters.gravity = Gravity.LEFT;
		slidingPanel.setLayoutParams(slidingPanelParameters);

		/*
		 * listView = (ListView) findViewById(R.id.list); listViewParameters =
		 * (LinearLayout.LayoutParams) listView .getLayoutParams();
		 * listViewParameters.width = metrics.widthPixels;
		 * listView.setLayoutParams(listViewParameters);
		 */

		textitem1 = (LinearLayout) findViewById(R.id.menu_item_1);
		textitem2 = (LinearLayout) findViewById(R.id.menu_item_2);
		textitem3 = (LinearLayout) findViewById(R.id.menu_item_3);
		textitem4 = (LinearLayout) findViewById(R.id.menu_item_4);
		textitem5 = (LinearLayout) findViewById(R.id.menu_item_5);
		textitem6 = (LinearLayout) findViewById(R.id.menu_item_6);
		backIconButton = (ImageView) findViewById(R.id.backIconButton);
		textTitle1 = (TextView) findViewById(R.id.txt_title1);
		textTitle2 = (TextView) findViewById(R.id.txt_title2);

		textTitle1.setText(title1);
		textTitle2.setText(title2);

		textitem4.setBackgroundColor((getResources().getColor(R.color.blue)));
		backIconButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent previsousIntent = new Intent(OpinionPolling.this,
						HomeScreen.class);
				startActivity(previsousIntent);
				finish();
			}
		});

		textitem1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("Text menu item 1", "Menu Item Clickerd");
				isExpanded = false;
				menuPanel.setVisibility(View.GONE);
				textitem1.setBackgroundColor((getResources()
						.getColor(R.color.blue)));
				// Collapse
				new CollapseAnimation(slidingPanel, panelWidth,
						TranslateAnimation.RELATIVE_TO_PARENT, -0.33f,
						TranslateAnimation.RELATIVE_TO_PARENT, 0.0f, 0, 0.0f,
						0, 0.0f);
				// if you want left to right just remove ( - ) before 0.33f

				Intent item1 = new Intent(OpinionPolling.this, HomeScreen.class);
				item1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(item1);
				finish();
			}
		});
		textitem2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("Text menu item 2", "Menu Item Clickerd");
				isExpanded = false;
				menuPanel.setVisibility(View.GONE);
				textitem2.setBackgroundColor((getResources()
						.getColor(R.color.blue)));
				// Collapse
				new CollapseAnimation(slidingPanel, panelWidth,
						TranslateAnimation.RELATIVE_TO_PARENT, -0.33f,
						TranslateAnimation.RELATIVE_TO_PARENT, 0.0f, 0, 0.0f,
						0, 0.0f);
				// if you want left to right just remove ( - ) before 0.33f

				Intent item2 = new Intent(OpinionPolling.this,
						ElectionList.class);
				startActivity(item2);

			}
		});

		textitem3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("Text menu item 3", "Menu Item Clickerd");
				isExpanded = false;
				menuPanel.setVisibility(View.GONE);
				textitem3.setBackgroundColor((getResources()
						.getColor(R.color.blue)));
				// Collapse
				new CollapseAnimation(slidingPanel, panelWidth,
						TranslateAnimation.RELATIVE_TO_PARENT, -0.33f,
						TranslateAnimation.RELATIVE_TO_PARENT, 0.0f, 0, 0.0f,
						0, 0.0f);
				// if you want left to right just remove ( - ) before 0.33f

				Intent item1 = new Intent(OpinionPolling.this,
						ResultfElectionList.class);
				item1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(item1);
				finish();

			}
		});
		textitem4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("Text menu item 4", "Menu Item Clickerd");
				isExpanded = false;
				menuPanel.setVisibility(View.GONE);
				textitem4.setBackgroundColor((getResources()
						.getColor(R.color.blue)));
				// Collapse
				new CollapseAnimation(slidingPanel, panelWidth,
						TranslateAnimation.RELATIVE_TO_PARENT, -0.33f,
						TranslateAnimation.RELATIVE_TO_PARENT, 0.0f, 0, 0.0f,
						0, 0.0f);
				// if you want left to right just remove ( - ) before 0.33f

				// Intent item1 = new Intent(OpinionPolling.this,
				// HomeScreen.class);
				// item1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				// | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				// startActivity(item1);
				// finish();

			}
		});
		textitem5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("Text menu item 5", "Menu Item Clickerd");
				isExpanded = false;
				menuPanel.setVisibility(View.GONE);
				textitem5.setBackgroundColor((getResources()
						.getColor(R.color.blue)));
				// Collapse
				new CollapseAnimation(slidingPanel, panelWidth,
						TranslateAnimation.RELATIVE_TO_PARENT, -0.33f,
						TranslateAnimation.RELATIVE_TO_PARENT, 0.0f, 0, 0.0f,
						0, 0.0f);
				// if you want left to right just remove ( - ) before 0.33f

				Intent item1 = new Intent(OpinionPolling.this, AboutUs.class);
				item1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(item1);
				finish();

			}
		});
		textitem6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("Text menu item 6", "Menu Item Clickerd");
				isExpanded = false;
				menuPanel.setVisibility(View.GONE);
				textitem6.setBackgroundColor((getResources()
						.getColor(R.color.blue)));
				// Collapse
				new CollapseAnimation(slidingPanel, panelWidth,
						TranslateAnimation.RELATIVE_TO_PARENT, -0.33f,
						TranslateAnimation.RELATIVE_TO_PARENT, 0.0f, 0, 0.0f,
						0, 0.0f);
				// if you want left to right just remove ( - ) before 0.33f
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();

			}
		});

		// Slide the Panel
		menuViewButton = (ImageView) findViewById(R.id.menuViewButton);
		menuViewButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!isExpanded) {
					isExpanded = true;
					menuPanel.setVisibility(View.VISIBLE);
					// Expand
					new ExpandAnimation(slidingPanel, panelWidth,
							Animation.RELATIVE_TO_PARENT, 0.0f,
							Animation.RELATIVE_TO_PARENT, -0.33f, 0, 0.0f, 0,
							0.0f);

					// if you want left to right just remove ( - ) before 0.33f
				} else {
					isExpanded = false;
					menuPanel.setVisibility(View.GONE);
					// Collapse
					new CollapseAnimation(slidingPanel, panelWidth,
							TranslateAnimation.RELATIVE_TO_PARENT, -0.33f,
							TranslateAnimation.RELATIVE_TO_PARENT, 0.0f, 0,
							0.0f, 0, 0.0f);
					// if you want left to right just remove ( - ) before 0.33f

				}
			}
		});
	}

	@Override
	public void onBackPressed() {

		Log.d("CDA", "onBackPressed Called");
		/*
		 * Intent setIntent = new Intent(Intent.ACTION_MAIN);
		 * setIntent.addCategory(Intent.CATEGORY_HOME);
		 * setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * startActivity(setIntent);
		 */
		if (isExpanded) {
			isExpanded = false;
			menuPanel.setVisibility(View.GONE);
			// Collapse
			new CollapseAnimation(slidingPanel, panelWidth,
					TranslateAnimation.RELATIVE_TO_PARENT, -0.33f,
					TranslateAnimation.RELATIVE_TO_PARENT, 0.0f, 0, 0.0f, 0,
					0.0f);
			// if you want left to right just remove ( - ) before 0.33f
		} else {
			super.onBackPressed();
		}
	}
}
