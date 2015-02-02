package com.mxo2.votingapp;

import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mxo2.votingapp.utils.MyService;
import com.mxo2.votingapp.utils.ParentActivity;
import com.mxo2.votingapp.utils.TelephonyInfo;

public class SplashScreen extends ParentActivity {
	
	public static String name;
	public static String email;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		//isDualSimOrNot();
		startAnimation();

		Intent services = new Intent(getApplicationContext(), MyService.class);
		services.putExtra("name", "SurvivingwithAndroid");
		startService(services);
		name = getIMEI();
		if(getAccount()!= null && !TextUtils.isEmpty(getAccount())){
			email = getAccount();
		}else{
			email ="";
		}
		

	}
	public String getIMEI() {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String IMEINumber = tm.getDeviceId();
		Log.i("IMEI Number is", IMEINumber);
		return IMEINumber;
	}
	
	public String getAccount() {
		AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
		Account[] list = manager.getAccountsByType("com.google");
		if (list.length != 0) {
			String email = list[0].name;
			return email;
		} else {
			return null;
		}
	}

	private void isDualSimOrNot() {
		TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);

		String imeiSIM1 = telephonyInfo.getImsiSIM1();
		String imeiSIM2 = telephonyInfo.getImsiSIM2();

		boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
		boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

		boolean isDualSIM = telephonyInfo.isDualSIM();
		Log.e("Dual = ", " IME1 : " + imeiSIM1 + "\n" + " IME2 : " + imeiSIM2
				+ "\n" + " IS DUAL SIM : " + isDualSIM + "\n"
				+ " IS SIM1 READY : " + isSIM1Ready + "\n"
				+ " IS SIM2 READY : " + isSIM2Ready + "\n");
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
	}

	private void startAnimation() {
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
		anim.reset();
		RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_layout);
		rel.clearAnimation();
		rel.startAnimation(anim);

		anim = AnimationUtils.loadAnimation(this, R.anim.translate);
		anim.reset();
		ImageView iv = (ImageView) findViewById(R.id.img_toplogo);
		iv.clearAnimation();
		iv.startAnimation(anim);

		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Intent i = new Intent(SplashScreen.this, HomeScreen.class);
				SplashScreen.this.startActivity(i);
				SplashScreen.this.finish();
			}
		});

	}
	
}
