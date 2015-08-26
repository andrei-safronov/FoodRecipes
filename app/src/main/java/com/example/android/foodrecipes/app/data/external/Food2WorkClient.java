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
package com.example.android.foodrecipes.app.data.external;

import android.net.Uri;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.example.android.foodrecipes.app.AppController;

import java.util.concurrent.ExecutionException;

/**
 * Communicated with Food2Work service and returns the result JSON.
 *
 * Supported API calls:
 * <p></p>
 * Find recipe descriptions by ingredients:
 * http://food2fork.com/api/search?key=key&q=tomato,chicken,lime,mayo&page=3
 *
 * <p></p>
 * Get recipe by id:
 * http://food2fork.com/api/get?key=key&rId=42055
 *
 * @author Andrei Safronov
 */
class Food2WorkClient {

	private static final String LOG_TAG = Food2WorkClient.class.getSimpleName();

	private static final String URL_BASE_SEARCH = "http://food2fork.com/api/search";
	private static final String URL_BASE_GET = "http://food2fork.com/api/get";
	private static final String API_KEY = "89f19052b6769262b3722353f98a0c39";

	static String getRecipesJson(String query, int page) throws Exception {
		Uri builtUri = Uri.parse(URL_BASE_SEARCH).buildUpon()
				.appendQueryParameter("key", API_KEY)
				.appendQueryParameter("q", query)
				.appendQueryParameter("page", String.valueOf(page))
				.build();

		return readDataFromUrlConnection(builtUri.toString());
	}

	static String getRecipeJson(String recipeId) throws Exception {
		Uri builtUri = Uri.parse(URL_BASE_GET).buildUpon()
				.appendQueryParameter("key", API_KEY)
				.appendQueryParameter("rId", recipeId)
				.build();

		return readDataFromUrlConnection(builtUri.toString());
	}

	static String readDataFromUrlConnection(String url) throws Exception {
		RequestFuture<String> future = RequestFuture.newFuture();
		StringRequest request = new StringRequest(Request.Method.GET, url, future, future);
		request.setRetryPolicy(new DefaultRetryPolicy(
				(int) AppController.CONNECTION_TIMEOUT, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		AppController.getInstance().addToRequestQueue(request);
		try {
			return future.get();
		} catch (ExecutionException | InterruptedException e) {
			Log.e(LOG_TAG, "Cannot get json from Food2Work", e);
			throw e;
		}
	}
}
