package com.mxo2.votingapp;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mxo2.votingapp.dialogs.AlertDialog;
import com.mxo2.votingapp.dialogs.AlertDialog.DialogOptionSelectionListener;
import com.mxo2.votingapp.dialogs.Progress_Dialog;
import com.mxo2.votingapp.pojo.Party_pojo;
import com.mxo2.votingapp.slidingmenu.CollapseAnimation;
import com.mxo2.votingapp.slidingmenu.ExpandAnimation;
import com.mxo2.votingapp.utils.AppConstants;
import com.mxo2.votingapp.utils.AppLocationService;
import com.mxo2.votingapp.utils.AppUtils;
import com.mxo2.votingapp.utils.GetReverseGeoCoding;
import com.mxo2.votingapp.utils.LazyImageLoadAdapter;
import com.mxo2.votingapp.utils.LazyImageLoadAdapter.ListItemSelectionListener;
import com.mxo2.votingapp.utils.ParentActivity;
import com.mxo2.votingapp.webservice.WebserviceClient;

public class PartiesList extends ParentActivity implements
		ListItemSelectionListener, DialogOptionSelectionListener {

	private AppUtils m_AppUtils = null;
	private LazyImageLoadAdapter list_adapter = null;
	private Party_pojo selectedParty = null;
	private AlertDialog dialogObj = null;
	private ArrayList<Party_pojo> electionPartyList = null;
	private Resources appResource = null;
	private ListView listParties;
	private TextView txt_noData;// , txt_info;
	private int electionId = -1;
	private int constId = -1;
	private String inputMobile = "";
	private Button castVoteButton;
	private static int positions;
	AppLocationService appLocationService;
	private double latitude;
	private double longitude;
	private static boolean hasLocation = false;
	private static boolean isPartySelected = false;
	private String Address1 = "", Address2 = "", City = "", State = "",
			Country = "", County = "", PIN = "";
	private static boolean isVotingState = false;

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

		setContentView(R.layout.parties_list);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		m_AppUtils = new AppUtils(PartiesList.this);
		dialogObj = new AlertDialog(PartiesList.this);
		appResource = PartiesList.this.getResources();

		// txt_info = (TextView) findViewById(R.id.info_txt);
		txt_noData = (TextView) findViewById(R.id.noData_txt);
		listParties = (ListView) findViewById(R.id.list_main);
		castVoteButton = (Button) findViewById(R.id.castVoteButton);
		if (getIntent() != null) {
			if (getIntent().hasExtra("electionId")) {
				electionId = getIntent().getIntExtra("electionId", -1);
			}
			if (getIntent().hasExtra("constId")) {
				constId = getIntent().getIntExtra("constId", -1);
			}
		}
		new FetchListTask(PartiesList.this, "getList").execute(
				String.valueOf(electionId), String.valueOf(constId));

		listParties.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long id) {
				// TODO Auto-generated method stub
				positions = pos;
				Log.v("long clicked", "pos: " + pos);
				// onListItemSelected(pos);
				return true;
			}
		});

		listParties.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				view.setSelected(true);
				isPartySelected = true;
				positions = position;
			}
		});

		castVoteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isPartySelected) {
					if (hasLocation) {
						if (isVotingState) {
							String imei = getIMEI();
							if (imei != null && imei.length() > 0) {
								// onListItemSelected(positions);

								String[] inputArray = new String[3];
								inputArray[0] = "action_verification";
								inputArray[1] = imei;
								inputArray[2] = String.valueOf(positions);
								DialogOptionSelectionListener m_Callback = (DialogOptionSelectionListener) PartiesList.this;

								OnDialogOptionSelected(inputArray);
							}else{
								getIMEI();
								Toast.makeText(PartiesList.this,
										"Please Retry",
										Toast.LENGTH_SHORT).show();
							}
						} else {

							dialogObj.votingPreventionDialog(appResource
									.getString(R.string.voting_prevented_state));
						}
					}else{
						getLocation();
					}
				} else {
					Toast.makeText(PartiesList.this,
							"Please Select Party to cast vote",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		initMenu("Vote", "Now");
		getIMEI();
		getLocation();
	}

	// Get Location of user
	public void getLocation() {
		appLocationService = new AppLocationService(PartiesList.this);
		Location gpsLocation = appLocationService
				.getLocation(LocationManager.GPS_PROVIDER);
		Location nwLocation = appLocationService
				.getLocation(LocationManager.NETWORK_PROVIDER);

		if (gpsLocation != null) {
			latitude = gpsLocation.getLatitude();
			longitude = gpsLocation.getLongitude();
			Log.i("GPS Location is", "Mobile Location (GPS): \nLatitude: "
					+ latitude + "\nLongitude: " + longitude);
			hasLocation = true;
			GetReverseGeoCoding address = new GetReverseGeoCoding();
			address.getAddress(latitude, longitude);
			Log.i("Geolocation address", "city " + address.getCity()
					+ " Address1 :" + address.getAddress1() + " Address2:"
					+ address.getAddress2());
			Log.i("Geolocation address",
					"State " + address.getState() + " Country :"
							+ address.getCountry() + " PIN:" + address.getPIN());

			Address1 = address.getAddress1();
			Address2 = address.getAddress2();
			State = address.getState();
			City = address.getCity();
			Country = address.getCountry();
			PIN = address.getPIN();

			/*
			 * if(State !=null && !TextUtils.isEmpty(State)){
			 * if(State.equalsIgnoreCase
			 * ("Delhi")||State.equalsIgnoreCase("New Delhi")){ isVotingState =
			 * true; }else{ isVotingState =false; } }
			 */
			if (PIN != null && !TextUtils.isEmpty(PIN)) {
				if (PIN.contains("1100")) {
					isVotingState = true;
				} else {
					isVotingState = false;
				}
			} else {
				isVotingState = false;
			}
			/*
			 * if(City !=null && !TextUtils.isEmpty(City)){
			 * if(State.equalsIgnoreCase
			 * ("Delhi")||State.equalsIgnoreCase("New Delhi")){ isVotingState =
			 * true; }else{ isVotingState =false; } }
			 */

		
		} else if (nwLocation != null) {
			latitude = nwLocation.getLatitude();
			longitude = nwLocation.getLongitude();
			Log.i("Network Location is", "Mobile Location (NW): \nLatitude: "
					+ latitude + "\nLongitude: " + longitude);

			GetReverseGeoCoding address = new GetReverseGeoCoding();
			address.getAddress(latitude, longitude);
			Log.i("Geolocation address", "city " + address.getCity()
					+ " Address1 :" + address.getAddress1() + " Address2:"
					+ address.getAddress2());
			Log.i("Geolocation address",
					"State " + address.getState() + " Country :"
							+ address.getCountry() + " PIN:" + address.getPIN());

			Address1 = address.getAddress1();
			Address2 = address.getAddress2();
			State = address.getState();
			City = address.getCity();
			Country = address.getCountry();
			PIN = address.getPIN();

			/*
			 * if(State !=null && !TextUtils.isEmpty(State)){
			 * if(State.equalsIgnoreCase
			 * ("Delhi")||State.equalsIgnoreCase("New Delhi")){ isVotingState =
			 * true; }else{ isVotingState =false; } }
			 */
			if (PIN != null && !TextUtils.isEmpty(PIN)) {
				if (PIN.contains("1100")) {
					isVotingState = true;
				} else {
					isVotingState = false;
				}
			} else {
				isVotingState = false;
			}
			/*
			 * if(City !=null && !TextUtils.isEmpty(City)){
			 * if(State.equalsIgnoreCase
			 * ("Delhi")||State.equalsIgnoreCase("New Delhi")){ isVotingState =
			 * true; }else{ isVotingState =false; } }
			 */

			hasLocation = true;
		} else {
			showSettingsAlert("GPS");
			hasLocation = false;
		}
	}

	public String getIMEI() {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String IMEINumber = tm.getDeviceId();
		Log.i("IMEI Number is", IMEINumber);
		return IMEINumber;
	}

	public boolean setPartyListData(PartiesList task_activity,
			ArrayList<Party_pojo> partyList) {

		if (partyList == null) {
			showNoDataUI(task_activity);
		} else if (partyList.size() == 0) {
			showNoDataUI(task_activity);
		} else {
			electionPartyList = partyList;
			ArrayList<String> name_array = new ArrayList<String>();
			ArrayList<String> abbr_array = new ArrayList<String>();
			ArrayList<String> symbol_array = new ArrayList<String>();
			name_array.clear();
			abbr_array.clear();
			symbol_array.clear();

			for (int index = 0; index < partyList.size(); index++) {
				if (!TextUtils.isEmpty(partyList.get(index).getName())) {
					name_array.add(partyList.get(index).getName());
				} else {
					name_array.add("");
				}
				if (!TextUtils.isEmpty(partyList.get(index).getAbbreviation())) {
					abbr_array.add(partyList.get(index).getAbbreviation());
				} else {
					abbr_array.add("");
				}

				if (!TextUtils.isEmpty(partyList.get(index).getSymbol())) {
					symbol_array.add(AppConstants.app_url
							+ partyList.get(index).getSymbol());
				} else {
					symbol_array.add("");
				}

			}

			if (!name_array.isEmpty()) {
				listParties.setAdapter(m_AppUtils.clearListView(task_activity));
				list_adapter = new LazyImageLoadAdapter(task_activity,
						abbr_array, name_array, symbol_array,
						R.layout.rowlayout_parties);
				listParties.setAdapter(list_adapter);
				// txt_info.setVisibility(View.VISIBLE);
				listParties.setVisibility(View.VISIBLE);
				txt_noData.setVisibility(View.GONE);
			} else {
				showNoDataUI(task_activity);
			}
		}
		return true;

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
		// PartiesList.this.finish();

	}

	@Override
	public void onListItemSelected(int position) {

		selectedParty = null;
		selectedParty = electionPartyList.get(position);
		if (TextUtils.isEmpty(m_AppUtils.sharedPreferences.getString(
				AppConstants.ActionKeys.verification_code, ""))) {
			dialogObj.inputActionDialog(appResource
					.getString(R.string.verification_info));
		} else {
			dialogObj.votingPreventionDialog(appResource
					.getString(R.string.voting_prevented));
		}

	}

	@Override
	public void OnDialogOptionSelected(String[] input) {

		if (input != null) {
			if (input[0] != null) {
				if (input[0].equalsIgnoreCase("action_verification")) {
					selectedParty = null;
					int position = Integer.parseInt(input[2]);
					selectedParty = electionPartyList.get(position);
					inputMobile = input[1];
					dialogObj.actionDialog(appResource.getString(
							R.string.voting_confirmation,
							selectedParty.getAbbreviation()),
							AppConstants.app_url + selectedParty.getSymbol());
				} else if (input[0].equalsIgnoreCase("action_castVote")) {
					new FetchListTask(PartiesList.this, "castVote").execute(
							String.valueOf(electionId),
							String.valueOf(constId), selectedParty.getId(),
							inputMobile);
				}
			}
		}

	}

	public String generatedUniqueVerificationCode(String mobile_number) {

		String verificationCode = "";
		Time t = new Time();
		t.setToNow();
		Random mRandom = new Random(t.toMillis(false));
		int randomNumber = mRandom.nextInt(10000);
		if (!TextUtils.isEmpty(mobile_number)) {
			String first4Characters = mobile_number.substring(0,
					Math.min(mobile_number.length(), 4));
			String last4Characters = String.valueOf(randomNumber);
			verificationCode = new StringBuilder(first4Characters
					+ last4Characters).reverse().toString();
		}
		Log.v("verificationCode", verificationCode);
		return verificationCode;
	}

	private void showNoDataUI(PartiesList task_activity) {

		listParties.setVisibility(View.GONE);
		// txt_info.setVisibility(View.GONE);
		txt_noData.setVisibility(View.VISIBLE);
		txt_noData.setText(task_activity.getResources().getString(
				R.string.no_party));

	}

	private class FetchListTask extends AsyncTask<String, Void, Boolean> {

		private PartiesList task_activity = null;
		private Progress_Dialog m_ProgressDialog = null;
		private AlertDialog dialogObj = null;
		private ArrayList<Party_pojo> partyList = null;
		private AppUtils _appUtils = null;
		private String taskType;
		private String message;

		public FetchListTask(PartiesList _activity, String _type) {
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
			if (taskType.equalsIgnoreCase("getList")) {
				WebserviceClient wsClient = new WebserviceClient(_appUtils);
				partyList = wsClient.fetchPartyList(Integer.valueOf(params[0]),
						Integer.valueOf(params[1]));
				isSuccessful = true;
			} else if (taskType.equalsIgnoreCase("castVote")) {
				WebserviceClient wsClient = new WebserviceClient(_appUtils);
				// isSuccessful = wsClient.castVote(params[0],
				// params[1],params[2], params[3]);
				String response[] = wsClient.castVote(params[0], params[1],
						params[2], params[3]);
				message = "";

				if (response != null) {
					if (response[0] != null && !TextUtils.isEmpty(response[0])) {
						if (response[1] != null
								&& !TextUtils.isEmpty(response[1])) {
							message = response[1];
							if (response[0].equalsIgnoreCase("Invalid")
									|| (response[0].equalsIgnoreCase("Error"))) {
								isSuccessful = false;
							} else if (response[0].equalsIgnoreCase("Success")) {
								isSuccessful = true;
							}
						} else {
							isSuccessful = false;
						}
					} else {
						isSuccessful = false;
					}
				} else {
					isSuccessful = false;
				}
				if (isSuccessful) {
					_appUtils.settings_editor.putString(
							AppConstants.ActionKeys.verification_code,
							generatedUniqueVerificationCode(params[3]));
					_appUtils.settings_editor.commit();
				}
			}
			return isSuccessful;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (taskType.equalsIgnoreCase("getList")) {
				if (setPartyListData(task_activity, partyList)
						&& (m_ProgressDialog != null)) {
					m_ProgressDialog.dismissProgressDialog();
				}
			} else if (taskType.equalsIgnoreCase("castVote")) {
				if (m_ProgressDialog != null) {
					m_ProgressDialog.dismissProgressDialog();
				}
				if (result) {
					dialogObj.inputMissingDialog(task_activity.getResources()
							.getString(R.string.voting_success));
				} else {

					if (message.equalsIgnoreCase("You are already voted.")) {
						dialogObj.votingPreventionDialog(appResource
								.getString(R.string.voting_prevented));
					} else {
						dialogObj.inputMissingDialog(task_activity
								.getResources().getString(
										R.string.voting_failed));
					}
				}
			}
		}

	}

	public void showSettingsAlert(String provider) {
		android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(
				PartiesList.this);

		alertDialog.setTitle(provider + " SETTINGS");

		alertDialog.setMessage(provider
				+ " is not enabled! Want to go to settings menu?");

		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						PartiesList.this.startActivity(intent);
					}
				});

		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						// showSettingsAlert("GPS");
					}
				});

		alertDialog.show();
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
				Intent previsousIntent = new Intent(PartiesList.this,
						ElectionConstituencyList.class);
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
				textitem2.setBackgroundColor((getResources()
						.getColor(R.color.blue)));
				// Collapse
				new CollapseAnimation(slidingPanel, panelWidth,
						TranslateAnimation.RELATIVE_TO_PARENT, -0.33f,
						TranslateAnimation.RELATIVE_TO_PARENT, 0.0f, 0, 0.0f,
						0, 0.0f);
				// if you want left to right just remove ( - ) before 0.33f

				Intent item1 = new Intent(PartiesList.this, HomeScreen.class);
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

				Intent item2 = new Intent(PartiesList.this, ElectionList.class);
				item2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(item2);
				finish();
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

				Intent item1 = new Intent(PartiesList.this,
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

				Intent item1 = new Intent(PartiesList.this, OpinionPolling.class);
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

				Intent item1 = new Intent(PartiesList.this, AboutUs.class);
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

}
