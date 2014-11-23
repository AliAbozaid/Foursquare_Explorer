package com.abozaid.foursquareexplorer.singleton;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

@SuppressLint("NewApi")
public class MemoryCacheSingleton {
	static int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	static int cacheSize = maxMemory / 8;
	private static LruCache<String, Bitmap> mMemoryCache;

	private MemoryCacheSingleton() {

	}

	public static LruCache<String, Bitmap> getInstance() {
		if (mMemoryCache == null) {
			mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					// The cache size will be measured in kilobytes rather than
					// number of items.
					return bitmap.getByteCount() / 1024;
				}
			};
		}
		return mMemoryCache;
	}
	//method to add image to memory cache
	public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			MemoryCacheSingleton.getInstance().put(key, bitmap);
		}
	}
	//method to check if image is already saved
	public static Bitmap getBitmapFromMemCache(String key) {
		return MemoryCacheSingleton.getInstance().get(key);
	}
}
