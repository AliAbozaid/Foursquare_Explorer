package com.abozaid.foursquareexplorer.jobs;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.http.QueryMap;

import com.abozaid.foursquareexplorer.model.FoursquareAddCheckIn;
import com.abozaid.foursquareexplorer.model.FoursquareSearch;

public class Controller {

	/*
	 * sync request to get nearest place
	 */
	public interface getNearPlace {
		@GET("/venues/search?filters[0][operator]=equals")
		FoursquareSearch getNearPlace(@Query("ll") String ll,
				@Query("oauth_token") String oauth_token, @Query("v") String v,
				@Query("radius") String radius);
	}

	/*
	 * sync request to check_in
	 */
	public interface AddCheck_in {
		@POST("/checkins/add")
		FoursquareAddCheckIn AddCheck_in(@QueryMap Map<String, String> checkIn);

	}

}
