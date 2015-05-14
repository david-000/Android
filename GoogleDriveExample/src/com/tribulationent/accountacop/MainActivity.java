package com.tribulationent.accountacop;




import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
 
public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {
 
    private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "MainActivity"; 
    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400; 
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient; 
    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress,mSignInClicked;  
    
    private ConnectionResult mConnectionResult;
    
    private ImageView btnSignIn,btnSignOut,btnRecord;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        btnSignIn = (ImageView) findViewById(R.id.btn_sign_in);        
        btnSignOut=(ImageView)findViewById(R.id.btn_sign_out);
        btnRecord=(ImageView)findViewById(R.id.btn_record); 
        
        // Button click listeners
        btnRecord.setOnClickListener(new OnClickListener() {
		
    	   @Override
			public void onClick(View v) {
			// TODO Auto-generated method stub
    		   if (mGoogleApiClient.isConnected()) {
           			Intent intent=new Intent(MainActivity.this,RecVideoActivity.class);
           			startActivity(intent);
   				}else{
   					Toast.makeText(MainActivity.this, "Please Sign In", Toast.LENGTH_SHORT).show();
   				}
    	   }
        });
        btnSignIn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mGoogleApiClient.isConnected()) {
					Toast.makeText(MainActivity.this, "Already connnected", Toast.LENGTH_SHORT).show();
				}
				else{
					signInWithGplus(); 
				}
			}
		});
        btnSignOut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mGoogleApiClient.isConnected()) {
	        		signOutFromGplus();
				} else {
					Toast.makeText(MainActivity.this,"Already disconnected",Toast.LENGTH_SHORT).show();
				}       
			}
		});
        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API,new Plus.PlusOptions.Builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }
 
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
			Toast.makeText(MainActivity.this,"Connected",Toast.LENGTH_SHORT).show();
		}
    }
 
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }     
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }     
        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;     
            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }     
    }     
    @Override
    protected void onActivityResult(int requestCode, int responseCode,Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }     
            mIntentInProgress = false;     
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }
     
    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        Toast.makeText(this, "Drive connected!", Toast.LENGTH_SHORT).show(); 
    }
     
    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }  

    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();           
        }
   }     
    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }        
    }
    /**
     * Sign-out from google
     * */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            Toast.makeText(MainActivity.this,"Successfully Disconnected",Toast.LENGTH_SHORT).show(); 
            }
    }
 
    @Override
    public void onBackPressed() {      	
    	signOutFromGplus();
    	finish();
    }    
}