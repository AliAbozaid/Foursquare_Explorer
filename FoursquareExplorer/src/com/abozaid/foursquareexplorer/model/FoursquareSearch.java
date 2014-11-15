package com.abozaid.foursquareexplorer.model;

import java.util.List;

import android.graphics.Bitmap;

public class FoursquareSearch {
	public response response;
	public meta meta;

	public class meta {
		public int code;
	}

	public class response {
		public List<venues> venues;

		public class venues {
			public String id;
			public String name;
			public location location;
			public List<categories> categories;
			public Bitmap bitmap;

			public class location {
				public double lat;
				public double lng;
				public double distance;
				public String cc;
				public String country;
			}

			public class categories {

				public String id;
				public String name;
				public String pluralName;
				public String shortName;
				public icon icon;

				public class icon {
					public String prefix;
					public String suffix;
				}
			}
		}
	}

}
