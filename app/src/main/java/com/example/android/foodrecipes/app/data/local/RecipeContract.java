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
package com.example.android.foodrecipes.app.data.local;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

public class RecipeContract {

	// The "Content authority" is a name for the entire content provider, similar to the
	// relationship between a domain name and its website.  A convenient string to use for the
	// content authority is the package name for the app, which is guaranteed to be unique on the
	// device.
	public static final String CONTENT_AUTHORITY = "com.example.android.foodrecipes.app";

	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	public static final String PATH_RECIPE = "recipe";

	public static final class RecipeEntry implements BaseColumns {

		public static final Uri CONTENT_URI =
				BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE).build();

		public static final String CONTENT_TYPE =
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE;

		public static final String TABLE_NAME = "recipe";

		public static final String COLUMN_EXTERNAL_ID = "external_id";
		public static final String COLUMN_TITLE = "title";
		public static final String COLUMN_IMAGE_URL = "image_url";
		public static final String COLUMN_IMAGE_CONTENT = "image_content";
		public static final String COLUMN_SOCIAL_RANK = "social_rank";
		public static final String COLUMN_SOURCE_URL = "source_url";
		public static final String COLUMN_INGREDIENTS = "ingredients";

		public static final String INGREDIENTS_DELIMITER = ";;;";

		public static Uri buildUriWithRecipeId(long id) {
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}

		public static String buildIngredientsString(List<String> ingredients) {
			return TextUtils.join(INGREDIENTS_DELIMITER, ingredients);
		}

		public static List<String> parseIngredients(String ingredients) {
			return Arrays.asList(TextUtils.split(ingredients, INGREDIENTS_DELIMITER));
		}
	}
}
