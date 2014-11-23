/*
 * Copyright (C) 2013 Foursquare Labs, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abozaid.foursquareexplorer.singleton;

public class TokenSingleton {
	private static TokenSingleton sInstance;
	private String token;

	private TokenSingleton() {
		// TODO Auto-generated constructor stub
	}

	public static TokenSingleton get() {
		if (sInstance == null) {
			sInstance = new TokenSingleton();
		}

		return sInstance;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}