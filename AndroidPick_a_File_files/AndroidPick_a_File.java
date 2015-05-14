package com.exercise.AndroidPick_a_File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AndroidPick_a_File extends Activity {
	
	TextView textFile, textFileName, textFolder;
	TextView textFileName_WithoutExt, textFileName_Ext;
	
	private static final int PICKFILE_RESULT_CODE = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button buttonPick = (Button)findViewById(R.id.buttonpick);
        textFile = (TextView)findViewById(R.id.textfile);
        textFolder = (TextView)findViewById(R.id.textfolder);
        textFileName = (TextView)findViewById(R.id.textfilename);
        
        textFileName_WithoutExt = (TextView)findViewById(R.id.textfilename_withoutext);
        textFileName_Ext = (TextView)findViewById(R.id.textfilename_ext);
        
        buttonPick.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	            intent.setType("file/*");
			    startActivityForResult(intent,PICKFILE_RESULT_CODE);
				
			}});
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch(requestCode){
		case PICKFILE_RESULT_CODE:
			if(resultCode==RESULT_OK){
				
				String FilePath = data.getData().getPath();
				String FileName = data.getData().getLastPathSegment();
				int lastPos = FilePath.length() - FileName.length();
				String Folder = FilePath.substring(0, lastPos);
				
				textFile.setText("Full Path: \n" + FilePath + "\n");
				textFolder.setText("Folder: \n" + Folder + "\n");
				textFileName.setText("File Name: \n" + FileName + "\n");
				
				filename thisFile = new filename(FileName);
				textFileName_WithoutExt.setText("Filename without Ext: " + thisFile.getFilename_Without_Ext());
		        textFileName_Ext.setText("Ext: " + thisFile.getExt());
				
			}
			break;
			
		}
	}
	
	private class filename{
		
		String filename_Without_Ext = "";
		String ext = "";
		
		filename(String file){
		    int dotposition= file.lastIndexOf(".");
		    filename_Without_Ext = file.substring(0,dotposition);
		    ext = file.substring(dotposition + 1, file.length());  
		}
		
		String getFilename_Without_Ext(){
			return filename_Without_Ext;
		}
		
		String getExt(){
			return ext;
		}
	}

}