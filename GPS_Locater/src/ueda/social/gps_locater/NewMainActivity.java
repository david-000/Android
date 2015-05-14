package ueda.social.gps_locater;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewMainActivity extends Activity implements OnClickListener {

	private Button btnShowLocation,btn_send,btn_add_audio,btn_add_photo,btn_settings,btn_exit;
	private TextView txt_longitude,txt_latitude,txt_time_stamp;
	private ImageView photo_image,audio_image;
	private LinearLayout get_location,location_text,attachment;
	
	private GPSTracker gps;
	
	private MediaRecorder recorder = null;
	private MediaPlayer mPlayer;
	
	private String default_email;
	private String default_name;
	private String sender_email;
	private String str_sender_name;
	private String password;
	
	private static final int REQUEST_CAMERA=1;
	private static final int SELECT_FILE=2;
	private static final int RQS_OPEN_AUDIO_MP3=3;
	private static final int SEND_SUCCESS=4;
	private static final int SEND_FAILED=5;
	
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp3";
	private static final String AUDIO_RECORDER_FOLDER = "GPS Locater";
	
	private int output_formats = MediaRecorder.OutputFormat.MPEG_4;
	private int btn_count=0;
	private String file_exts =  AUDIO_RECORDER_FILE_EXT_MP4;
	private String audiofile;
	private String imagefile;
	private String time_stamp;
	
	private long startTime = 0L;
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	
	private Uri audio,image;	
	
	private ProgressDialog progress_dg;	
	private Handler customHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_new);
		
		btnShowLocation=(Button)this.findViewById(R.id.btn_get_location);
		btn_send=(Button)this.findViewById(R.id.btn_send);
		btn_add_audio=(Button)this.findViewById(R.id.btn_add_audio);
		btn_add_photo=(Button)this.findViewById(R.id.btn_add_photo);
		btn_settings=(Button)this.findViewById(R.id.btn_settings);
		btn_exit=(Button)this.findViewById(R.id.btn_exit);
		photo_image=(ImageView)this.findViewById(R.id.photo_image);
		audio_image=(ImageView)this.findViewById(R.id.audio_image);
		txt_latitude=(TextView)this.findViewById(R.id.latitude);
		txt_longitude=(TextView)this.findViewById(R.id.longitude);
		txt_time_stamp=(TextView)this.findViewById(R.id.time_stamp);
		get_location=(LinearLayout)this.findViewById(R.id.linear4);
		location_text=(LinearLayout)this.findViewById(R.id.linear6);
		attachment=(LinearLayout)this.findViewById(R.id.linear2);
		
		initView();
		
		btnShowLocation.setOnClickListener(this);
		btn_send.setOnClickListener(this);
		btn_add_audio.setOnClickListener(this);
		btn_add_photo.setOnClickListener(this);
		btn_settings.setOnClickListener(this);
		btn_exit.setOnClickListener(this);
		audio_image.setOnClickListener(this);
		photo_image.setOnClickListener(this);
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewMainActivity.this);		
		default_email=sharedPreferences.getString("default_email", "");
		default_name=sharedPreferences.getString("default_name", "");
		sender_email=sharedPreferences.getString("sender_email", "");
		str_sender_name=sharedPreferences.getString("sender_name", "");
		password=sharedPreferences.getString("password", "");
		
		if (default_email.equals("")||sender_email.equals("")||password.equals("")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(NewMainActivity.this);
			builder.setTitle("Reminder");
			String text="It looks like you are new to this app.\nPlease set your email address, password, and default recipient email address.\nYour email address should be a Gmail account.";
			builder.setMessage(text);
			builder.setNegativeButton("OK",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					final Dialog new_dialog = new Dialog(NewMainActivity.this);
					new_dialog.setContentView(R.layout.dialog_forget_pw);
					new_dialog.setTitle("Set Default Email...");
				
					final EditText receiver=(EditText)new_dialog.findViewById(R.id.recipient_email);
					final EditText receiver_name=(EditText)new_dialog.findViewById(R.id.recipient_name);
					final EditText sender=(EditText)new_dialog.findViewById(R.id.sender_email);
					final EditText sender_name=(EditText)new_dialog.findViewById(R.id.sender_name);
					final EditText sender_password=(EditText)new_dialog.findViewById(R.id.sender_password);
					Button submitButton = (Button) new_dialog.findViewById(R.id.button1);
					
					receiver.setText(default_email);
					receiver_name.setText(default_name);
					sender.setText(sender_email);
					sender_name.setText(str_sender_name);
					sender_password.setText(password);
					// if button is clicked, close the custom dialog
					submitButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (sender.getText().toString().equals("")) {
								Toast.makeText(NewMainActivity.this, "Please enter sender email address!", Toast.LENGTH_SHORT).show();
							}else if(sender_password.getText().toString().equals("")){
								Toast.makeText(NewMainActivity.this, "Please enter sender email password", Toast.LENGTH_SHORT).show();
							}else if(receiver.getText().toString().equals("")){
								Toast.makeText(NewMainActivity.this, "Please enter receiver email address", Toast.LENGTH_SHORT).show();
							}else{
								SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewMainActivity.this);
								SharedPreferences.Editor editor = sharedPreferences.edit();		
								editor.putString("default_email",receiver.getText().toString());
								editor.putString("default_name", receiver_name.getText().toString());
								editor.putString("sender_email", sender.getText().toString());
								editor.putString("sender_name", sender_name.getText().toString());
								editor.putString("password", sender_password.getText().toString());
								editor.commit();	
								default_email=sharedPreferences.getString("default_email", "");
								default_name=sharedPreferences.getString("default_name", "");
								sender_email=sharedPreferences.getString("sender_email", "");
								str_sender_name=sharedPreferences.getString("sender_name", "");
								password=sharedPreferences.getString("password", "");
								new_dialog.dismiss();
							}						
						}	
					});
					new_dialog.show();
					dialog.dismiss();
				}
			});
			builder.show();
		}		
		
		audio=Uri.parse("");
		image=Uri.parse("");
		audiofile="";
		imagefile="";
		
		progress_dg=new ProgressDialog(this);
		progress_dg.setMessage("Sending email...");
	}

	public void initView(){
		location_text.setVisibility(View.INVISIBLE);
		attachment.setVisibility(View.INVISIBLE);
		btn_send.setVisibility(View.INVISIBLE);
		audio_image.setEnabled(false);
		photo_image.setEnabled(false);
		btn_send.setEnabled(false);
		btn_send.setAlpha(0.5f);
		txt_time_stamp.setText("");
	}
	
	public void sending_view(){
		btn_send.setText("Sending");
		btn_send.setEnabled(false);
		btn_send.setAlpha(0.5f);
		btn_add_audio.setEnabled(false);
		btn_add_audio.setAlpha(0.5f);
		btn_add_photo.setEnabled(false);
		btn_add_photo.setAlpha(0.5f);
		btn_settings.setEnabled(false);
		btn_settings.setAlpha(0.5f);
		btn_exit.setEnabled(false);
		btn_exit.setAlpha(0.5f);
		btnShowLocation.setEnabled(false);
		btnShowLocation.setAlpha(0.5f);
	}
	
	public void sent_view(){
		location_text.setVisibility(View.INVISIBLE);
		btn_send.setText("Sent");
		btn_send.setEnabled(false);
		btn_send.setAlpha(0.5f);
		btn_add_audio.setEnabled(true);
		btn_add_audio.setAlpha(1.0f);
		btn_add_audio.setText("Add audio");
		btn_add_photo.setEnabled(true);
		btn_add_photo.setAlpha(1.0f);
		btn_add_photo.setText("Add photo");
		btn_settings.setEnabled(true);
		btn_settings.setAlpha(1.0f);
		btn_exit.setEnabled(true);
		btn_exit.setAlpha(1.0f);
		btnShowLocation.setEnabled(true);
		btnShowLocation.setAlpha(1.0f);
		audio_image.setImageResource(R.drawable.empty_audio);
		photo_image.setImageResource(R.drawable.empty_image);
		audio_image.setEnabled(false);
		audio=Uri.parse("");
		image=Uri.parse("");
		audiofile="";
		imagefile="";
		txt_time_stamp.setText("");
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int item_id=v.getId();
		switch (item_id) {
			case R.id.btn_get_location:
				Calendar c = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
				time_stamp=sdf.format(c.getTime());
				txt_time_stamp.setText("Updated:"+sdf.format(c.getTime()));
				gps=new GPSTracker(NewMainActivity.this);
				if (gps.canGetlocation) {
					location_text.setVisibility(View.VISIBLE);
					attachment.setVisibility(View.VISIBLE);
					btn_send.setVisibility(View.VISIBLE);
					btnShowLocation.setText("Get new location");
					if (!image.toString().equals("")&&!audio.toString().equals("")) {
		            	btn_send.setEnabled(true);
						btn_send.setAlpha(1.0f);
						btn_send.setText("Send");
					}else{
						btn_send.setEnabled(false);
						btn_send.setAlpha(0.5f);
						btn_send.setText("Send");
					}
					double latitude=gps.getLatitude();
					double longitude=gps.getLongitude();
					txt_latitude.setText(String.valueOf(latitude));
					txt_longitude.setText(String.valueOf(longitude));
				}else{
					location_text.setVisibility(View.INVISIBLE);
					attachment.setVisibility(View.INVISIBLE);
					btn_send.setVisibility(View.INVISIBLE);
					gps.showSettingAlert();
				}
				break;
			case R.id.btn_add_audio:
				selectAudio();
				break;
			case R.id.btn_add_photo:
				selectImage();
				break;
			case R.id.btn_send:
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewMainActivity.this);
				default_email=sharedPreferences.getString("default_email", "");
				default_name=sharedPreferences.getString("default_name", "");
				sender_email=sharedPreferences.getString("sender_email", "");
				str_sender_name=sharedPreferences.getString("sender_name", "");
				password=sharedPreferences.getString("password", "");
				
				if (default_email.equals("")||sender_email.equals("")||password.equals("")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(NewMainActivity.this);
					builder.setTitle("Reminder");
					builder.setMessage("It looks you are first to this app.\nPlease set your email address,password and default receiver email address!\nYour email must be gmail account!");
					builder.setNegativeButton("OK",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
					builder.show();				
				}else{
					progress_dg.show();
					sending_view();
					new Thread(new Runnable() {
						public void run() {
							try {
								GMailSender sender = new GMailSender(
									sender_email,
									password);
								if (!audiofile.equals("")) {
									sender.addAudio(audiofile);
								}
								if (!imagefile.equals("")) {
									sender.addImage(imagefile);
								}								
								Log.d("fhrewkugrehikge", audiofile);
								Log.d("fhwuifheugie", imagefile);
								sender.sendMail("My current location", "My current location at "
										+time_stamp+"\n"
										+"Longitude:"+txt_longitude.getText().toString()										
										+"\nLatitude:"+txt_latitude.getText().toString()
										+"\nHere is a link to my current location: http://maps.google.com/maps?q=loc:"
										+txt_latitude.getText().toString()+","+txt_longitude.getText().toString()+"&gt",
										sender_email,
										default_email);
								Log.d("figbfiobfjsiob", "Success");
								myHandler.sendEmptyMessage(SEND_SUCCESS);
							} catch (Exception e) {
								e.printStackTrace();
								Log.d("vfhiogrejgioerg", "faild");
								myHandler.sendEmptyMessage(SEND_FAILED);
							}
						}
					}).start();
				}				
				break;
			case R.id.btn_settings:
				final Dialog dialog = new Dialog(NewMainActivity.this);
				dialog.setContentView(R.layout.dialog_forget_pw);
				dialog.setTitle("Set Default Email...");
			
				final EditText receiver=(EditText)dialog.findViewById(R.id.recipient_email);
				final EditText receiver_name=(EditText)dialog.findViewById(R.id.recipient_name);
				final EditText sender=(EditText)dialog.findViewById(R.id.sender_email);
				final EditText sender_name=(EditText)dialog.findViewById(R.id.sender_name);
				final EditText sender_password=(EditText)dialog.findViewById(R.id.sender_password);
				Button submitButton = (Button) dialog.findViewById(R.id.button1);
				
				receiver.setText(default_email);
				receiver_name.setText(default_name);
				sender.setText(sender_email);
				sender_name.setText(str_sender_name);
				sender_password.setText(password);
				// if button is clicked, close the custom dialog
				submitButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (sender.getText().toString().equals("")) {
							Toast.makeText(NewMainActivity.this, "Please enter sender email address!", Toast.LENGTH_SHORT).show();
						}else if(sender_password.getText().toString().equals("")){
							Toast.makeText(NewMainActivity.this, "Please enter sender email password", Toast.LENGTH_SHORT).show();
						}else if(receiver.getText().toString().equals("")){
							Toast.makeText(NewMainActivity.this, "Please enter receiver email address", Toast.LENGTH_SHORT).show();
						}else{
							SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewMainActivity.this);
							SharedPreferences.Editor editor = sharedPreferences.edit();		
							editor.putString("default_email",receiver.getText().toString());
							editor.putString("default_name", receiver_name.getText().toString());
							editor.putString("sender_email", sender.getText().toString());
							editor.putString("sender_name", sender_name.getText().toString());
							editor.putString("password", sender_password.getText().toString());
							editor.commit();							
							default_email=sharedPreferences.getString("default_email", "");
							default_name=sharedPreferences.getString("default_name", "");
							sender_email=sharedPreferences.getString("sender_email", "");
							str_sender_name=sharedPreferences.getString("sender_name", "");
							password=sharedPreferences.getString("password", "");
							dialog.dismiss();
						}						
					}	
				});
				dialog.show();
				break;
			case R.id.btn_exit:
				finish();
				break;
			case R.id.photo_image:
				break;
			case R.id.audio_image:
				btn_count++;
				switch (btn_count%2) {
					case 0:	
						if (mPlayer.isPlaying()) {
							mPlayer.stop();
							audio_image.setImageResource(R.drawable.audio1);
						}
						break;
					case 1:
						audio_image.setImageResource(R.drawable.audio);
						mPlayer = new MediaPlayer();
						mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
						try {
							mPlayer.setDataSource(getApplicationContext(), audio);
						} catch (IllegalArgumentException e) {
							Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
						} catch (SecurityException e) {
							Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
						} catch (IllegalStateException e) {
							Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							mPlayer.prepare();
						} catch (IllegalStateException e) {
							Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
						} catch (IOException e) {
							Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
						}
						mPlayer.start();
						break;
					default:
						break;
				}				
				break;
			default:
				break;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int item_id=item.getItemId();
		switch (item_id) {
		case R.id.set_default:
			final Dialog dialog = new Dialog(NewMainActivity.this);
			dialog.setContentView(R.layout.dialog_forget_pw);
			dialog.setTitle("Set Default Email...");
		
			final EditText receiver=(EditText)dialog.findViewById(R.id.recipient_email);
			final EditText receiver_name=(EditText)dialog.findViewById(R.id.recipient_name);
			final EditText sender=(EditText)dialog.findViewById(R.id.sender_email);
			final EditText sender_name=(EditText)dialog.findViewById(R.id.sender_name);
			final EditText sender_password=(EditText)dialog.findViewById(R.id.sender_password);
			Button submitButton = (Button) dialog.findViewById(R.id.button1);
			
			receiver.setText(default_email);
			receiver_name.setText(default_name);
			sender.setText(sender_email);
			sender_name.setText(str_sender_name);
			sender_password.setText(password);
			// if button is clicked, close the custom dialog
			submitButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (sender.getText().toString().equals("")) {
						Toast.makeText(NewMainActivity.this, "Please enter sender email address!", Toast.LENGTH_SHORT).show();
					}else if(sender_password.getText().toString().equals("")){
						Toast.makeText(NewMainActivity.this, "Please enter sender email password", Toast.LENGTH_SHORT).show();
					}else if(receiver.getText().toString().equals("")){
						Toast.makeText(NewMainActivity.this, "Please enter receiver email address", Toast.LENGTH_SHORT).show();
					}else{
						SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewMainActivity.this);
						SharedPreferences.Editor editor = sharedPreferences.edit();		
						editor.putString("default_email",receiver.getText().toString());
						editor.putString("default_name", receiver_name.getText().toString());
						editor.putString("sender_email", sender.getText().toString());
						editor.putString("sender_name", sender_name.getText().toString());
						editor.putString("password", sender_password.getText().toString());
						editor.commit();	
						default_email=sharedPreferences.getString("default_email", "");
						default_name=sharedPreferences.getString("default_name", "");
						sender_email=sharedPreferences.getString("sender_email", "");
						str_sender_name=sharedPreferences.getString("sender_name", "");
						password=sharedPreferences.getString("password", "");
						dialog.dismiss();
					}						
				}	
			});
			dialog.show();
			break;
		case R.id.exit:
			finish();
			break;
		default:
			break;
		}
		return true;
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                File f = new File(Environment.getExternalStorageDirectory()
                        .toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("image.jpg")) {
                        f = temp;
                        break;
                    }
                }
