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

import java.util.List;

/**
 * Public external data provider. Allows to get the list of recipes by query string and the
 * specific recipe by id.
 *
 * @author Andrei Safronov
 */
public class RecipeExternalProvider {

	/**
	 * Maximum number of returned recipes in the page
	 */
	public static final int MAX_RECIPES_PER_PAGE = 30;

	public static List<Recipe> getRecipes(String query, int page) {
		try {
			String recipeString = RecipeQueryStringCreator.createRecipeString(query);
			String json = Food2WorkClient.getRecipesJson(recipeString, page);
			return JsonParser.parseRecipes(json);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static Recipe getRecipe(String recipeId) {
		try {
			String json = Food2WorkClient.getRecipeJson(recipeId);
			return JsonParser.parseRecipe(json);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
