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
package com.example.android.foodrecipes.app.loaders;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;

import com.example.android.foodrecipes.app.common.RecipesLoadResult;

/**
 * As we need the same activity to be working with 2 different loaders, which have different
 * interfaces, we should have own adapter layer which will encapsulate all internal differences.
 *
 * This is the base class for such adapters.
 *
 * @author Andrei Safronov
 */
public abstract class RecipesLoaderAdapter {

	/**
	 * Recipe query to load
	 */
	protected String query;

	/**
	 * Page to load
	 */
	protected Integer page;

	/**
	 * Exact recipe id to load the recipe
	 */
	protected String recipeId;

	/**
	 * Activity to provide the access to resources
	 */
	protected Activity activity;

	/**
	 * Initiates the background loading
	 */
	public abstract void startLoading();

	/**
	 * Creates the loader to populate views
	 *
	 * @return Newly created loader
	 */
	public abstract Loader<RecipesLoadResult> createLoader();

	/**
	 * Cancels current loading if it exists
	 */
	public abstract void cancelLoading();

	/**
	 * Init loader with given {@code id} and {@code loaderCallback}
	 *
	 * @param activity Activity to init loader with
	 * @param savedInstanceState Saved state
	 * @param loaderCallback Callback for the loader
	 */
	public void initLoader(int id, Activity activity, Bundle savedInstanceState,
						   LoaderCallbacks<RecipesLoadResult> loaderCallback) {
		this.activity = activity;
	}

	/**
	 * Stops current loader, thus making it inactive
	 *
	 * @param id Id of the loader to stop
	 */
	public void closeLoader(int id) {
		if (activity != null) {
			LoaderManager manager = activity.getLoaderManager();
			manager.destroyLoader(id);
		}
	}

	/**
	 * Sets the recipe query and page number to load. Should be called before
	 * {@link RecipesLoaderAdapter#startLoading()}.
	 *
	 * Also this method nullifies the {@link RecipesLoaderAdapter#recipeId} as we should load data
	 * only using the query and page or using the recipe id.
	 *
	 * @param query Recipe query
	 * @param page Page number to load
	 */
	public void setQueryAndPage(String query, Integer page) {
		this.query = query;
		this.page = page;
		recipeId = null;
	}

	/**
	 * Sets the recipe id to load recipe.
	 *
	 * Also this method nullifies the {@link RecipesLoaderAdapter#query} and
	 * {@link RecipesLoaderAdapter#page}as we should load data only using the query and page or
	 * using the recipe id.
	 *
	 * @param recipeId Id of recipe to load
	 */
	public void setRecipeId(String recipeId) {
		this.recipeId = recipeId;
		query = null;
		page = null;
	}
}
