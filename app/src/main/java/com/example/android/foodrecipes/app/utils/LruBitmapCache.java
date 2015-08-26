/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.foodrecipes.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * Implementation of LRU-based {@link ImageCache} for {@code ImageLoader} from the {@code Volley}
 * library.
 *
 * @author Andrei Safronov
 */
public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageCache {

	public LruBitmapCache(Context context) {
		super(getDefaultCacheSize(context));
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	public Bitmap getBitmap(String url) {
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}

	/**
	 * Returns the cache size which fits roughly 3 screens of images
	 *
	 * @param ctx Context to get access to resources
	 * @return the cache size which fits roughly 3 screens of images
	 */
	public static int getDefaultCacheSize(Context ctx) {
		DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
		int screen = displayMetrics.widthPixels * displayMetrics.heightPixels * 4; //4 bytes per px
		return screen * 3; //3 screens
	}
}
