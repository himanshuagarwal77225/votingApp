package com.mxo2.votingapp.utils;

import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;

public class MyService extends Service implements LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	String GPS_FILTER = "";
	Thread triggerService;
	LocationListener locationListener;
	LocationManager lm;
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10
																		// meter

	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000 * 30 * 1; // 1
																			// minute
	protected LocationManager locationManager;
	boolean isRunning = true;

	Calendar cur_cal = Calendar.getInstance();

	Location location;
	double latitude; // latitude
	double longitude;
	// flag for GPS status
	boolean isGPSEnabled = false;
	// flag for network status
	boolean isNetworkEnabled = false;
	// flag for GPS status
	boolean canGetLocation = false;
	private String provider;

	private int year;
	private int month;
	private int day;
	private String date;

	private int hours;
	private int min;

	// private LocationClient mLocationClient;

	GPSTracker gps;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// mLocationClient = new LocationClient(this, this, this);

		// mLocationClient = MainActivity.mLocationClient;
		// if (!mLocationClient.isConnected())
		// mLocationClient.connect();

		Intent intent = new Intent(this, MyService.class);
		PendingIntent pintent = PendingIntent.getService(
				getApplicationContext(), 0, intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		GPS_FILTER = "MyGPSLocation";
		// locationManager = (LocationManager)
		// getSystemService(Context.LOCATION_SERVICE);
		// locationManager.requestLocationUpdates(
		// LocationManager.GPS_PROVIDER,
		// MINIMUM_TIME_BETWEEN_UPDATES,
		// MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
		// new MyLocationListener());
		cur_cal.setTimeInMillis(System.currentTimeMillis());
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cur_cal.getTimeInMillis(),
				30 * 1000 * 1, pintent);
	}

	@Override
	public void onStart(Intent intent, int startId) {

		// TODO Auto-generated method stub
		super.onStart(intent, startId);

		gps = new GPSTracker(MyService.this);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		// getting GPS status
		isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		locationListener = new MyLocationListener();
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		// Toast.makeText(getApplicationContext(), provider,
		// Toast.LENGTH_LONG).show();

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH) + 1;
		day = c.get(Calendar.DAY_OF_MONTH);

		hours = c.get(Calendar.HOUR_OF_DAY);
		min = c.get(Calendar.MINUTE);

		Log.i("Current time is :", hours + "-" + min);

		date = day + "/" + month + "/" + year;

		if (!isGPSEnabled && !isNetworkEnabled) {

			// no network provider is enabled
		} else {
			this.canGetLocation = true;

			// if GPS Enabled get lat/long using GPS Services
			if (isGPSEnabled) {
				location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (location == null) {
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER,
							MINIMUM_TIME_BETWEEN_UPDATES,
							MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("GPS Enabled", "GPS Enabled");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);// getLastKnownLocation();
																					// //
																					// locationManager.getLastKnownLocation(provider);
						Log.i("Location Client  connected", "location client");
						/*
						 * if (mLocationClient.isConnected()) {
						 * Log.i("Location Client  connected",
						 * "location client");
						 * 
						 * Location mCurrentLocation; mCurrentLocation =
						 * mLocationClient .getLastLocation();
						 */
						if (gps.canGetLocation()) {

							double latitude = gps.getLatitude();
							double longitude = gps.getLongitude();

							Log.i("GPS CAN GET LOCATION", latitude + ","
									+ longitude + " GPS");

							// if (location != null) {

							/*
							 * double latitude = location.getLatitude();
							 * mCurrentLocation .getLatitude();//
							 * googleMap.getMyLocation().getLatitude(); double
							 * longitude =location.getLongitude();
							 * mCurrentLocation .getLongitude();//
							 * googleMap.getMyLocation().getLongitude();
							 */

						}
						/*
						 * } else{ Log.i("Location Client not connected",
						 * "location client"); }
						 */
					}
				}
			}

			// First get location from Network Provider
			else if (isNetworkEnabled) {

				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER,
						MINIMUM_TIME_BETWEEN_UPDATES,
						MINIMUM_TIME_BETWEEN_UPDATES, this);
				Log.d("Network", "Network");
				if (locationManager != null) {

					location = locationManager
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);// getLastKnownLocation();

					Location mCurrentLocation; // //
					// mCurrentLocation = mLocationClient.getLastLocation(); //
					// locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					// Toast.makeText(getApplicationContext(), location+"",
					// Toast.LENGTH_LONG).show();
					if (location != null) {

						double latitude = location.getLatitude();// mCurrentLocation.getLatitude();//
																	// googleMap.getMyLocation().getLatitude();
						double longitude = location.getLongitude();// mCurrentLocation.getLongitude();//
																	// googleMap.getMyLocation().getLongitude();
						Log.i("GPS CAN GET LOCATION", latitude + ","
								+ longitude + " GPS");
					}
				}
			}
		}

	}

	private Location getLastKnownLocation() {
		List<String> providers = locationManager.getProviders(true);
		Location bestLocation = null;
		for (String provider : providers) {
			Location l = locationManager.getLastKnownLocation(provider);
			Log.d("last known location, provider: %s, location: %s", provider
					+ l);

			if (l == null) {
				continue;
			}
			if (bestLocation == null
					|| l.getAccuracy() < bestLocation.getAccuracy()) {
				Log.d("found best last known location: %s", l + "");
				bestLocation = l;
			}
		}
		if (bestLocation == null) {
			return null;
		}
		return bestLocation;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// mLocationClient.disconnect();
		removeGpsListener();
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void removeGpsListener() {
		if (locationManager != null) {
			locationManager.removeUpdates(MyService.this);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			postdata(location.getLatitude(), location.getLongitude());
			String message = String.format(
					"New Location \n Longitude: %1$s \n Latitude: %2$s",
					location.getLongitude(), location.getLatitude());
		}

		private void postdata(double latitude, double longitude) {

		}

		public void onStatusChanged(String s, int i, Bundle b) {

		}

		public void onProviderDisabled(String s) {

		}

		public void onProviderEnabled(String s) {

		}

	}

	public void turnGPSOn() {
		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", true);
		this.sendBroadcast(intent);

		String provider = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!provider.contains("gps")) { // if gps is disabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			this.sendBroadcast(poke);

		}
	}

	// automatic turn off the gps
	public void turnGPSOff() {
		String provider = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (provider.contains("gps")) { // if gps is enabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			this.sendBroadcast(poke);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		postdata(location.getLatitude(), location.getLongitude());
		String message = String.format(
				"New Location \n Longitude: %1$s \n Latitude: %2$s",
				location.getLongitude(), location.getLatitude());
	}

	private void postdata(double latitude, double longitude) {
		// TODO Auto-generated method stub
		/*
		 * Toast.makeText(getApplicationContext(), latitude + ", " + longitude,
		 * Toast.LENGTH_LONG).show();
		 */
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(MyService.this);
		}
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Log.e("Connection Failed Location Client", arg0.toString());
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		Log.e("Connected  Location Client", arg0.toString());
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

}
