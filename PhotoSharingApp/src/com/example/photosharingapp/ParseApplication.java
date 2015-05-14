package com.example.photosharingapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

public class ParseApplication extends Application {

	private String AppID = "am5rgq1FR4BAc3JEsQvGQ9EjXCB2d3DESLP8mogd";
	private String ClientID = "5uaag7kjxjrYXH00deEwYnhtorjA8TstkXQlf6Vo";
	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
        Parse.initialize(this, AppID, ClientID);

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();

		// If you would like all objects to be private by default, remove this
		// line.
		defaultACL.setPublicReadAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);
	}
}
