package com.abozaid.foursquareexplorer.events;

public class OnActionDone {
	String done;
	public String getDone() {
		return done;
	}
	public void setDone(String done) {
		this.done = done;
	}
	public OnActionDone(String done){
		this.done=done;
	}
}
