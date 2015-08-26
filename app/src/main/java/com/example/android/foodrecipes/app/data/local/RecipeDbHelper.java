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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.foodrecipes.app.data.local.RecipeContract.RecipeEntry;

public class RecipeDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	static final String DATABASE_NAME = "recipes.db";

	public RecipeDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		String sqlCreateRecipes = "CREATE TABLE " + RecipeEntry.TABLE_NAME + " (" +
				RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				RecipeEntry.COLUMN_EXTERNAL_ID + " TEXT NOT NULL," +
				RecipeEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
				RecipeEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
				RecipeEntry.COLUMN_IMAGE_CONTENT + " BLOB, " +
				RecipeEntry.COLUMN_SOCIAL_RANK + " REAL NOT NULL, " +
				RecipeEntry.COLUMN_SOURCE_URL + " TEXT NOT NULL," +
				RecipeEntry.COLUMN_INGREDIENTS + " TEXT NOT NULL," +
				" UNIQUE (" + RecipeEntry.COLUMN_EXTERNAL_ID + ") ON CONFLICT REPLACE);";

		sqLiteDatabase.execSQL(sqlCreateRecipes);

		// Index the table by the recipe external ids because queries will hit this index.
		// Thus the performance will increase significantly
		String createExternalIdsIndex =
				"CREATE INDEX external_id_idx ON " + RecipeEntry.TABLE_NAME +
						" (" + RecipeEntry.COLUMN_EXTERNAL_ID + ");";

		sqLiteDatabase.execSQL(createExternalIdsIndex);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		// actually, we should migrate the old data to new format, but it's not needed in the case
		// of current application, where the schema is final
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME);
		onCreate(sqLiteDatabase);
	}
}
