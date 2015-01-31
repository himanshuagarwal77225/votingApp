package com.mxo2.votingapp.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.ArrayAdapter;

import com.mxo2.votingapp.MainAppClass;
import com.mxo2.votingapp.R;
import com.mxo2.votingapp.db.DatabaseHelper;
import com.mxo2.votingapp.db.DatabaseSchema;
import com.mxo2.votingapp.pojo.Constituency_pojo;
import com.mxo2.votingapp.pojo.District_pojo;
import com.mxo2.votingapp.webservice.WebserviceClient;

/**
 * All common and repeatedly used methods have been placed in this class, to
 * access them easily. Many methods are static over here.
 **/

public class AppUtils {

	private Context m_Context = null;
	public SharedPreferences sharedPreferences = null;
	public Editor settings_editor = null;
	private WebserviceClient m_WSclient = null;

	public AppUtils(Context _context) {
		m_Context = _context;
		sharedPreferences = MainAppClass.getSharedPref();
		settings_editor = sharedPreferences.edit();
		m_WSclient = new WebserviceClient(AppUtils.this);
	}

	/**
	 * returns the current timestamp of the device
	 **/
	public static long currentTimeSecs() {
		return (System.currentTimeMillis() / 1000);
	}

	/**
	 * verifies if Internet is available or not
	 **/
	public boolean isNetworkConnected() {
		ConnectivityManager connectivitymanager = (ConnectivityManager) m_Context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isConnectedOrConnecting()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * verifies if SD card is available or not
	 **/
	public static boolean isSdPresent() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_SHARED)
				|| Environment.getExternalStorageState().equals(
						Environment.MEDIA_CHECKING)
				|| Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED);
	}

	/**
	 * create default directories for MindGym application in the SD card
	 **/
	public boolean makeDirectory(String directory_name) {
		boolean dirCreated = false;
		if (isSdPresent()) {
			File f = new File(directory_name);
			try {
				if (f.exists()) {
					if (!f.isDirectory()) {
						f.mkdirs();
						Log.v("Directory ==> ", "Directory created");
					}
					dirCreated = true;
				} else {
					f.mkdirs();
					dirCreated = true;
					Log.v("Directory ==> ", "Directory created");
				}
				CreateDirectoryForPictures();
				CreateDirectoryForFiles();
			} catch (Exception e) {
				Log.e("Make directory exception ==> ", e.toString());
			}
		} else {
			Log.v("SD Card ==> ", "SD Card not found");
		}
		return dirCreated;
	}

	/**
	 * create default directory for iVote application in the SD card for storing
	 * image files
	 **/
	public void CreateDirectoryForPictures() {
		File f = new File(AppConstants.APP_Images_directory);
		try {
			if (f.exists()) {
				if (!f.isDirectory()) {
					f.mkdirs();
					Log.v("Directory ==> ", "Directory created");
				}
			} else {
				f.mkdirs();
				Log.v("Directory ==> ", "Directory created");
			}
		} catch (Exception e) {
			Log.e("Make directory exception ==> ", e.toString());
		}
	}

	/**
	 * create default directory for iVote application in the SD card for storing
	 * files
	 **/
	public void CreateDirectoryForFiles() {
		File f = new File(AppConstants.APP_Files_directory);
		try {
			if (f.exists()) {
				if (!f.isDirectory()) {
					f.mkdirs();
					Log.v("Directory ==> ", "Directory created");
				}
			} else {
				f.mkdirs();
				Log.v("Directory ==> ", "Directory created");
			}
		} catch (Exception e) {
			Log.e("Make directory exception ==> ", e.toString());
		}
	}

	public static Calendar getCalendarFromDateWithCustomFormat(String date,
			String currentFormat, String requiredFormat) {

		Calendar MCalendar = null;
		String output = null;
		SimpleDateFormat currentSimpleDateFormat = null, requiredSimpleDateFormat = null;

		try {
			currentSimpleDateFormat = new SimpleDateFormat(currentFormat);
			requiredSimpleDateFormat = new SimpleDateFormat(requiredFormat);
			output = requiredSimpleDateFormat.format(currentSimpleDateFormat
					.parse(date));
			MCalendar = Calendar.getInstance();
			MCalendar.setTime(requiredSimpleDateFormat.parse(output));
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
			return null;
		}

		return MCalendar;
	}

	public static boolean isValidTodayOrFutureDate(String date, String format) {
		SimpleDateFormat simpleDateFormat = null;
		Date SelectedDate = null, CurrentDate = null;
		try {
			simpleDateFormat = new SimpleDateFormat(format);
			SelectedDate = simpleDateFormat.parse(date);
			CurrentDate = new Date(System.currentTimeMillis());
			if ((SelectedDate != null) && (CurrentDate != null)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
			return false;
		}
	}

	public static int compareDates(String start_date, String end_date) {

		/**
		 * result = -1 ==> start_date is older than end_date result = 0 ==>
		 * start_date is equal to end_date result = 1 ==> start_date is later
		 * than end_date
		 * 
		 * **/
		try {
			if (!TextUtils.isEmpty(start_date) && !TextUtils.isEmpty(end_date)) {
				DateFormat formatter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date startDate = (Date) formatter.parse(start_date);
				Date endDate = (Date) formatter.parse(end_date);
				if ((startDate != null) && (endDate != null)) {
					return startDate.compareTo(endDate);
				}
			}
		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
		}
		return -2;
	}

	public static int getdeviceType(Context context) {

		int currentDevice = -1;

		if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_SMALL
				&& (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) < Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			currentDevice = AppConstants.SMALL_DEVICE;
		} else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_NORMAL
				&& (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) < Configuration.SCREENLAYOUT_SIZE_LARGE) {
			currentDevice = AppConstants.NORMAL_DEVICE;
		} else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
				&& (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) < Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			currentDevice = AppConstants.LARGE_DEVICE;
		} else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			currentDevice = AppConstants.XLARGE_DEVICE;
		}

		return currentDevice;

	}

	public ArrayAdapter<String> clearSpinner(Context _context) {
		ArrayList<String> emptyList = new ArrayList<String>();
		emptyList.clear();
		ArrayAdapter<String> nullAdapter = new ArrayAdapter<String>(_context,
				R.layout.spinnerrow, R.id.item, emptyList);
		return nullAdapter;
	}

	public ArrayAdapter<String> clearListView(Context _context) {
		ArrayList<String> emptyList = new ArrayList<String>();
		emptyList.clear();
		ArrayAdapter<String> nullAdapter = new ArrayAdapter<String>(_context,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				emptyList);
		return nullAdapter;
	}

	public static Bitmap setPic(String currentPhotoPath, int targetHeight,
			int targetWidth) {

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetWidth, photoH / targetHeight);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
		return bitmap;

	}

	/** copies data from input to output **/
	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {

			byte[] bytes = new byte[buffer_size];
			for (;;) {
				// Read byte from input stream
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				// Write byte from output stream
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
			Log.v("Exception --> ", ex.toString());
		}
	}

	/** Convert input file to byte array **/
	public byte[] readBytesFromFile(Context _context, File attachment_file) {

		byte[] bytes = null;
		InputStream is;
		try {
			is = new FileInputStream(attachment_file);
			long length = attachment_file.length();
			if (length > Integer.MAX_VALUE) {
				Log.e("IO_Exception", "Could not completely read file "
						+ attachment_file.getName() + " as it is too long ("
						+ length + " bytes, max supported " + Integer.MAX_VALUE
						+ ")");
				is.close();
				return null;
			}
			bytes = new byte[(int) length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			if (offset < bytes.length) {
				Log.e("IO_Exception", "Could not completely read file "
						+ attachment_file.getName());
			}
			is.close();
		} catch (Exception e) {
			Log.e("Exception", e.getMessage());
		}
		return bytes;
	}

	/** write input byte array to file **/
	public static void writeBytesToFile(File out_File, byte[] bytes) {
		BufferedOutputStream bos = null;
		try {
			FileOutputStream fos = new FileOutputStream(out_File);
			bos = new BufferedOutputStream(fos);
			bos.write(bytes);
		} catch (Exception e) {
			Log.e("IO_Exception", e.getMessage());
		} finally {
			if (bos != null) {
				try {
					bos.flush();
					bos.close();
				} catch (Exception e) {
					Log.e("Exception", e.getMessage());
				}
			}
		}
	}

	public static String getJSONParam(
			ArrayList<NameValuePair> ParamsNameValuePair) {
		String jsonParam = null;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject();
			for (NameValuePair element : ParamsNameValuePair) {
				if (element.getValue() == null)
					jsonObject.put(element.getName(), JSONObject.NULL);
				else if (element.getValue().indexOf("{") == 0
						&& element.getValue().lastIndexOf("}") == element
								.getValue().length() - 1) {
					jsonObject.put(element.getName(),
							(new JSONObject(element.getValue())));
					return element.getValue();
				} else
					jsonObject.put(element.getName(), element.getValue());
				element = null;
			}
			jsonParam = jsonObject.toString();
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
		}
		return jsonParam;
	}

	public int getScreenResolution() {
		int density = m_Context.getResources().getDisplayMetrics().densityDpi;
		return density;
	}

	/** fetch List of all Districts for the state from server **/
	public ArrayList<District_pojo> fetchDistrictListFromServer(int stateId) {

		ArrayList<District_pojo> districtListArray = null;
		try {
			JSONArray districtJsonArray = fetchDistrictJsonArrayFromServer(stateId);
			Log.v("District array ==> ", districtJsonArray.toString());
			if (districtJsonArray != null) {
				districtListArray = new ArrayList<District_pojo>();
				for (int index = 0; index < districtJsonArray.length(); index++) {
					JSONObject districtObj = districtJsonArray
							.optJSONObject(index);
					District_pojo districtItem = new District_pojo(
							districtObj
									.optString(DatabaseSchema.DISTRICT_MASTER.ID),
							districtObj
									.optString(DatabaseSchema.DISTRICT_MASTER.NAME),
							"");
					districtListArray.add(districtItem);
				}
				if (districtListArray != null) {
					if (districtListArray.size() > 0) {
						saveDistrictListInLocalDatabase(districtListArray);
					}
				}
			}
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
			return null;
		}
		return districtListArray;
	}

	/**
	 * Fetch list of districts from local database in JSON Array format. If
	 * there are no records in the database then hit web service to fetch from
	 * server
	 **/
	public JSONArray getDistrictList(int stateId) {

		JSONArray districtJsonArray = null;
		try {
			JSONObject districtJsonObject = null;
			String[] projection = new String[] {
					DatabaseSchema.DISTRICT_MASTER.ID,
					DatabaseSchema.DISTRICT_MASTER.NAME };
			String selWhere = DatabaseSchema.DISTRICT_MASTER.STATE_ID + " = ?";
			String[] selArguments = new String[] { String.valueOf(stateId) };
			districtJsonObject = new JSONObject(getItemListFromDatabase(
					AppConstants.DISTRICT,
					DatabaseSchema.DISTRICT_MASTER.TABLE_NAME, selWhere,
					selArguments, projection));
			if (districtJsonObject != null) {
				if (districtJsonObject.has("result")) {
					if ((!districtJsonObject.optString("result")
							.equalsIgnoreCase("success"))) {
						districtJsonArray = fetchDistrictJsonArrayFromServer(stateId);
					} else {
						if (districtJsonObject.has("msg")) {
							districtJsonArray = districtJsonObject
									.optJSONArray("msg");
							if (districtJsonArray == null) {
								return null;
							} else if (districtJsonArray.length() == 0) {
								return null;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
			return null;
		}
		return districtJsonArray;
	}

	/** fetch List of Districts from server in JSON format **/
	private JSONArray fetchDistrictJsonArrayFromServer(int stateId) {

		JSONArray ditrictJA = null;
		try {
			if (!MainAppClass.isOfflineModeOn && isNetworkConnected()) {
				ditrictJA = m_WSclient.fetchElectionDistrictList(stateId);
			}
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
			return null;
		}
		return ditrictJA;
	}

	/**
	 * save latest District list fetched from server in local database
	 * 
	 * @param districtListArray
	 **/
	public void saveDistrictListInLocalDatabase(
			ArrayList<District_pojo> districtListArray) {

		DatabaseHelper dbHelper = null;
		try {
			dbHelper = new DatabaseHelper(m_Context);
			if (districtListArray != null) {
				dbHelper.open();
				for (int index = 0; index < districtListArray.size(); index++) {
					ContentValues values = new ContentValues();
					values.put(DatabaseSchema.DISTRICT_MASTER.ID, Integer
							.valueOf(districtListArray.get(index).getId()));
					values.put(DatabaseSchema.DISTRICT_MASTER.NAME,
							districtListArray.get(index).getName());
					values.put(DatabaseSchema.DISTRICT_MASTER.STATE_ID,
							Integer.valueOf(districtListArray.get(index)
									.getState_id()));
					dbHelper.addDetailInDb(
							DatabaseSchema.DISTRICT_MASTER.TABLE_NAME, values,
							DatabaseSchema.DISTRICT_MASTER.ID);
					values = null;
				}
			}
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
		} finally {
			if (dbHelper != null)
				dbHelper.close();
		}
	}

	/**
	 * Fetch list of constituency from local database in JSON Array format. If
	 * there are no records in the database then hit web service to fetch from
	 * server
	 **/
	public JSONArray getConstituencyList(int stateId, int district_id) {

		JSONArray constListJsonArray = null;
		try {
			JSONObject constJsonObject = null;
			String[] projection = new String[] {
					DatabaseSchema.CONSTITUENCY_MASTER.ID,
					DatabaseSchema.CONSTITUENCY_MASTER.NAME };
			String selWhere = DatabaseSchema.CONSTITUENCY_MASTER.STATE_ID
					+ " = ? AND "
					+ DatabaseSchema.CONSTITUENCY_MASTER.DISTRICT_ID + " = ?";
			String[] selArguments = new String[] { String.valueOf(stateId),
					String.valueOf(district_id) };
			constJsonObject = new JSONObject(getItemListFromDatabase(
					AppConstants.CONSTITUENCY,
					DatabaseSchema.CONSTITUENCY_MASTER.TABLE_NAME, selWhere,
					selArguments, projection));
			if (constJsonObject != null) {
				if (constJsonObject.has("result")) {
					if ((!constJsonObject.optString("result").equalsIgnoreCase(
							"success"))) {
						constListJsonArray = fetchConstituencyJsonArrayFromServer(
								stateId, district_id);
					} else {
						if (constJsonObject.has("msg")) {
							constListJsonArray = constJsonObject
									.optJSONArray("msg");
							if (constListJsonArray == null) {
								return null;
							} else if (constListJsonArray.length() == 0) {
								return null;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
			return null;
		}
		return constListJsonArray;
	}

	/** fetch List of Constituency from server in JSON format **/
	private JSONArray fetchConstituencyJsonArrayFromServer(int stateId,
			int district_id) {

		JSONArray constituencyJA = null;
		try {
			if (!MainAppClass.isOfflineModeOn && isNetworkConnected()) {
				// constituencyJA =
				// m_WSclient.fetchElectionConstituencyList(stateId,
				// district_id);
			}
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
			return null;
		}
		return constituencyJA;
	}

	/**
	 * save latest Constituency list fetched from server in local database
	 **/
	public void saveConstituencyListInLocalDatabase(
			ArrayList<Constituency_pojo> constListArray) {

		DatabaseHelper dbHelper = null;
		try {
			dbHelper = new DatabaseHelper(m_Context);
			if (constListArray != null) {
				dbHelper.open();
				for (int index = 0; index < constListArray.size(); index++) {
					ContentValues values = new ContentValues();
					values.put(DatabaseSchema.CONSTITUENCY_MASTER.ID,
							Integer.valueOf(constListArray.get(index).getId()));
					values.put(DatabaseSchema.CONSTITUENCY_MASTER.NAME,
							constListArray.get(index).getName());
					values.put(DatabaseSchema.CONSTITUENCY_MASTER.STATE_ID,
							Integer.valueOf(constListArray.get(index)
									.getState_id()));
					values.put(DatabaseSchema.CONSTITUENCY_MASTER.DISTRICT_ID,
							Integer.valueOf(constListArray.get(index)
									.getDistrict_id()));
					dbHelper.addDetailInDb(
							DatabaseSchema.CONSTITUENCY_MASTER.TABLE_NAME,
							values, DatabaseSchema.CONSTITUENCY_MASTER.ID);
					values = null;
				}
			}
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
		} finally {
			if (dbHelper != null)
				dbHelper.close();
		}
	}

	public String getItemListFromDatabase(int Type, String tableName,
			String whereParam, String[] selArg, String[] projection) {
		String response = "";
		DatabaseHelper dbHelper = new DatabaseHelper(m_Context);
		try {
			dbHelper.open();
			response = dbHelper.fetchRequiredList(Type, tableName, projection,
					whereParam, selArg);
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
		} finally {
			if (dbHelper != null)
				dbHelper.close();
		}
		return response;
	}

}
