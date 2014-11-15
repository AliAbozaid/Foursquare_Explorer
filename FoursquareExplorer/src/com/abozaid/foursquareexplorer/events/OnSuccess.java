package com.abozaid.foursquareexplorer.events;

import com.abozaid.foursquareexplorer.model.FoursquareSearch.response;

public class OnSuccess {
	response success;
	public response getSuccess() {
		return success;
	}
	public void setSuccess(response success) {
		this.success = success;
	}
	public OnSuccess(response success){
		this.success=success;
	}
}
