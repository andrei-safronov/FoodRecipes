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
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;

import com.example.android.foodrecipes.app.common.Recipe;
import com.example.android.foodrecipes.app.common.RecipesLoadResult;
import com.example.android.foodrecipes.app.data.external.RecipeExternalProvider;

import java.util.List;

/**
 * External loader adapter implementation. Loads data from the Food2Work.
 *
 * @author Andrei Safronov
 */
public class ExternalRecipesLoaderAdapter extends RecipesLoaderAdapter {

	private ExtLoader loader;

	@Override
	public void startLoading() {
		if (query != null && page != null) {
			loader.setQueryAndPage(query, page);
		} else {
			loader.setRecipeId(recipeId);
		}
		loader.forceLoad();
	}

	@Override
	public void initLoader(int id, Activity activity, Bundle savedInstanceState,
						   LoaderManager.LoaderCallbacks<RecipesLoadResult> loaderCallback) {
		super.initLoader(id, activity, savedInstanceState, loaderCallback);
		LoaderManager manager = activity.getLoaderManager();
		loader = (ExtLoader) manager.initLoader(id, savedInstanceState, loaderCallback);
	}

	@Override
	public Loader<RecipesLoadResult> createLoader() {
		return new ExtLoader(activity);
	}

	@Override
	public void cancelLoading() {
		if (loader != null)
			loader.cancelLoad();
	}

	private static class ExtLoader extends AsyncTaskLoader<RecipesLoadResult> {

		private String recipeId;
		private String query;
		private Integer page;

		public ExtLoader(Context context) {
			super(context);
		}

		@Override
		protected void onStartLoading() {
			forceLoad();
		}

		@Override
		public RecipesLoadResult loadInBackground() {
			try {
				if (recipeId != null) {
					Recipe recipe = RecipeExternalProvider.getRecipe(recipeId);
					return new RecipesLoadResult(recipe);
				} else if (query != null && page != null) {
					List<Recipe> recipes = RecipeExternalProvider.getRecipes(query, page);
					return new RecipesLoadResult(recipes);
				} else {
					throw new IllegalStateException("Cannot load recipes because no parameters are set");
				}

			} catch (Exception e) {
				return new RecipesLoadResult(e);
			}
		}

		void setRecipeId(String recipeId) {
			this.recipeId = recipeId;
			this.query = null;
			this.page = null;
		}

		void setQueryAndPage(String query, int page) {
			this.query = query;
			this.page = page;
			this.recipeId = null;
		}
	}
}
