package com.abozaid.foursquareexplorer.model;

public class FoursquareAddCheckIn {
	public response response;
	public meta meta;

	public class meta {
		public int code;
	}

	public class response {
		public checkin checkin;

		public class checkin {
			public String id;
			public String createdAt;
			public String type;
			public String timeZoneOffset;

			public user user;
			public venue venue;

			public class user {
				public String id;
				public String firstName;
				public String lastName;
				public String gender;
				public String relationship;
				public photo photo;

				public class photo {
					public String prefix;
					public String suffix;
				}
			}

			public class venue {
				public String id;
				public String name;

				public location location;

				public class location {
					public String address;
					public String lat;
					public String lng;
					public String cc;
					public String city;
					public String state;
					public String country;

				}
			}
		}
	}
}
