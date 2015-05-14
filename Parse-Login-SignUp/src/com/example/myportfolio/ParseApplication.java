package com.example.myportfolio;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

public class ParseApplication extends Application {

	private String AppID = "0Gcaq69hREGImooQ5TzSHgVmmxUvXf7Y2FL8nac3";
	private String ClientID = "4MrMG6k5p6I3O9ftliqwGWThv57zZievfOOlx5Di";
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
