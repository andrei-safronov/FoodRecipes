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
import android.view.MenuItem;

import java.io.Serializable;

import static com.example.android.foodrecipes.app.RecipeDetailsFragment.*;

/**
 * Activity which shows the detailed information about the recipe.
 * Actually this activity works only on phones and small screen devices.
 *
 * @author Andrei Safronov
 *
 * @see RecipeSearchActivity#mTwoPane
 */
public class RecipeDetailsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		// enable back button on the action bar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			// get required parameters from intent
			Intent intent = getIntent();
			// the id of recipe to show
			String recipeId = intent.getStringExtra(RECIPE_EXTERNAL_ID);
			// instance of RecipesLoaderAdapter
			Serializable loaderAdapter = intent.getSerializableExtra(LOADER_ADAPTER_FACTORY);
			// flag indicating the fragment should have the "Remove from favorites" button
			boolean isRemove = intent.getBooleanExtra(EXECUTE_RECIPE_REMOVE, false);

			Bundle arguments = new Bundle();
			arguments.putString(RECIPE_EXTERNAL_ID, recipeId);
			arguments.putSerializable(LOADER_ADAPTER_FACTORY, loaderAdapter);
			arguments.putBoolean(EXECUTE_RECIPE_REMOVE, isRemove);

			RecipeDetailsFragment fragment = new RecipeDetailsFragment();
			fragment.setArguments(arguments);

			getSupportFragmentManager().beginTransaction()
					.add(R.id.recipe_detail_container, fragment)
					.commit();
		}
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