package com.abozaid.foursquareexplorer.jobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit.RetrofitError;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.abozaid.foursquareexplorer.events.OnError;
import com.abozaid.foursquareexplorer.events.OnSuccess;
import com.abozaid.foursquareexplorer.jobs.Controller.getNearPlace;
import com.abozaid.foursquareexplorer.model.FoursquareSearch;
import com.abozaid.foursquareexplorer.singleton.MemoryCacheSingleton;
import com.abozaid.foursquareexplorer.singleton.RetrofitSingleton;
import com.abozaid.foursquaretest.R;
import com.google.gson.Gson;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import de.greenrobot.event.EventBus;

@SuppressWarnings("serial")
public class FoursquareSearchJob extends Job {
	String ll,oauth_token,v,radius;
	
	URL location;
	InputStream is;
	Bitmap returnedBMP;
	Context context;
	Bitmap venueImage;
	final String TAG = "MyTag";
	
	public FoursquareSearchJob(String ll,String oauth_token, String v, String radius,Context con, int PRIORITY) {
		super(new Params(PRIORITY).requireNetwork());
		// TODO Auto-generated constructor stub
		this.ll =ll;
		this.oauth_token = oauth_token;
		this.v =v;
		this.radius = radius;
		this.context = con;
	}

	@Override
	public void onAdded() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRun() throws Throwable {
		// TODO Auto-generated method stub
		
		//Initiate request
		getNearPlace retrofitNearPlaceList = RetrofitSingleton.getInstance().create(getNearPlace.class);
		try 
		{
			//get data nearest venues from server using retrofit
			FoursquareSearch re = retrofitNearPlaceList.getNearPlace(ll, oauth_token, v, radius);
			if(re.meta.code!=200)
			{
				
				EventBus.getDefault().post(new OnError("error"));
			}
			else
			{
				
				Gson gson = new Gson();
				String strinResponse = gson.toJson(re.response);
				//cache data in file
				write("cached", strinResponse);
				//for loop to get image from server and cache it in memory
				for(int i = 0;i<re.response.venues.size();i++)
				{
					try 
					{
						//check if image is already saved in memory
						if(MemoryCacheSingleton.getBitmapFromMemCache(re.response.venues.get(i).categories.get(0).id)!=null)
						{
							continue;
						}
						else
						{
							//save image in memory using Lru cache 
							venueImage =  DownloadBMP(re.response.venues.get(i).categories.get(0).icon.prefix+"bg_88"+re.response.venues.get(i).categories.get(0).icon.suffix);
							MemoryCacheSingleton.addBitmapToMemoryCache(re.response.venues.get(i).categories.get(0).id,venueImage);
							//save image in external
							storeImage(venueImage, re.response.venues.get(i).categories.get(0).id);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//make the default image if he can't get image from server
						re.response.venues.get(i).bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker);
						continue;
					}					
				}
				EventBus.getDefault().post(new OnSuccess (re.response));
			}
				
			
		} catch (RetrofitError e) 
		{
			Toast.makeText(context, e.getResponse().getStatus(), Toast.LENGTH_LONG).show();
		}
	}
	//method to cache data in file 
	public void write(String fname, String re) {
		try {
			FileOutputStream fos = context.openFileOutput(fname,
					Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(re);
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//method to save image in internal or external storage
	private void storeImage(Bitmap image,String imageName) {
	    File pictureFile = getOutputMediaFile(imageName);
	    if (pictureFile == null) {
	        Log.d(TAG,"Error creating media file, check storage permissions: ");// e.getMessage());
	        return;
	    } 
	    try {
	        FileOutputStream fos = new FileOutputStream(pictureFile);
	        image.compress(Bitmap.CompressFormat.PNG, 90, fos);
	        fos.close();
	    } catch (FileNotFoundException e) {
	        Log.d(TAG, "File not found: " + e.getMessage());
	    } catch (IOException e) {
	        Log.d(TAG, "Error accessing file: " + e.getMessage());
	    }  
	}
	//method to create file to save image on it
	private  File getOutputMediaFile(String imageName){
		//create folder with name FoursquareAPI
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
				+ "/FoursquareAPI");

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            return null;
	        }
	    } 
	    File mediaFile;
	        String mImageName= imageName +".png";
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);  
	    return mediaFile;
	} 
	
	//method to get image from server
	private Bitmap DownloadBMP(String url)  
	{
		try
		{
			returnedBMP =BitmapFactory.decodeStream((InputStream)new URL(url.toString()).getContent());
		} catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	        //returns the downloaded bitmap  
	        return returnedBMP;
	}

	
	@Override
	protected void onCancel() {
		// TODO Auto-generated method stub	
	}
		

	@Override
	protected boolean shouldReRunOnThrowable(Throwable throwable) {
		// TODO Auto-generated method stub
		return false;
	}

}
