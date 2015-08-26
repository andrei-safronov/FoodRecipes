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

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;

import java.util.Arrays;
import java.util.UUID;

import static com.example.android.foodrecipes.app.data.local.RecipeContract.RecipeEntry.*;

public class TestProvider extends AndroidTestCase {

	public static final String LOG_TAG = TestProvider.class.getSimpleName();

	/*
	   This helper function deletes all records from both database tables using the ContentProvider.
	   It also queries the ContentProvider to make sure that the database has been successfully
	   deleted, so it cannot be used until the Query and Delete functions have been written
	   in the ContentProvider.

	   Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
	   the delete functionality in the ContentProvider.
	 */
	public void deleteAllRecordsFromProvider() {
		mContext.getContentResolver().delete(
				CONTENT_URI,
				null,
				null
		);

		Cursor cursor = mContext.getContentResolver().query(
				CONTENT_URI,
				null,
				null,
				null,
				null
		);

		assertEquals(0, cursor.getCount());
		cursor.close();
	}

	// Since we want each test to start with a clean slate, run deleteAllRecords
	// in setUp (called by the test runner before each test).
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		deleteAllRecordsFromProvider();
	}

	public void testProviderRegistry() {
		PackageManager pm = mContext.getPackageManager();

		ComponentName componentName = new ComponentName(mContext.getPackageName(),
				RecipeContentProvider.class.getName());
		try {
			// Fetch the provider info using the component name from the PackageManager
			// This throws an exception if the provider isn't registered.
			ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

			// Make sure that the registered authority matches the authority from the Contract.
			assertEquals(providerInfo.authority, RecipeContract.CONTENT_AUTHORITY);
		} catch (PackageManager.NameNotFoundException e) {
			// content provider is not registered
			fail(e.getMessage());
		}
	}

	/*
			This test doesn't touch the database.  It verifies that the ContentProvider returns
			the correct type for each type of URI that it can handle.
			Students: Uncomment this test to verify that your implementation of GetType is
			functioning correctly.
		 */
	public void testGetType() {
		String type = mContext.getContentResolver().getType(CONTENT_URI);
		assertEquals(CONTENT_TYPE, type);
	}

	public void testBasicRecipeQuery() {
		RecipeDbHelper dbHelper = new RecipeDbHelper(mContext);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		TestUtilities.insertTestRecipeValues(mContext);

		db.close();

		// Test the basic content provider query
		Cursor recipeCursor = mContext.getContentResolver().query(
				CONTENT_URI,
				null,
				null,
				null,
				null
		);

		// Make sure we get the correct cursor out of the database
		TestUtilities.validateCursor(recipeCursor, TestUtilities.createTestRecipeValues());

		// Has the NotificationUri been set correctly? --- we can only test this easily against API
		// level 19 or greater because getNotificationUri was added in API level 19.
		if (Build.VERSION.SDK_INT >= 19) {
			assertEquals(recipeCursor.getNotificationUri(), CONTENT_URI);
		}
	}

	/*
		This test uses the provider to insert and then update the data. Uncomment this test to
		see if your update location is functioning correctly.
	 */
	public void testUpdateLocation() {
		// Create a new map of values, where column names are the keys
		ContentValues values = TestUtilities.createTestRecipeValues();

		Uri recipeUri = mContext.getContentResolver().insert(CONTENT_URI, values);
		long recipeRowId = ContentUris.parseId(recipeUri);

		// Verify we got a row back.
		assertTrue(recipeRowId != -1);

		ContentValues updatedValues = new ContentValues(values);
		updatedValues.put(_ID, recipeRowId);
		updatedValues.put(COLUMN_TITLE, "New recipe title");

		// Create a cursor with observer to make sure that the content provider is notifying
		// the observers as expected
		Cursor locationCursor = mContext.getContentResolver().query(CONTENT_URI, null, null, null, null);

		TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
		locationCursor.registerContentObserver(tco);

		int count = mContext.getContentResolver().update(
				CONTENT_URI, updatedValues, _ID + "= ?",
				new String[] { Long.toString(recipeRowId)});
		assertEquals(count, 1);

		// Test to make sure our observer is called.  If not, we throw an assertion.
		//
		// Students: If your code is failing here, it means that your content provider
		// isn't calling getContext().getContentResolver().notifyChange(uri, null);
		tco.waitForNotificationOrFail();

		locationCursor.unregisterContentObserver(tco);
		locationCursor.close();

		// A cursor is your primary interface to the query results.
		Cursor cursor = mContext.getContentResolver().query(
				CONTENT_URI,
				null,   // projection
				_ID + " = " + recipeRowId,
				null,   // Values for the "where" clause
				null    // sort order
		);

		TestUtilities.validateCursor(cursor, updatedValues);
		cursor.close();
	}

	// Make sure we can still delete after adding/updating stuff
	//
	// Student: Uncomment this test after you have completed writing the delete functionality
	// in your provider.  It relies on insertions with testInsertReadProvider, so insert and
	// query functionality must also be complete before this test can be used.
	public void testDeleteRecords() {
		testBasicRecipeQuery();

		// Register a content observer for our location delete.
		TestUtilities.TestContentObserver locationObserver = TestUtilities.getTestContentObserver();
		mContext.getContentResolver().registerContentObserver(CONTENT_URI, true, locationObserver);

		deleteAllRecordsFromProvider();

		locationObserver.waitForNotificationOrFail();

		mContext.getContentResolver().unregisterContentObserver(locationObserver);
	}

	private static final int BULK_INSERT_RECORDS_TO_INSERT = 10;

	static ContentValues[] createBulkInsertValues() {
		ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

		for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
			ContentValues recipeValue = new ContentValues();
			recipeValue.put(COLUMN_EXTERNAL_ID, UUID.randomUUID().toString());
			recipeValue.put(COLUMN_TITLE, "title");
			recipeValue.put(COLUMN_IMAGE_URL, "image-url");
			recipeValue.put(COLUMN_IMAGE_CONTENT, "content".getBytes());
			recipeValue.put(COLUMN_SOCIAL_RANK, 3.5);
			recipeValue.put(COLUMN_SOURCE_URL, "source-url");
			recipeValue.put(COLUMN_INGREDIENTS,
					buildIngredientsString(Arrays.asList("Chicken", "Potato")));
			returnContentValues[i] = recipeValue;
		}
		return returnContentValues;
	}

	public void testBulkInsert() {
		ContentValues[] bulkInsertContentValues = createBulkInsertValues();

		// Register a content observer for our bulk insert.
		TestUtilities.TestContentObserver recipeObserver = TestUtilities.getTestContentObserver();
		mContext.getContentResolver().registerContentObserver(CONTENT_URI, true, recipeObserver);

		int insertCount = mContext.getContentResolver().bulkInsert(CONTENT_URI, bulkInsertContentValues);

		recipeObserver.waitForNotificationOrFail();
		mContext.getContentResolver().unregisterContentObserver(recipeObserver);

		assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

		// A cursor is your primary interface to the query results.
		Cursor cursor = mContext.getContentResolver().query(
				CONTENT_URI,
				null, // leaving "columns" null just returns all the columns.
				null, // cols for "where" clause
				null, // values for "where" clause
				COLUMN_SOCIAL_RANK + " ASC"  // sort order == by social rank ASCENDING
		);

		// we should have as many records in the database as we've inserted
		assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

		// and let's make sure they match the ones we created
		cursor.moveToFirst();
		for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext())
			TestUtilities.validateCurrentRecord(cursor, bulkInsertContentValues[i]);

		cursor.close();
	}
}
