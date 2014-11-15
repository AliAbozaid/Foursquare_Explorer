package com.abozaid.foursquareexplorer;

import com.abozaid.foursquaretest.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {

	private final int SPLASH_DISPLAY_LENGTH = 1500;
	protected boolean _active = true;
	SharedPreferences UserInformation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.splash);
		// thread for displaying the SplashScreen
		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (_active && (waited < SPLASH_DISPLAY_LENGTH)) {
						sleep(100);
						if (_active) {
							waited += 100;
						}
					}
				} catch (InterruptedException e) {
					// do nothing
				} finally {
					finish();
					UserInformation = getSharedPreferences("userinfo", 0);
					String token = UserInformation.getString("accessToken", "");
					if (!token.equals("") && token != null) {
						Intent mainIntent = new Intent(SplashActivity.this,
								MapActivity.class);
						SplashActivity.this.startActivity(mainIntent);
					} else {
						Intent mainIntent = new Intent(SplashActivity.this,
								FourSquareOauthActivity.class);
						SplashActivity.this.startActivity(mainIntent);
					}

				}
			}
		};
		splashTread.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// _active = false;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		finish();
	}

}