//                image=Uri.fromFile(f); 
//                imagefile=image.getPath();
//                imagefile=f.getPath();
	            if (!image.toString().equals("")) {
					btn_add_photo.setText("Change Photo");					
					photo_image.setImageResource(R.drawable.image);
					photo_image.setImageURI(image);
				}
	            if (!image.toString().equals("")&&!audio.toString().equals("")) {
	            	btn_send.setEnabled(true);
					btn_send.setAlpha(1.0f);
				}
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData(); 
                image=selectedImageUri;
                String tempPath = getPath(selectedImageUri, NewMainActivity.this);
                Bitmap bm;
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                imagefile=tempPath;
//                Toast.makeText(NewMainActivity.this, imagefile, Toast.LENGTH_SHORT).show();
                if (!image.toString().equals("")) {
					btn_add_photo.setText("Change Photo");
					photo_image.setImageResource(R.drawable.image);
					photo_image.setImageURI(image);
				}
                if (!image.toString().equals("")&&!audio.toString().equals("")) {
	            	btn_send.setEnabled(true);
					btn_send.setAlpha(1.0f);
				}
            }else if(requestCode==RQS_OPEN_AUDIO_MP3){
            	Uri audioFileUri = data.getData();
            	audio=audioFileUri;
            	String tempPath=getPath(audioFileUri, NewMainActivity.this);
                audiofile=tempPath;
                if (!audio.toString().equals("")) {
					btn_add_audio.setText("Change Audio");
					audio_image.setImageResource(R.drawable.audio1);	
					audio_image.setEnabled(true);
				}
                if (!image.toString().equals("")&&!audio.toString().equals("")) {
	            	btn_send.setEnabled(true);
					btn_send.setAlpha(1.0f);
				}
            }
        }
    }
	public String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaColumns.DATA };
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
	
	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
		          "Cancel" };
		
		AlertDialog.Builder builder = new AlertDialog.Builder(NewMainActivity.this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
		    public void onClick(DialogInterface dialog, int item) {
		        if (items[item].equals("Take Photo")) {
		            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		            File f = new File(android.os.Environment
		                    .getExternalStorageDirectory()+"/GPS Locater/", "image.jpg");
		            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		            image=Uri.fromFile(f);
		            imagefile=f.getPath();
		            startActivityForResult(intent, REQUEST_CAMERA);
		        } else if (items[item].equals("Choose from Library")) {
		            Intent intent = new Intent(
		                    Intent.ACTION_PICK,
		                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		            intent.setType("image/*");
		            startActivityForResult(
		                    Intent.createChooser(intent, "Select File"),
		                    SELECT_FILE);
		        } else if (items[item].equals("Cancel")) {
		            dialog.dismiss();
		        }
			}
		});
		builder.show();
	}
	
	public void selectAudio() {
		// TODO Auto-generated method stub
		final CharSequence[] items = { "Record Audio", "Choose from Library",
        "Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(NewMainActivity.this);
		builder.setTitle("Add Audio!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int item) {
		        if (items[item].equals("Record Audio")) {
		        	final Dialog dialog_record = new Dialog(NewMainActivity.this);
		        	dialog_record.setContentView(R.layout.dialog_recording);
		        	dialog_record.setTitle("Record Audio...");
		        	
		        	startTime = 0L;
		        	timeInMilliseconds = 0L;
		        	timeSwapBuff = 0L;
		        	updatedTime = 0L;
		        	
					final TextView file_name=(TextView)dialog_record.findViewById(R.id.recording_audio_name);
					final TextView timer_value=(TextView)dialog_record.findViewById(R.id.timerValue);
					final ImageView mic_image=(ImageView)dialog_record.findViewById(R.id.mic_image);
					final Button start = (Button) dialog_record.findViewById(R.id.btn_record_start);
					final Button save=(Button)dialog_record.findViewById(R.id.btn_audio_save);
//					final Button stop=(Button)dialog_record.findViewById(R.id.btn_stop);
					
					file_name.setText("audio.mp3");
//					stop.setEnabled(false);
//					save.setEnabled(false);
					// if button is clicked, close the custom dialog
					final Runnable updateTimerThread = new Runnable() {

						public void run() {
							
							timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
							
							updatedTime = timeSwapBuff + timeInMilliseconds;

							int secs = (int) (updatedTime / 1000);
							int mins = secs / 60;
							secs = secs % 60;
							int milliseconds = (int) (updatedTime % 1000);
							timer_value.setText("" + mins + ":"
									+ String.format("%02d", secs) + ":"
									+ String.format("%03d", milliseconds));
							customHandler.postDelayed(this, 0);
						}

					};
					
					start.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {							
							startTime = SystemClock.uptimeMillis();							
							customHandler.postDelayed(updateTimerThread, 0);
							start.setEnabled(false);
//							stop.setEnabled(true);
							mic_image.setImageResource(R.drawable.microphone_recording);
							startRecording();
						}	
					});	
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
					save.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							stopRecording();
							timeSwapBuff += timeInMilliseconds;
							customHandler.removeCallbacks(updateTimerThread);
							dialog_record.dismiss();
							File f = new File(android.os.Environment
		                            .getExternalStorageDirectory()+"/GPS Locater/", "audio.mp3");
							audio=Uri.fromFile(f);
							if (!audio.toString().equals("")) {
								btn_add_audio.setText("Change Audio");
								audio_image.setImageResource(R.drawable.audio1);	
								audio_image.setEnabled(true);
							}
							if (!image.toString().equals("")&&!audio.toString().equals("")) {
				            	btn_send.setEnabled(true);
								btn_send.setAlpha(1.0f);
							}
						}
					});
					dialog_record.show();
		        } else if (items[item].equals("Choose from Library")) {
		        	Intent intent = new Intent();
		     	   	intent.setType("audio/mp3");
		     	   	intent.setAction(Intent.ACTION_GET_CONTENT);
		     	   	startActivityForResult(Intent.createChooser(
		     	   				intent, "Open Audio (mp3) file"), RQS_OPEN_AUDIO_MP3);
		        } else if (items[item].equals("Cancel")) {
		            dialog.dismiss();
		        }
		    }
		});
		builder.show();
	}
	
	private void startRecording() {
		recorder = new MediaRecorder();

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(output_formats);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(getFilename());
		audiofile=getFilename();
		audio=Uri.parse(audiofile);

		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);

		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopRecording() {
		if (null != recorder) {
			recorder.stop();
			recorder.reset();
			recorder.release();
			recorder = null;
		}
	}
	
	private String getFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}

		return (file.getAbsolutePath() + "/" + "audio" + file_exts);
	}
	
	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			Toast.makeText(NewMainActivity.this,
					"Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
		}
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			Toast.makeText(NewMainActivity.this,
					"Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
					.show();
		}
	};
	
	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
	   	    switch (msg.what) 
		    {		    	
		        case SEND_SUCCESS:
		        	if (progress_dg!=null)
			    		progress_dg.dismiss();
		        	sent_view();
//		        	Toast.makeText(NewMainActivity.this, "Mail sent successfully!", Toast.LENGTH_SHORT).show();
		    		break;
		        case SEND_FAILED:
		        	sent_view();
		        	Toast.makeText(NewMainActivity.this, "Mail send failed!", Toast.LENGTH_SHORT).show();
		        	break;
		    }
		}
	};
	protected void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}
}
