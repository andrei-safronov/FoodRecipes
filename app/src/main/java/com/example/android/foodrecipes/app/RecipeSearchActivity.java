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
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import static com.example.android.foodrecipes.app.loaders.RecipesLoaderAdapterFactories.EXTERNAL;

/**
 * Main activity of the app. Allows to search and browse recipes.
 *
 * @author Andrei Safronov
 */
public class RecipeSearchActivity extends ActionBarActivity implements RecipeSearchFragment.Callback {

	protected static final String DESCRIPTION_FRAGMENT_TAG = "DESCFTAG";
	protected static final String DETAIL_FRAGMENT_TAG = "DFTAG";

	protected boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (findViewById(R.id.recipe_detail_container) != null) {
			mTwoPane = true;
			if (savedInstanceState == null) {
				RecipeDetailsFragment fragment = new RecipeDetailsFragment();
				fragment.setArguments(createDetailFragmentArgs(null));

				getSupportFragmentManager().beginTransaction()
						.replace(R.id.recipe_detail_container, fragment, DETAIL_FRAGMENT_TAG)
						.commit();
			}
		} else {
			mTwoPane = false;
			// to remove the gap between the Fragment and the action bar
			getSupportActionBar().setElevation(0f);
		}

		RecipeSearchFragment recipeSearchFragment = new RecipeSearchFragment();
		recipeSearchFragment.setArguments(createDescriptionFragmentArgs());

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_recipes, recipeSearchFragment, DESCRIPTION_FRAGMENT_TAG)
				.commit();
	}

	protected Bundle createDescriptionFragmentArgs() {
		Bundle result = new Bundle();
		// operating in two pane mode or not
		result.putBoolean(RecipeSearchFragment.AUTO_LOAD_FIRST_RECIPE, mTwoPane);
		// should load additional data on scroll to the bottom of the list
		result.putBoolean(RecipeSearchFragment.ENABLE_LOAD_ON_SCROLL, true);
		// show the recipe search query input, thus allowing to execute recipe search
		result.putBoolean(RecipeSearchFragment.SHOW_SEARCH_QUERY_INPUT, true);
		// use external loader which takes data from the internet
		result.putSerializable(RecipeSearchFragment.LOADER_ADAPTER_FACTORY, EXTERNAL);
		// the key to get last shown recipe id from shared preferences
		result.putString(RecipeSearchFragment.LAST_SHOWN_RECIPE_ID_KEY_NAME,
				"LAST_SHOWN_RECIPE_ID_SEARCH");
		return result;
	}

	protected Bundle createDetailFragmentArgs(String recipeId) {
		Bundle result = new Bundle();
		// use external loader which takes data from the internet
		result.putSerializable(RecipeDetailsFragment.LOADER_ADAPTER_FACTORY, EXTERNAL);
		// set the recipe id to show
		if (recipeId != null)
			result.putString(RecipeDetailsFragment.RECIPE_EXTERNAL_ID, recipeId);

		// operating in two pane mode or not
		result.putBoolean(RecipeDetailsFragment.TWO_PANE, mTwoPane);

		return result;
	}

	protected void startDetailActivity(String recipeId) {
		// show the recipe search query input, thus allowing to execute recipe search
		// set the recipe id to show
		Intent intent = new Intent(this, RecipeDetailsActivity.class)
				.putExtra(RecipeDetailsFragment.RECIPE_EXTERNAL_ID, recipeId)
				.putExtra(RecipeDetailsFragment.LOADER_ADAPTER_FACTORY, EXTERNAL);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search_recipes_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings :
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
			case R.id.action_favourites:
				startActivity(new Intent(this, FavoriteRecipesActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onItemSelected(String recipeId) {
		if (mTwoPane) {
			RecipeDetailsFragment fragment = new RecipeDetailsFragment();
			fragment.setArguments(createDetailFragmentArgs(recipeId));

			getSupportFragmentManager().beginTransaction()
					.replace(R.id.recipe_detail_container, fragment, DETAIL_FRAGMENT_TAG)
					.commit();
		} else {
			startDetailActivity(recipeId);
		}
	}
}
