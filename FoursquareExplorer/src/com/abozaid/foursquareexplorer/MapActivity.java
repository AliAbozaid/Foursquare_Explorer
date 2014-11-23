package com.abozaid.foursquareexplorer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.abozaid.foursquareexplorer.events.OnActionDone;
import com.abozaid.foursquareexplorer.events.OnError;
import com.abozaid.foursquareexplorer.events.OnSuccess;
import com.abozaid.foursquareexplorer.jobs.FoursquareSearchJob;
import com.abozaid.foursquareexplorer.jobs.FoursquarecheckInJob;
import com.abozaid.foursquareexplorer.mapstate.GPSTracker;
import com.abozaid.foursquareexplorer.mapstate.MapStateManager;
import com.abozaid.foursquareexplorer.model.FoursquareSearch.response;
import com.abozaid.foursquareexplorer.singleton.MemoryCacheSingleton;
import com.abozaid.foursquareexplorer.singleton.ReadWriteToFile;
import com.abozaid.foursquaretest.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.path.android.jobqueue.JobManager;

import de.greenrobot.event.EventBus;

@SuppressLint({ "NewApi", "SimpleDateFormat" })
public class MapActivity extends ActionBarActivity implements LocationListener {
	MapView mapView;
	GoogleMap map;
	CameraUpdate cameraViewOnMap;
	Context context;
	double latitude, longitude;
	private JobManager jobManager;
	String v, ll, token, radius ;
	GPSTracker gps;
	static boolean resumeGps;
	Map<String, String> CheckInData;
	SharedPreferences UserInformation;
	final String ACCESS_TOKEN = "accessToken";
	static response savedResponse;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		resumeGps = false;
		//Initialize GPSTracker to get info about gps
		gps = new GPSTracker(this);
		//map initiate an determine it's view
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		map = mapView.getMap();
		map.getUiSettings().setMyLocationButtonEnabled(false);
		map.setMyLocationEnabled(true);
		//Initialize shared prefrence
		UserInformation = getSharedPreferences("userinfo", 0);
		//action to make check_in when user click in venue info
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) 
			{
				// TODO Auto-generated method stub
				// marker.getPosition().latitude;
				Toast.makeText(context,	marker.getPosition().latitude + ","	+ marker.getPosition().longitude,Toast.LENGTH_SHORT).show();
				CheckInData = new HashMap<String, String>();
				CheckInData.put("oauth_token",UserInformation.getString(ACCESS_TOKEN, ""));
				CheckInData.put("venueId",getVenueId(marker.getPosition().latitude,marker.getPosition().longitude));
				CheckInData.put("v",timeMilisToString(System.currentTimeMillis()));
				CheckInData.put("broadcast", "public");
				jobManager.addJobInBackground(new FoursquarecheckInJob(CheckInData, context, 1));
			}
		});
		// to listen to events
		EventBus.getDefault().register(this);
		try
		{
			//used to  to initialize the Google Maps 
			MapsInitializer.initialize(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		//if user ofen his location
		if (gps.canGetLocation()) 
		{
			//call to update venues on map and update camera view
			getLocationOnMap();
		}
		//if user doesn't open his location 
		else 
		{
			resumeGps = true;
			//show dialo to user enable from settings his location 
			gps.showSettingsAlert();
		}
		//read cached data from file
		String cacheString = ReadWriteToFile.read("cached", context);
		//if file is not empty or not exist
		if (cacheString != null)
		{
			Gson gson = new Gson();
			// get data in gson format
			savedResponse = gson.fromJson(cacheString, response.class);
			//show cached data on map
			drawPinOnMap(savedResponse);
			
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume() 
	{
		mapView.onResume();
		super.onResume();
		try
		{
			// update to the latest location
			gps.getLocation();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//if the user open location update view and get venues and focus camera on current state
		if (gps.canGetLocation() && resumeGps) 
		{
			//call to update venues on map and update camera view
			getLocationOnMap();
		}
		//if the user still didn't open location show dialog to him
		else if (!gps.canGetLocation() && resumeGps) {
			resumeGps = true;
			gps.showSettingsAlert();
		}
		//get last state view of map & camera
		MapStateManager mgr = new MapStateManager(this);
		CameraPosition position = mgr.getSavedCameraPosition();
		if (position != null) 
		{
			CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
			map.moveCamera(update);
			
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//save last state of map & camera
		MapStateManager mgr = new MapStateManager(this);
		mgr.saveMapState(map);
	}
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		mapView.onDestroy();
		
	}
	
	@Override
	public void onLowMemory() 
	{
		super.onLowMemory();
		mapView.onLowMemory();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		//update venues and camera when my location change
		getLocationOnMap();
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
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		//close the app when back button press
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	// function to get current location and nearest venues on map and update camera view
	public void getLocationOnMap()
	{
		resumeGps = false;
		// intialize request
		latitude = gps.getLatitude();
		longitude = gps.getLongitude();
		ll = latitude + "," + longitude;
		v = timeMilisToString(System.currentTimeMillis());
		token = UserInformation.getString(ACCESS_TOKEN, "");
		radius = "50";
		// call request(job)
		myjob(ll, token, v, radius);
		cameraViewOnMap = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15);
		map.animateCamera(cameraViewOnMap);
	}
	//function to return venue_id to make check_in in that place
	public String getVenueId(double lat, double lan) {
		for (int i = 0; i < savedResponse.venues.size(); i++) {
			if (savedResponse.venues.get(i).location.lat == lat && savedResponse.venues.get(i).location.lng == lan) 
			{
				return savedResponse.venues.get(i).id;
			}
		}
		return "";
	}
	//function to return current date in yyyyMMdd format
	private String timeMilisToString(long milis) {
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milis);
		return sd.format(calendar.getTime());
	}	
	//function call job to get search request from server
	public void myjob(String ll, String token, String v, String radius) {
		try 
		{
			jobManager = new JobManager(context);
			jobManager.addJobInBackground(new FoursquareSearchJob(ll, token, v,radius, context, 1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//method to get bitmap from internal/external storage
	public Bitmap getImage(String fileName) {
		File f = new File(Environment.getExternalStorageDirectory()
				+ "/FoursquareAPI" + "/" + fileName + ".png");
		Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
		return bmp;
	}
	//method for put pin on map
	public void drawPinOnMap(response respons)
	{
		for (int i = 0; i < savedResponse.venues.size(); i++) 
		{
			try 
			{
				//if images are in memory cache
				if (MemoryCacheSingleton.getBitmapFromMemCache(savedResponse.venues.get(i).categories.get(0).id) != null) 
				{
					map.addMarker(new MarkerOptions().position(new LatLng(savedResponse.venues.get(i).location.lat, savedResponse.venues.get(i).location.lng))
						.flat(false)
						.title(savedResponse.venues.get(i).name)
						.snippet(savedResponse.venues.get(i).categories.get(0).name)
						.icon(BitmapDescriptorFactory.fromBitmap(MemoryCacheSingleton.getBitmapFromMemCache(savedResponse.venues.get(i).categories.get(0).id))));
				} //if images aren't in memory cache
				else 
				{
					map.addMarker(new MarkerOptions().position(new LatLng(savedResponse.venues.get(i).location.lat, savedResponse.venues.get(i).location.lng))
						.flat(false)
						.title(savedResponse.venues.get(i).name)
						.snippet(savedResponse.venues.get(i).categories.get(0).name)
						.icon(BitmapDescriptorFactory.fromBitmap(getImage(savedResponse.venues.get(i).categories.get(0).id))));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//listen to events
	// event to ensure that the user make check_in
		public void onEventMainThread(OnActionDone done) {
			// TODO Auto-generated method stub
			Toast.makeText(context, "check_in success", Toast.LENGTH_LONG).show();
		}

		//event to show data in map after get it from server 
		public void onEventMainThread(OnSuccess success) 
		{
			// TODO Auto-generated method stub
			savedResponse = success.getSuccess();
			map.clear();
			//to show pin on map
			drawPinOnMap(success.getSuccess());
			
		}
		//events to get error
		public void onEventMainThread(OnError error) 
		{
			// TODO Auto-generated method stub
			Toast.makeText(context, "check the network please", Toast.LENGTH_LONG).show();
		}
}
