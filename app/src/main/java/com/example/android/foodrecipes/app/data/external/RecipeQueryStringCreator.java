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

/**
 * The required input for the Food2Work API is the comma-separated ingredients list.
 * Most probably that's not convenient for the user, because user wants to split ingredients
 * by spaces or maybe some other characters.
 *
 * This class takes the recipe query and changes {@code '.', ',', ';', ':'} delimiters
 * to the {@code ','}.
 *
 * @author Andrei Safronov
 */
class RecipeQueryStringCreator {

	private static final String[] PATTERNS_TO_REPLACE = {"\\.", ",", ";", ":"};
	private static final String SPACE = " ";
	private static final char COMMA = ',';

	static String createRecipeString(String recipeQuery) {
		for (String pattern : PATTERNS_TO_REPLACE)
			recipeQuery = recipeQuery.replaceAll(pattern, SPACE);

		String[] parts = recipeQuery.split(SPACE);
		StringBuilder sb = new StringBuilder();

		for (String part : parts) {
			if (!part.trim().isEmpty())
				sb.append(part).append(COMMA);
		}

		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}
}
