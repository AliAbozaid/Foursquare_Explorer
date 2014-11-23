package com.abozaid.foursquareexplorer.jobs;

import java.util.Map;

import retrofit.RetrofitError;
import android.content.Context;
import android.widget.Toast;

import com.abozaid.foursquareexplorer.events.OnActionDone;
import com.abozaid.foursquareexplorer.events.OnError;
import com.abozaid.foursquareexplorer.jobs.Controller.AddCheck_in;
import com.abozaid.foursquareexplorer.model.FoursquareAddCheckIn;
import com.abozaid.foursquareexplorer.singleton.RetrofitSingleton;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import de.greenrobot.event.EventBus;

@SuppressWarnings("serial")
public class FoursquarecheckInJob extends Job{
	Map<String, String> CheckInData;
	Context context;
	public FoursquarecheckInJob(Map<String, String> CheckInData,Context context,int PRIORITY) {
		super(new Params(PRIORITY).requireNetwork());
		// TODO Auto-generated constructor stub
		this.CheckInData = CheckInData;
		this.context = context;
	}

	@Override
	public void onAdded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRun() throws Throwable {
		// TODO Auto-generated method stub
		//Initiate request
		AddCheck_in retrofitcheckIn = RetrofitSingleton.getInstance().create(AddCheck_in.class);
		try {
			//send information to make check_in using retrofit
			FoursquareAddCheckIn checkInResponse = retrofitcheckIn.AddCheck_in(CheckInData);
			
			//check if request is true
			if(checkInResponse.meta.code!=200)
			{
				EventBus.getDefault().post(new OnError("error"));
			}
			else{
				
				EventBus.getDefault().post(new OnActionDone("success"));
			}
			

		} catch (RetrofitError e) {
			
			Toast.makeText(context, e.getResponse().getStatus(), Toast.LENGTH_LONG).show();
			
		}
		
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
