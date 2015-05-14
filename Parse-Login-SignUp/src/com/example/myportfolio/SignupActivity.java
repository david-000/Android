package com.example.myportfolio;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends Activity{

	private EditText sign_user, sign_pass, sign_confirm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.sign);
		
		sign_user = (EditText)this.findViewById(R.id.sign_user);
		sign_pass = (EditText)this.findViewById(R.id.sign_pass);
		sign_confirm = (EditText)this.findViewById(R.id.sign_confirm);
		
		Button signup_btn = (Button)this.findViewById(R.id.signup_btn);
				
		signup_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String new_username = sign_user.getText().toString();
				String new_password = sign_pass.getText().toString();
				String new_confirm = sign_confirm.getText().toString();
				if(new_password.equals(new_confirm)){
					ParseUser user = new ParseUser();
					
//					if(!username.equals(new_username)){
						
						user.setUsername(new_username);
						user.setPassword(new_password);

						Log.d("username", new_username);
						
						user.signUpInBackground(new SignUpCallback() {
		
							@Override
							public void done(ParseException e) {
								if(e == null){
									Intent i = new Intent(SignupActivity.this, LoginActivity.class);
									startActivity(i);
								}else{
									
								}
							}
						});
//					}else if(username.equals(new_username)){
//						Toast.makeText(SignupActivity.this, "The user already exists!", Toast.LENGTH_SHORT).show();
//					}
				}else{
					Toast.makeText(SignupActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

}
