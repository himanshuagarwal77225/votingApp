package com.mxo2.votingapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mxo2.votingapp.dialogs.Progress_Dialog;
import com.mxo2.votingapp.pojo.Election_pojo;
import com.mxo2.votingapp.slidingmenu.CollapseAnimation;
import com.mxo2.votingapp.slidingmenu.ExpandAnimation;
import com.mxo2.votingapp.utils.AppConstants;
import com.mxo2.votingapp.utils.AppUtils;
import com.mxo2.votingapp.utils.LazyImageLoadAdapter;
import com.mxo2.votingapp.utils.Log;
import com.mxo2.votingapp.utils.ParentActivity;
import com.mxo2.votingapp.utils.LazyImageLoadAdapter.ListItemSelectionListener;
import com.mxo2.votingapp.webservice.WebserviceClient;
import com.mxo2.votingapp.R;

public class ElectionList extends ParentActivity implements
		ListItemSelectionListener {

	private ListView listView = null;
	private TextView txt_title, txt_noData;
	private ArrayList<Election_pojo> election_list = null;

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

		setContentView(R.layout.election_list);
		listView = (ListView) findViewById(R.id.list_main);
		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_noData = (TextView) findViewById(R.id.noData_txt);
		listView.setOnItemClickListener(itemClickListener);
		txt_title.setText(ElectionList.this.getResources().getString(
				R.string.forthcoming)
				+ " "
				+ ElectionList.this.getResources()
						.getString(R.string.elections));
		new FetchListTask(ElectionList.this).execute();

		initMenu(
				ElectionList.this.getResources()
						.getString(R.string.forthcoming), ElectionList.this
						.getResources().getString(R.string.elections));
	}

	public boolean setElectionList(ArrayList<Election_pojo> election_list,
			ElectionList task_activity) {

		try {
			if (election_list == null) {
				showNoDataUI(task_activity);
			} else if (election_list.size() == 0) {
				showNoDataUI(task_activity);
			} else {
				this.election_list = election_list;
				ArrayList<String> name_array = new ArrayList<String>();
				ArrayList<String> date_array = new ArrayList<String>();
				ArrayList<String> logo_array = new ArrayList<String>();
				name_array.clear();
				date_array.clear();
				logo_array.clear();

				for (int index = 0; index < election_list.size(); index++) {
					if (!TextUtils.isEmpty(election_list.get(index).getName())) {
						name_array.add(election_list.get(index).getName());
					} else {
						name_array.add("");
					}
					String ele_date = election_list.get(index)
							.getElection_date();
					if (!TextUtils.isEmpty(ele_date)) {
						DateFormat formatter = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						Date date = (Date) formatter.parse(ele_date);
						String convertedDate = (String) android.text.format.DateFormat
								.format("MMM dd, yyyy", date);
						date_array.add(convertedDate);
					} else {
						date_array.add("");
					}
					if (!TextUtils.isEmpty(election_list.get(index)
							.getElection_date())) {
						date_array.add(election_list.get(index)
								.getElection_date());
					} else {
						date_array.add("");
					}
					if (!TextUtils.isEmpty(election_list.get(index)
							.getDescription())) {
						logo_array.add(AppConstants.app_url
								+ election_list.get(index).getDescription());
					} else {
						logo_array.add("");
					}
				}
				if (!name_array.isEmpty()) {
					listView.setAdapter((new AppUtils(task_activity))
							.clearListView(task_activity));
					LazyImageLoadAdapter list_adapter = new LazyImageLoadAdapter(
							task_activity, name_array, date_array, logo_array,
							R.layout.rowlayout_election);
					listView.setAdapter(list_adapter);
					listView.setVisibility(View.VISIBLE);
					txt_noData.setVisibility(View.GONE);
				} else {
					showNoDataUI(task_activity);
				}
			}
		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
		}
		return true;
	}

	@Override
	public void onListItemSelected(int position) {

	}

	private void showNoDataUI(ElectionList task_activity) {

		listView.setVisibility(View.GONE);
		txt_noData.setVisibility(View.VISIBLE);
		txt_noData.setText(task_activity.getResources().getString(
				R.string.no_data));

	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (isExpanded) {
				isExpanded = false;

				// Collapse
				new CollapseAnimation(slidingPanel, panelWidth,
						TranslateAnimation.RELATIVE_TO_PARENT, -0.33f,
						TranslateAnimation.RELATIVE_TO_PARENT, 0.0f, 0, 0.0f,
						0, 0.0f);
				// if you want left to right just remove ( - ) before 0.33f
			}
			Election_pojo selElection = election_list.get(position);
			Intent _intent = new Intent(ElectionList.this,
					ElectionConstituencyList.class);
			_intent.putExtra("electionId", selElection.getId());
			ElectionList.this.startActivity(_intent);

		}
	};

	private class FetchListTask extends AsyncTask<Void, Void, Boolean> {

		private ElectionList task_activity = null;
		private Progress_Dialog m_ProgressDialog = null;
		private ArrayList<Election_pojo> election_list = null;

		public FetchListTask(ElectionList _activity) {
			task_activity = _activity;
			m_ProgressDialog = new Progress_Dialog(task_activity);
		}

		@Override
		protected void onPreExecute() {
			if (m_ProgressDialog != null) {
				m_ProgressDialog.showProgressDialog();
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			WebserviceClient wsClient = new WebserviceClient(new AppUtils(
					task_activity));
			election_list = wsClient.fetchElectionList();
			if (election_list == null) {
				return false;
			} else if (election_list.size() == 0) {
				return false;
			} else {
				return true;
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (setElectionList(election_list, task_activity)
					&& (m_ProgressDialog != null)) {
				m_ProgressDialog.dismissProgressDialog();
			}
		}

	}

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

		textitem2.setBackgroundColor((getResources().getColor(R.color.blue)));
		backIconButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent previsousIntent = new Intent(ElectionList.this,
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

				Intent item1 = new Intent(ElectionList.this, HomeScreen.class);
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

				// Intent item2 = new Intent(ElectionList.this,
				// ElectionList.class);
				// startActivity(item2);

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

				Intent item1 = new Intent(ElectionList.this,
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

				Intent item1 = new Intent(ElectionList.this, OpinionPolling.class);
				item1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(item1);
				finish();

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

				Intent item1 = new Intent(ElectionList.this, AboutUs.class);
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
