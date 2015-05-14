package com.example.myportfolio;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity{

	TextView sign;
	EditText login_username, login_password;
	Button login_btn;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.login);

		login_username = (EditText)this.findViewById(R.id.login_username);
		login_password = (EditText)this.findViewById(R.id.login_password);
		login_btn = (Button)this.findViewById(R.id.login_btn);
		
		login_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String username = login_username.getText().toString();
				String password = login_password.getText().toString();
				if(!username.equals("") && !password.equals("")){

					//progress dialog
					progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging..");
					
					ParseUser.logInInBackground(username, password, new LogInCallback() {
						
						@Override
						public void done(ParseUser user, ParseException e) {
							if(user != null){
								progressDialog.dismiss();
								Intent i = new Intent(LoginActivity.this, MainActivity.class);
								startActivity(i);
								
							}else if(user == null){
								progressDialog.dismiss();
								Toast.makeText(LoginActivity.this, "The username and password is invalid !", Toast.LENGTH_SHORT).show();
							}
						}
					});
				}else if(!username.equals("") && password.equals("")){
					Toast.makeText(LoginActivity.this, "Enter password !", Toast.LENGTH_SHORT).show();
				}else if(username.equals("") && !password.equals("")){
					Toast.makeText(LoginActivity.this, "Enter username !", Toast.LENGTH_SHORT).show();
				}else if(username.equals("") && password.equals("")){
					Toast.makeText(LoginActivity.this, "Enter username and password !", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		sign = (TextView)this.findViewById(R.id.txt_sign);
		
		sign.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SpannableString content = new SpannableString("Sign up now");
				content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
				sign.setText(content);
				Intent i = new Intent(LoginActivity.this, SignupActivity.class);
				startActivity(i);
			}
		});
	}

}