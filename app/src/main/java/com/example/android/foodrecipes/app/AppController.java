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
package com.example.android.foodrecipes.app;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.android.foodrecipes.app.utils.LruBitmapCache;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import java.util.concurrent.TimeUnit;

/**
 * Application controller. Contains application-wide objects and settings.
 *
 * @author Andrei Safronov
 */
public class AppController extends Application {

	private static final String RECIPE_REQUESTS_TAG = "RECIPE_REQUESTS";

	/**
	 * Default value for connection timeout used for Volley.
	 * Actually this value is high, but it's ok for the purposes of this app.
	 */
	public static final long CONNECTION_TIMEOUT = TimeUnit.MINUTES.toMillis(5);

	/**
	 * Volley disk cache
	 */
	private static final int MAX_DISK_CACHE_BYTES = 5 * 1024 * 1024;

	/**
	 * Stored instance of the {@code AppController}
	 */
	private static AppController mInstance;

	/**
	 * Volley request queue
	 */
	private RequestQueue mRequestQueue;

	/**
	 * Volley image loader
	 */
	private ImageLoader mImageLoader;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}

	public static AppController getInstance() {
		return mInstance;
	}

	public ImageLoader getImageLoader() {
		initRequestQueueIfNeeded();
		if (mImageLoader == null) {
			LruBitmapCache cache = new LruBitmapCache(getApplicationContext());
			mImageLoader = new ImageLoader(this.mRequestQueue, cache);
		}
		return mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(RECIPE_REQUESTS_TAG);
		initRequestQueueIfNeeded();
		mRequestQueue.add(req);
	}

	public void cancelPendingRecipeRequests() {
		if (mRequestQueue != null)
			mRequestQueue.cancelAll(RECIPE_REQUESTS_TAG);
	}

	private void initRequestQueueIfNeeded() {
		if (mRequestQueue == null) {
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, (int) CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, (int) CONNECTION_TIMEOUT);

			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

			ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
			DefaultHttpClient defaultHttpClient = new DefaultHttpClient(cm, httpParams);

			HttpClientStack httpClientStack = new HttpClientStack(defaultHttpClient);
			mRequestQueue = Volley.newRequestQueue(getApplicationContext(), httpClientStack,
					MAX_DISK_CACHE_BYTES);
		}
	}
}
