package com.abozaid.foursquareexplorer.singleton;

import retrofit.RestAdapter;

public class RetrofitSingleton {
	private static RestAdapter restAdapter = null;

	public RetrofitSingleton() {

	}

	public static RestAdapter getInstance() {
		if (restAdapter == null) {
			restAdapter = new RestAdapter.Builder().setEndpoint(
					"https://api.foursquare.com/v2").build();
		}
		return restAdapter;
	}

}
