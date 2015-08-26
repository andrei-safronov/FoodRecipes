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

import android.test.AndroidTestCase;

import com.example.android.foodrecipes.app.common.Recipe;

import java.util.List;

/**
 * @author Andrei Safronov
 */
public class TestJsonParser extends AndroidTestCase {

	private static final String RECIPES_JSON =
			"{" +
			"  \"count\": 1," +
			"  \"recipes\": [" +
			"    {" +
			"      \"publisher\": \"All Recipes\"," +
			"      \"f2f_url\": \"http://food2fork.com/view/27251\"," +
			"      \"title\": \"Rudy's Molcajete Mixto\"," +
			"      \"source_url\": \"http://allrecipes.com/Recipe/Rudys-Molcajete-Mixto/Detail.aspx\"," +
			"      \"recipe_id\": \"27251\"," +
			"      \"image_url\": \"http://static.food2fork.com/976037b18a.jpg\"," +
			"      \"social_rank\": 35.286038941529," +
			"      \"publisher_url\": \"http://allrecipes.com\"" +
			"    }" +
			"  ]" +
			"}";

	private static final String RECIPE_JSON =
			"{" +
			"  \"recipe\": {" +
			"    \"publisher\": \"All Recipes\"," +
			"    \"f2f_url\": \"http://food2fork.com/view/27251\"," +
			"    \"ingredients\": [" +
			"      \"1/2 pound thinly sliced carne asada (beef steak)\"," +
			"      \"1/2 pound boneless chicken breast\"," +
			"      \"5 large, shelled shrimp\"," +
			"      \"4 1/2 ounces chorizo (Mexican sausage)\"," +
			"      \"2 nopales (cactus leaves) or zucchini and yellow squash\"," +
			"      \"2 jalapeno peppers\"," +
			"      \"1/4 bunch cilantro\"," +
			"      \"2 green onions\"," +
			"      \"1 large tomato\"," +
			"      \"1/2 teaspoon salt\"," +
			"      \"1/2 teaspoon pepper\"," +
			"      \"1 lime, quartered\"," +
			"      \"1 avocado\"," +
			"      \"1/4 pound queso fresco (Mexican cheese)\"," +
			"      \"1 (16 ounce) jar HERDEZ Salsa Verde\"," +
			"      \"1 (8 ounce) package small tortillas\"," +
			"      \"Stone molcajete (mortar and pestle)\"" +
			"    ]," +
			"    \"source_url\": \"http://allrecipes.com/Recipe/Rudys-Molcajete-Mixto/Detail.aspx\"," +
			"    \"recipe_id\": \"27251\"," +
			"    \"image_url\": \"http://static.food2fork.com/976037b18a.jpg\"," +
			"    \"social_rank\": 35.286038941529," +
			"    \"publisher_url\": \"http://allrecipes.com\"," +
			"    \"title\": \"Rudy's Molcajete Mixto\"" +
			"  }" +
			"}";


	public void testParseRecipes() throws Exception {
		List<Recipe> descriptions = JsonParser.parseRecipes(RECIPES_JSON);
		assertEquals(1, descriptions.size());
		
		Recipe description = descriptions.get(0);
		assertEquals("27251", description.getExternalId());
		assertEquals("http://static.food2fork.com/976037b18a.jpg", description.getImageUrl());
		assertEquals("Rudy's Molcajete Mixto", description.getTitle());
		assertEquals(35.286038941529, description.getSocialRank(), 1E-8);
	}

	public void testParseRecipe() throws Exception {
		Recipe recipe = JsonParser.parseRecipe(RECIPE_JSON);
		assertEquals("27251", recipe.getExternalId());
		assertEquals("http://static.food2fork.com/976037b18a.jpg", recipe.getImageUrl());
		assertEquals("Rudy's Molcajete Mixto", recipe.getTitle());
		assertEquals(35.286038941529, recipe.getSocialRank(), 1E-8);
		assertEquals("http://allrecipes.com/Recipe/Rudys-Molcajete-Mixto/Detail.aspx", recipe.getSourceUrl());

		List<String> ingredients = recipe.getIngredients();
		assertEquals(17, ingredients.size());
		assertTrue(ingredients.contains("1/2 pound thinly sliced carne asada (beef steak)"));
		assertTrue(ingredients.contains("1/2 pound boneless chicken breast"));
		assertTrue(ingredients.contains("5 large, shelled shrimp"));
		assertTrue(ingredients.contains("4 1/2 ounces chorizo (Mexican sausage)"));
		assertTrue(ingredients.contains("2 nopales (cactus leaves) or zucchini and yellow squash"));
		assertTrue(ingredients.contains("2 jalapeno peppers"));
		assertTrue(ingredients.contains("1/4 bunch cilantro"));
		assertTrue(ingredients.contains("2 green onions"));
		assertTrue(ingredients.contains("1 large tomato"));
		assertTrue(ingredients.contains("1/2 teaspoon salt"));
		assertTrue(ingredients.contains("1/2 teaspoon pepper"));
		assertTrue(ingredients.contains("1 lime, quartered"));
		assertTrue(ingredients.contains("1 avocado"));
		assertTrue(ingredients.contains("1/4 pound queso fresco (Mexican cheese)"));
		assertTrue(ingredients.contains("1 (16 ounce) jar HERDEZ Salsa Verde"));
		assertTrue(ingredients.contains("1 (8 ounce) package small tortillas"));
		assertTrue(ingredients.contains("Stone molcajete (mortar and pestle)"));
	}
}
