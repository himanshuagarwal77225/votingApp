package com.mxo2.votingapp;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		//isDualSimOrNot();
		startAnimation();

		Intent services = new Intent(getApplicationContext(), MyService.class);
		services.putExtra("name", "SurvivingwithAndroid");
		startService(services);

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
