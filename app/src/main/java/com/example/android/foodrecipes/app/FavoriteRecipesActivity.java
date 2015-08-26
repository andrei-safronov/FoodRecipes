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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import static com.example.android.foodrecipes.app.loaders.RecipesLoaderAdapterFactories.*;

/**
 * Favorites recipes activity. Overrides the method of {@link Bundle construction} to allow
 * child fragment to use different loaders and parameters.
 *
 * @author Andrei Safronov
 */
public class FavoriteRecipesActivity extends RecipeSearchActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// enable home button on the action bar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected Bundle createDescriptionFragmentArgs() {
		Bundle result = new Bundle();
		result.putBoolean(RecipeSearchFragment.AUTO_LOAD_FIRST_RECIPE, mTwoPane);
		// show all data at once
		result.putBoolean(RecipeSearchFragment.ENABLE_LOAD_ON_SCROLL, false);
		// no search query input string should be shown
		result.putBoolean(RecipeSearchFragment.SHOW_SEARCH_QUERY_INPUT, false);
		// use the loader which fetches all data except the ingredients from the local db
		result.putSerializable(RecipeSearchFragment.LOADER_ADAPTER_FACTORY, LOCAL_BASIC);
		// the key to get last shown recipe id from shared preferences
		result.putString(RecipeSearchFragment.LAST_SHOWN_RECIPE_ID_KEY_NAME,
				"LAST_SHOWN_RECIPE_ID_FAVORITES");
		return result;
	}

	@Override
	protected Bundle createDetailFragmentArgs(String recipeId) {
		Bundle result = new Bundle();
		result.putBoolean(RecipeDetailsFragment.TWO_PANE, mTwoPane);
		// use the loader which fetches all data from the local db
		result.putSerializable(RecipeDetailsFragment.LOADER_ADAPTER_FACTORY, LOCAL_EXTENDED);
		// the "Remove from favorites" button will be shown
		result.putBoolean(RecipeDetailsFragment.EXECUTE_RECIPE_REMOVE, true);
		// id of the recipe to show
		if (recipeId != null)
			result.putString(RecipeDetailsFragment.RECIPE_EXTERNAL_ID, recipeId);

		return result;
	}

	@Override
	protected void startDetailActivity(String recipeId) {
		// use the loader which fetches all data from the local db
		// the "Remove from favorites" button will be shown
		// id of the recipe to show
		Intent intent = new Intent(this, RecipeDetailsActivity.class)
				.putExtra(RecipeDetailsFragment.RECIPE_EXTERNAL_ID, recipeId)
				.putExtra(RecipeDetailsFragment.LOADER_ADAPTER_FACTORY, LOCAL_EXTENDED)
				.putExtra(RecipeDetailsFragment.EXECUTE_RECIPE_REMOVE, true);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true; //no menu
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
