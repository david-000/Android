package ueda.social.gps_locater;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GPSTracker extends Service implements LocationListener {

	private final Context context;
	
	boolean isGPSenabled=false;
	boolean canGetlocation=false;
	boolean isNetworkenabled=false;
	
	double latitude;
	double longitude;
	
	Location location;
	
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES=10;
	private static final long MIN_TIME_BW_UPDATES=1000*60;
	
	protected LocationManager locationmanager;
	
	public GPSTracker(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
		getLocation();
	}

	public Location getLocation(){
		try {
			locationmanager=(LocationManager)context.getSystemService(LOCATION_SERVICE);
			isGPSenabled=locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			isNetworkenabled=locationmanager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (!isGPSenabled&&!isNetworkenabled) {
				
			}else{
				this.canGetlocation=true;
				
				if (isNetworkenabled) {
					locationmanager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES, 
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);				
					if (locationmanager!=null) {
						location=locationmanager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						
						if (location!=null) {
							latitude=location.getLatitude();
							longitude=location.getLongitude();
						}
					}
				}
				if (isGPSenabled) {
					if (location==null) {
						locationmanager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER, 
								MIN_TIME_BW_UPDATES, 
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						if (locationmanager!=null) {
							location=locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if(location!=null){
								latitude=location.getLatitude();
								longitude=location.getLongitude();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return location;
	}
	
	public void stopUsingGPS(){
		if (locationmanager!=null) {
			locationmanager.removeUpdates(GPSTracker.this);
		}
	}
	
	public double getLatitude(){
		if (location!=null) {
			latitude=location.getLatitude();
		}
		return latitude;
	}
	
	public double getLongitude(){
		if (location!=null) {
			longitude=location.getLongitude();
		}
		return longitude;
	}
	
	public boolean canGetLocation(){
		return this.canGetlocation;
	}
	
	public void showSettingAlert(){
		AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
		alertDialog.setTitle("GPS is settings");
		alertDialog.setMessage("GPS is not enabled.Please set GPS enabled");
		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		alertDialog.show();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
