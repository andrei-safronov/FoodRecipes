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

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class RecipeContentProvider extends ContentProvider {

	private static final UriMatcher sUriMatcher = buildUriMatcher();
	private RecipeDbHelper mOpenHelper;

	static final int RECIPE = 100;

	static UriMatcher buildUriMatcher() {
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		matcher.addURI(RecipeContract.CONTENT_AUTHORITY, RecipeContract.PATH_RECIPE, RECIPE);
		return matcher;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new RecipeDbHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		int match = sUriMatcher.match(uri);
		if (match == RECIPE) {
			return RecipeContract.RecipeEntry.CONTENT_TYPE;
		} else {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
						String sortOrder) {
		Cursor retCursor;
		int match = sUriMatcher.match(uri);
		if (match == RECIPE) {
			retCursor = mOpenHelper.getReadableDatabase().query(
					RecipeContract.RecipeEntry.TABLE_NAME,
					projection,
					selection,
					selectionArgs,
					null,
					null,
					sortOrder
			);
		} else {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		retCursor.setNotificationUri(getContext().getContentResolver(), uri);
		return retCursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int match = sUriMatcher.match(uri);
		Uri returnUri;

		if (match == RECIPE) {
			long id = db.insert(RecipeContract.RecipeEntry.TABLE_NAME, null, values);
			if (id > 0) {
				returnUri = RecipeContract.RecipeEntry.buildUriWithRecipeId(id);
			} else {
				throw new SQLException("Failed to insert row into " + uri);
			}
		} else {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return returnUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int rowsDeleted;
		// this makes delete all rows return the number of rows deleted
		if (selection == null)
			selection = "1";

		int match = sUriMatcher.match(uri);
		if (match == RECIPE) {
			rowsDeleted = db.delete(RecipeContract.RecipeEntry.TABLE_NAME, selection, selectionArgs);
		} else {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		if (rowsDeleted != 0)
			getContext().getContentResolver().notifyChange(uri, null);

		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int match = sUriMatcher.match(uri);
		int rowsUpdated;
		if (match == RECIPE) {
			rowsUpdated = db.update(RecipeContract.RecipeEntry.TABLE_NAME, values, selection, selectionArgs);
		} else {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		if (rowsUpdated != 0)
			getContext().getContentResolver().notifyChange(uri, null);

		return rowsUpdated;
	}

	@Override
	public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int match = sUriMatcher.match(uri);
		if (match == RECIPE) {
			db.beginTransaction();
			int returnCount = 0;
			try {
				for (ContentValues value : values) {
					long id = db.insert(RecipeContract.RecipeEntry.TABLE_NAME, null, value);
					if (id != -1)
						++returnCount;
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			getContext().getContentResolver().notifyChange(uri, null);
			return returnCount;
		} else {
			return super.bulkInsert(uri, values);
		}
	}

	// This is a method specifically to assist the testing framework in running smoothly.
	// More: http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
	@Override
	@TargetApi(11)
	public void shutdown() {
		mOpenHelper.close();
		super.shutdown();
	}
}