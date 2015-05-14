package com.tribulationent.accountacop;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.MetadataChangeSet;
import com.skd.androidrecording.video.AdaptiveSurfaceView;
import com.skd.androidrecording.video.CameraHelper;
import com.skd.androidrecording.video.VideoRecordingHandler;
import com.skd.androidrecording.video.VideoRecordingManager;

public class RecVideoActivity extends Activity implements OnConnectionFailedListener,ConnectionCallbacks{

	private Button btnHome, btnRecStart, btnRecStop;
	
	private static final String TAG = "RecVideoActivity";
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private GoogleApiClient mGoogleApiClient;
    private Bitmap mBitmapToSave;

	private Size videoSize = null;
	private VideoRecordingManager recordingManager;
	private String fileName;
	
	private VideoRecordingHandler recordingHandler = new VideoRecordingHandler() {
		@Override
		public boolean onPrepareRecording() {
			if (videoSize == null) {
				initVideoSize();
				return true;
			}
			return false;
		}		
		@Override
		public Size getVideoSize() {
			return videoSize;
		}		
		@Override
		public int getDisplayRotation() {
			return RecVideoActivity.this.getWindowManager().getDefaultDisplay().getRotation();
		}
	};	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rec_video);
        
        btnHome = (Button) findViewById(R.id.take_photo_home_btn);
        btnRecStart = (Button) findViewById(R.id.take_photo_shutter_btn);
        btnRecStop  = (Button) findViewById(R.id.stop_rec_btn);   
        AdaptiveSurfaceView videoView = (AdaptiveSurfaceView) findViewById(R.id.camera_preview);
		recordingManager = new VideoRecordingManager(videoView, recordingHandler);	
		
		if (mGoogleApiClient == null) {
    		// Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
    	}
        // Connect the client. Once connected, the camera is launched.
        mGoogleApiClient.connect();
    }
    
    @SuppressLint("NewApi")
    private void initVideoSize() {
    	if (Build.VERSION.SDK_INT >= 11) {
    		List<Size> sizes = CameraHelper.getCameraSupportedVideoSizes(recordingManager.getCameraManager().getCamera());
			videoSize = (Size) sizes.get(0);
			recordingManager.setPreviewSize(videoSize);			
		}
    }
	
    public void onStartRec(View v) {
    	
		fileName = String.format("%d.mp4", System.currentTimeMillis());		
		try {
			FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
			fos.close();
			
			if (recordingManager.startRecording("mnt/sdcard/" + fileName, videoSize)) {
				btnRecStart.setVisibility(View.GONE);
		    	btnRecStop.setVisibility(View.VISIBLE);		    	
				btnHome.setEnabled(false);				
				return;
			}
		} catch (FileNotFoundException e) {
			Log.e("File Output Error : ", e.getMessage());
		} catch (IOException e) {
			Log.e("File Write Error : ", e.getMessage());
		}		
		Toast.makeText(this, "Recording Video Failed", Toast.LENGTH_LONG).show();
    }
    
    public void onStopRec(View v) {
    	if (recordingManager.stopRecording()) {
    		saveFileToDrive();
    		btnRecStart.setVisibility(View.VISIBLE);
	    	btnRecStop.setVisibility(View.GONE);
			btnHome.setEnabled(true);
		}
    }
    
    public void onBack(View v) {
    	this.onBackPressed();
    }
    
    @Override
    public void onBackPressed() {
    	
    	if (!mGoogleApiClient.isConnected()) {
    		Intent intent = new Intent(RecVideoActivity.this, MainActivity.class);
        	startActivity(intent);
        	finish();
        	Log.d("RecActivity", "Client disconnected");
		}else{
			mGoogleApiClient.disconnect();
		} 
    }
    
    @Override
	protected void onDestroy() {
		recordingManager.dispose();
		recordingHandler = null;		
		super.onDestroy();
	}
    
    /**
     * Upload to Drive.
     */
    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveContentsResult>() {

            @Override
            public void onResult(DriveContentsResult result) {
                // If the operation was not successful, we cannot do anything
                // and must
                // fail.
                if (!result.getStatus().isSuccess()) {
                    Log.i(TAG, "Failed to create new contents.");
                    return;
                }
                // Otherwise, we can write our data to the new contents.
                Log.i(TAG, "New contents created.");
                
                FileInputStream inputStream = null;
				try {
					inputStream = new FileInputStream("/mnt/sdcard/"+fileName);
				} catch (FileNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
            	OutputStream outputStream = result.getDriveContents().getOutputStream();
            	BufferedInputStream bin = new BufferedInputStream(inputStream);
            	BufferedOutputStream bout = new BufferedOutputStream(outputStream);
            	int bytesRead = 0;
            	try {
            		int bytesAvailable = bin.available(); 
       		     	int maxBufferSize = 1024 * 1024;
		            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
		            byte[] buffer = new byte[bufferSize];		     
		            // read file and write it into form...
		            bytesRead = bin.read(buffer, 0, bufferSize);
		            while (bytesRead > 0 ) {
		            	bout.write(buffer, 0, bufferSize);
		                bytesAvailable = bin.available();
		                bufferSize = Math.min(bytesAvailable, maxBufferSize);
		                bytesRead = bin.read(buffer, 0, bufferSize);  		                 
		            }
		            Log.e("Jpg Uploading", "Successfully uploaded");
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} 
            	// Create the initial metadata - MIME type and title.
                // Note that the user will be able to change the title later.
                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                        .setMimeType("video/mpeg4").setTitle(fileName).build();
                Drive.DriveApi.getRootFolder(mGoogleApiClient).createFile(mGoogleApiClient, metadataChangeSet, result.getDriveContents());     
                File file = new File("mnt/sdcard/" + fileName);
                file.delete();
            }            
        });        
    }

    @Override
    protected void onResume() {
    	super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        //unnecessary code
    	switch (requestCode) {
            case REQUEST_CODE_CREATOR:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Image successfully saved.");
                    mBitmapToSave = null;
                    // Just start the camera again for another photo.
                }
            break;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "API client connected.");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }
}
