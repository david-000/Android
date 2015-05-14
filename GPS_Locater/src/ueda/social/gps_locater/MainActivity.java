//package ueda.social.gps_locater;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.location.Location;
//import android.media.MediaRecorder;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.preference.PreferenceManager;
//import android.provider.MediaStore;
//import android.provider.MediaStore.MediaColumns;
//import android.provider.Settings;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesClient;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.location.LocationClient;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//
//public class MainActivity extends Activity implements 	GooglePlayServicesClient.ConnectionCallbacks,
//														GooglePlayServicesClient.OnConnectionFailedListener, 
//														LocationListener, OnClickListener{
//	// locations objects
//	LocationClient mLocationClient;
//	Location mCurrentLocation;
//    LocationRequest mLocationRequest;
//    
//    private MediaRecorder recorder = null;
//    
//	private TextView txtLong,txtLat,audio_name,audio_size,photo_name,photo_size;
//	private Button btn_select_photo,btn_selete_audio,btn_send;
//	private ImageView delete_photo,delete_audio;
//	private EditText message_content,email_address;
//	
//	private static final int REQUEST_CAMERA=1;
//	private static final int SELECT_FILE=2;
//	private static final int RQS_OPEN_AUDIO_MP3=3;
//	
//	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp3";
//	private static final String AUDIO_RECORDER_FOLDER = "GPS Locater";
//	
//	private int output_formats = MediaRecorder.OutputFormat.MPEG_4;
//	private String file_exts =  AUDIO_RECORDER_FILE_EXT_MP4;
//	private String audiofile;
//	private String imagefile;
//	private Uri audio,image;	
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		// 1. setContnetView
//		setContentView(R.layout.activity_main);
//		
//		// 2. get reference to TextView
//		txtLong = (TextView) findViewById(R.id.longitude);
//		txtLat = (TextView) findViewById(R.id.latitude);
//		audio_name=(TextView)this.findViewById(R.id.audio_name);
////		audio_size=(TextView)this.findViewById(R.id.audio_size);
//		photo_name=(TextView)this.findViewById(R.id.photo_name);
////		photo_size=(TextView)this.findViewById(R.id.photo_size);
//		btn_select_photo=(Button)this.findViewById(R.id.btn_add_photo);
//		btn_selete_audio=(Button)this.findViewById(R.id.btn_add_audio);
//		btn_send=(Button)this.findViewById(R.id.btn_send);
//		delete_photo=(ImageView)this.findViewById(R.id.btn_delete_photo);
//		delete_audio=(ImageView)this.findViewById(R.id.btn_delete_audio);
//		message_content=(EditText)this.findViewById(R.id.message_content);
//		email_address=(EditText)this.findViewById(R.id.email_address);
//		
//		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);		
//		email_address.setText(sharedPreferences.getString("default_email", ""));
//		
//		btn_select_photo.setOnClickListener(this);
//		btn_selete_audio.setOnClickListener(this);
//		btn_send.setOnClickListener(this);
//		delete_audio.setOnClickListener(this);
//		delete_photo.setOnClickListener(this);
//		
//		// 3. create LocationClient
//		mLocationClient = new LocationClient(this, this, this);
//
//		// 4. create & set LocationRequest for Location update
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        // Set the update interval to 5 seconds
//        mLocationRequest.setInterval(1000 * 5);
//        // Set the fastest update interval to 1 second
//        mLocationRequest.setFastestInterval(1000 * 1);
//        audio=Uri.parse("");
//        image=Uri.parse("");
//	}
//	
//	protected void onStart() {
//        super.onStart();
//        mLocationClient.connect();
//    }
// 
//    protected void onStop() {
//    	super.onStop();
//    	mLocationClient.disconnect();        
//    }
//    
//	@Override
//	public boolean onMenuItemSelected(int featureId, MenuItem item) {
//		// TODO Auto-generated method stub
//		 int item_id;
//		 item_id=item.getItemId();
//		 switch (item_id) {
////		case R.id.add_email:			
////			break;
//		case R.id.set_default:
//			final Dialog dialog = new Dialog(MainActivity.this);
//			dialog.setContentView(R.layout.dialog_forget_pw);
//			dialog.setTitle("Set Default Email...");
//		
//			final EditText email=(EditText)dialog.findViewById(R.id.editText1);
//			Button submitButton = (Button) dialog.findViewById(R.id.button1);
//			// if button is clicked, close the custom dialog
//			submitButton.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if (email.getText().toString().equals("")) {
//						Toast.makeText(MainActivity.this, "Please enter email address!", Toast.LENGTH_SHORT).show();
//					}
//					else{
//						SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//						SharedPreferences.Editor editor = sharedPreferences.edit();		
//						editor.putString("default_email",email.getText().toString());
//						editor.commit();
//						email_address.setText(email.getText().toString());
//						dialog.dismiss();
//					}						
//				}	
//			});
//			dialog.show();
//			break;
//		case R.id.exit:
//			finish();
//			break;
//		default:
//			break;
//		}
//		return super.onMenuItemSelected(featureId, item);
//	}
//	@Override
//	public void onBackPressed() {
//		// TODO Auto-generated method stub
//		finish();
//	}
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// TODO Auto-generated method stub
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//	// GooglePlayServicesClient.OnConnectionFailedListener
//	@Override
//	public void onConnectionFailed(ConnectionResult result) {
//		if (!result.hasResolution()) {
//            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
//                    0).show();
//            return;
//        }  
//    }
//
//	// GooglePlayServicesClient.ConnectionCallbacks 
//	@Override
//	public void onConnected(Bundle arg0) {
//		
//		if(mLocationClient != null)
//			mLocationClient.requestLocationUpdates(mLocationRequest,  this);
//
//        if(mLocationClient != null){
//        	// get location
//        	mCurrentLocation = mLocationClient.getLastLocation();
//        	try{        			
//        		// set TextView(s) 
//        		txtLat.setText(mCurrentLocation.getLatitude()+"");
//        		txtLong.setText(mCurrentLocation.getLongitude()+"");        			
//        	}catch(NullPointerException npe){        			
//        		Toast.makeText(this, "Failed to Connect", Toast.LENGTH_SHORT).show();
//        		// switch on location service intent
//        		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        		startActivity(intent);
//        	}
//        }        
//	}
//	@Override
//	public void onDisconnected() {		
//		Toast.makeText(this, "Disconnected.", Toast.LENGTH_SHORT).show();	
//	}
//
//	// LocationListener
//	@Override
//	public void onLocationChanged(Location location) {
////		Toast.makeText(this, "Location changed.", Toast.LENGTH_SHORT).show();
//		mCurrentLocation = mLocationClient.getLastLocation();
//		txtLat.setText(mCurrentLocation.getLatitude()+"");
//		txtLong.setText(mCurrentLocation.getLongitude()+"");
//	}
//	
//	private void selectImage() {
//        final CharSequence[] items = { "Take Photo", "Choose from Library",
//                "Cancel" };
// 
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("Add Photo!");
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//                if (items[item].equals("Take Photo")) {
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File f = new File(android.os.Environment
//                            .getExternalStorageDirectory()+"/GPS Locater/", "image.jpg");
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//                    image=Uri.fromFile(f);  
//                    startActivityForResult(intent, REQUEST_CAMERA);
//                } else if (items[item].equals("Choose from Library")) {
//                    Intent intent = new Intent(
//                            Intent.ACTION_PICK,
//                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    intent.setType("image/*");
//                    startActivityForResult(
//                            Intent.createChooser(intent, "Select File"),
//                            SELECT_FILE);
//                } else if (items[item].equals("Cancel")) {
//                    dialog.dismiss();
//                }
//            }
//        });
//        builder.show();
//    }
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_CAMERA) {
//                File f = new File(Environment.getExternalStorageDirectory()
//                        .toString());
//                for (File temp : f.listFiles()) {
//                    if (temp.getName().equals("image.jpg")) {
//                        f = temp;
//                        break;
//                    }
//                }
//                photo_name.setText("image.jpg");
//            } else if (requestCode == SELECT_FILE) {
//                Uri selectedImageUri = data.getData(); 
//                image=selectedImageUri;
//                String tempPath = getPath(selectedImageUri, MainActivity.this);
//                Bitmap bm;
//                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
//                bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
//                int i=tempPath.length();
//                while (!tempPath.substring(i-1, i).equals("/")) {
//                	i--;
//                }
//                if (tempPath.substring(i).length()>20) {
//					String temp=tempPath.substring(i);
//					photo_name.setText(temp.substring(0,19)+"...");
//				}else{
//					photo_name.setText(tempPath.substring(i));
//				}
//                imagefile=tempPath;
////                Toast.makeText(MainActivity.this, imagefile, Toast.LENGTH_SHORT).show();
//            }else if(requestCode==RQS_OPEN_AUDIO_MP3){
//            	Uri audioFileUri = data.getData();
//            	audio=audioFileUri;
//            	String tempPath=getPath(audioFileUri, MainActivity.this);
//            	int i=tempPath.length();
//                while (!tempPath.substring(i-1, i).equals("/")) {
//                	i--;
//                }
//                if (tempPath.substring(i).length()>20) {
//					String temp=tempPath.substring(i);
//					audio_name.setText(temp.substring(0,19)+"...");
//				}else{
//					audio_name.setText(tempPath.substring(i));
//				}
//                audiofile=tempPath;
////                Toast.makeText(MainActivity.this, audiofile, Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//	public String getPath(Uri uri, Activity activity) {
//        String[] projection = { MediaColumns.DATA };
//        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		int id;
//		id=v.getId();
//		switch (id) {		
//		case R.id.btn_add_audio:
//			selectAudio();
//			break;
//		case R.id.btn_add_photo:
//			selectImage();
//			break;
//		case R.id.btn_delete_audio:
//			audio_name.setText("");			
//			audio=Uri.parse("");
//			break;
//		case R.id.btn_delete_photo:
//			photo_name.setText("");
//			image=Uri.parse("");
//			break;
//		case R.id.btn_send:
//			String emailTo 		= email_address.getText().toString();
//			String emailSubject 	= "Test Mail";
//			String emailContent 	= message_content.getText().toString()+"\n"+"Currently I 'm in "+"Longitude:"+txtLong.getText().toString()+"\n"+"Latitude:"+txtLat.getText().toString();
//			
//			Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//			emailIntent.setType("plain/text");
//			emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ emailTo});
//			emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
//			emailIntent.putExtra(Intent.EXTRA_TEXT, emailContent);			
//			////use below 3 commented lines if need to send attachment
//			//			 emailIntent .setType("image/jpeg");
//			//			 emailIntent .putExtra(Intent.EXTRA_STREAM, image);
//			//			 emailIntent .putExtra(Intent.EXTRA_STREAM, audio);
//			 
//			ArrayList<Uri> uris = new ArrayList<Uri>();
//			if (!image.toString().equals("")) {
//				uris.add(image);	
//			}
//			if (!audio.toString().equals("")) {
//				uris.add(audio);
//			}
//			if (uris.size()!=0) {
//				emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
//			}			
//			//			 emailIntent .putExtra(Intent.EXTRA_STREAM, "");
//			//			 emailIntent .setType("audio/mp3");
//			//need this to prompts email client only
//			emailIntent.setType("message/rfc822");
//			startActivity(Intent.createChooser(emailIntent, "Select an Email Client:"));
//			break;
//		default:
//			break;
//		}
//	}
//	
//	public void selectAudio() {
//		// TODO Auto-generated method stub
//		final CharSequence[] items = { "Record Audio", "Choose from Library",
//        "Cancel" };
//
//		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//		builder.setTitle("Add Audio!");
//		builder.setItems(items, new DialogInterface.OnClickListener() {
//		    @Override
//		    public void onClick(DialogInterface dialog, int item) {
//		        if (items[item].equals("Record Audio")) {
//		        	final Dialog dialog_record = new Dialog(MainActivity.this);
//		        	dialog_record.setContentView(R.layout.dialog_recording);
//		        	dialog_record.setTitle("Record Audio...");
//		        	
//					final TextView file_name=(TextView)dialog_record.findViewById(R.id.recording_audio_name);
//					final ImageView mic_image=(ImageView)dialog_record.findViewById(R.id.mic_image);
//					final Button start = (Button) dialog_record.findViewById(R.id.btn_record_start);
//					final Button save=(Button)dialog_record.findViewById(R.id.btn_audio_save);
//					final Button stop=(Button)dialog_record.findViewById(R.id.btn_stop);
//					
//					file_name.setText("audio.mp3");
//					stop.setEnabled(false);
//					save.setEnabled(false);
//					// if button is clicked, close the custom dialog
//					start.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							start.setEnabled(false);
//							stop.setEnabled(true);
//							mic_image.setImageResource(R.drawable.microphone_recording);
//							startRecording();
//						}	
//					});	
//					stop.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							start.setEnabled(false);
//							stop.setEnabled(false);
//							save.setEnabled(true);
//							mic_image.setImageResource(R.drawable.microphone);
//							stopRecording();
//						}
//					});
//					save.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							dialog_record.dismiss();
//							audio_name.setText("audio.mp3");
//							File f = new File(android.os.Environment
//		                            .getExternalStorageDirectory()+"/GPS Locater/", "audio.mp3");
//							audio=Uri.fromFile(f);
////							Toast.makeText(MainActivity.this, audiofile, Toast.LENGTH_SHORT).show();
//						}
//					});
//					dialog_record.show();
//		        } else if (items[item].equals("Choose from Library")) {
//		        	Intent intent = new Intent();
//		     	   	intent.setType("audio/mp3");
//		     	   	intent.setAction(Intent.ACTION_GET_CONTENT);
//		     	   	startActivityForResult(Intent.createChooser(
//		     	   				intent, "Open Audio (mp3) file"), RQS_OPEN_AUDIO_MP3);
//		        } else if (items[item].equals("Cancel")) {
//		            dialog.dismiss();
//		        }
//		    }
//		});
//		builder.show();
//	}
//	private String getFilename() {
//		String filepath = Environment.getExternalStorageDirectory().getPath();
//		File file = new File(filepath, AUDIO_RECORDER_FOLDER);
//
//		if (!file.exists()) {
//			file.mkdirs();
//		}
//
//		return (file.getAbsolutePath() + "/" + "audio" + file_exts);
//	}
//
//	private void startRecording() {
//		recorder = new MediaRecorder();
//
//		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//		recorder.setOutputFormat(output_formats);
//		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//		recorder.setOutputFile(getFilename());
//		audiofile=getFilename();
//		audio=Uri.parse(audiofile);
//
//		recorder.setOnErrorListener(errorListener);
//		recorder.setOnInfoListener(infoListener);
//
//		try {
//			recorder.prepare();
//			recorder.start();
//		} catch (IllegalStateException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void stopRecording() {
//		if (null != recorder) {
//			recorder.stop();
//			recorder.reset();
//			recorder.release();
//			recorder = null;
//		}
//	}
//	
//	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
//		@Override
//		public void onError(MediaRecorder mr, int what, int extra) {
//			Toast.makeText(MainActivity.this,
//					"Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
//		}
//	};
//
//	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
//		@Override
//		public void onInfo(MediaRecorder mr, int what, int extra) {
//			Toast.makeText(MainActivity.this,
//					"Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
//					.show();
//		}
//	};
//}
