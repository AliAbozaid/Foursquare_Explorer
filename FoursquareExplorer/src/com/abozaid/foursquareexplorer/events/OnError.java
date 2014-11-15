package com.abozaid.foursquareexplorer.events;

public class OnError {
	String error;
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public OnError(String error)
	{
		this.error=error;
	}

}
