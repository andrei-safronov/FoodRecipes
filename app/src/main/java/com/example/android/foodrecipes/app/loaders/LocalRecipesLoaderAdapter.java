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
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import com.example.android.foodrecipes.app.common.RecipesLoadResult;
import com.example.android.foodrecipes.app.data.local.RecipeContract;
import com.example.android.foodrecipes.app.common.Recipe;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.foodrecipes.app.data.local.RecipeContract.RecipeEntry.*;

/**
 * Implementation of adapter of loader which takes data from the local db.
 *
 * @author Andrei Safronov
 */
@SuppressWarnings("unchecked")
public class LocalRecipesLoaderAdapter extends RecipesLoaderAdapter {

	/**
	 * Not including ingredients
	 */
	private static final String[] COLUMNS_BASIC = {
			COLUMN_EXTERNAL_ID,
			COLUMN_TITLE,
			COLUMN_IMAGE_URL,
			COLUMN_IMAGE_CONTENT,
			COLUMN_SOCIAL_RANK
	};

	/**
	 * All columns
	 */
	private static final String[] COLUMNS_EXTENDED = {
			COLUMN_EXTERNAL_ID,
			COLUMN_TITLE,
			COLUMN_IMAGE_URL,
			COLUMN_IMAGE_CONTENT,
			COLUMN_SOCIAL_RANK,
			COLUMN_SOURCE_URL,
			COLUMN_INGREDIENTS
	};

	private static final int EXTERNAL_ID_INDEX = 0;
	private static final int TITLE_INDEX = 1;
	private static final int IMAGE_URL_INDEX = 2;
	private static final int IMAGE_CONTENT_INDEX = 3;
	private static final int SOCIAL_RANK_INDEX = 4;
	private static final int SOURCE_URL_INDEX = 5;
	private static final int INGREDIENTS_INDEX = 6;

	private final boolean loadIngredients;
	private CursorLoader loader;

	public LocalRecipesLoaderAdapter(boolean loadIngredients) {
		this.loadIngredients = loadIngredients;
	}

	@Override
	public void startLoading() {
		loader.forceLoad();
	}

	@Override
	public void initLoader(int id, Activity activity, Bundle savedInstanceState,
						   LoaderManager.LoaderCallbacks<RecipesLoadResult> loaderCallback) {
		super.initLoader(id, activity, savedInstanceState, loaderCallback);
		LoaderManager manager = activity.getLoaderManager();
		DelegatingCallback callback = new DelegatingCallback(loaderCallback);
		loader = (CursorLoader) manager.initLoader(id, savedInstanceState, callback);
	}

	@Override
	public Loader<RecipesLoadResult> createLoader() {
		String[] columns = loadIngredients ? COLUMNS_EXTENDED : COLUMNS_BASIC;
		String sortOrder = RecipeContract.RecipeEntry.COLUMN_TITLE + " ASC";

		String selection = null;
		String[] selectionArgs = null;

		if (recipeId != null) {
			selection = COLUMN_EXTERNAL_ID + "=?";
			selectionArgs = new String[]{recipeId};
		}

		return (Loader)
				new CursorLoader(activity, CONTENT_URI, columns, selection, selectionArgs, sortOrder);
	}

	@Override
	public void cancelLoading() {
		if (loader != null)
			loader.cancelLoad();
	}

	/**
	 * As it would be better to provide the {@link RecipesLoadResult}, but not {@link Cursor}
	 * in the loader callback, this wrapper encapsulates the {@link RecipesLoadResult} construction
	 * from the cursor.
	 */
	private class DelegatingCallback implements LoaderManager.LoaderCallbacks<Cursor> {

		final LoaderManager.LoaderCallbacks<RecipesLoadResult> callback;

		DelegatingCallback(LoaderManager.LoaderCallbacks<RecipesLoadResult> callback) {
			this.callback = callback;
		}

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return (Loader) callback.onCreateLoader(id, args);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			RecipesLoadResult recipes = extractRecipesFromCursor(data);
			callback.onLoadFinished((Loader) loader, recipes);
		}

		private RecipesLoadResult extractRecipesFromCursor(Cursor cursor) {
			List<Recipe> result = new ArrayList<>();
			while (cursor.moveToNext()) {
				Recipe recipe = new Recipe();
				recipe.setExternalId(cursor.getString(EXTERNAL_ID_INDEX));
				recipe.setTitle(cursor.getString(TITLE_INDEX));
				recipe.setImageUrl(cursor.getString(IMAGE_URL_INDEX));
				recipe.setImageBytes(cursor.getBlob(IMAGE_CONTENT_INDEX));
				recipe.setSocialRank(cursor.getDouble(SOCIAL_RANK_INDEX));

				if (loadIngredients) {
					recipe.setSourceUrl(cursor.getString(SOURCE_URL_INDEX));
					recipe.setIngredients(parseIngredients(cursor.getString(INGREDIENTS_INDEX)));
				}

				result.add(recipe);
			}

			return new RecipesLoadResult(result);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			callback.onLoaderReset((Loader) loader);
		}
	}
}
