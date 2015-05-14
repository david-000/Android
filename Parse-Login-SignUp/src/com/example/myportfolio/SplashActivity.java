package com.example.myportfolio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
				
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);
		
		Thread background = new Thread(){
			public void run(){
				try{
					//Thread will sleep for few seconds
					sleep(2000);
					
					Intent i = new Intent(getBaseContext(), LoginActivity.class);
					startActivity(i);
					
					finish();
				}catch(Exception e){
					
				}
			}
		};
		
		background.start();
	}
}
