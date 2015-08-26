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

import com.example.android.foodrecipes.app.common.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Extracts the {@link Recipe} objects from the JSON
 *
 * @author Andrei Safronov
 */
class JsonParser {

	static List<Recipe> parseRecipes(String json) throws JSONException {
		JSONObject root = new JSONObject(json);
		JSONArray recipesArray = root.getJSONArray("recipes");
		List<Recipe> result = new ArrayList<>(recipesArray.length());
		for (int i = 0; i < recipesArray.length(); i++) {
			JSONObject descriptionJson = recipesArray.getJSONObject(i);
			Recipe description = parseBasicRecipeData(descriptionJson);
			result.add(description);
		}
		return result;
	}

	static Recipe parseRecipe(String json) throws JSONException {
		JSONObject root = new JSONObject(json);
		JSONObject recipe = root.getJSONObject("recipe");
		Recipe result = parseBasicRecipeData(recipe);
		result.setSourceUrl(recipe.getString("source_url"));
		result.setIngredients(parseIngredients(recipe.getJSONArray("ingredients")));
		return result;
	}

	private static Recipe parseBasicRecipeData(JSONObject json) throws JSONException {
		Recipe description = new Recipe();
		description.setExternalId(json.getString("recipe_id"));
		description.setTitle(json.getString("title"));
		description.setImageUrl(json.getString("image_url"));
		description.setSocialRank(json.getDouble("social_rank"));
		return description;
	}

	private static List<String> parseIngredients(JSONArray ingredients) throws JSONException {
		List<String> result = new ArrayList<>(ingredients.length());
		for (int i = 0; i < ingredients.length(); i++)
			result.add((String) ingredients.get(i));

		return result;
	}
}
