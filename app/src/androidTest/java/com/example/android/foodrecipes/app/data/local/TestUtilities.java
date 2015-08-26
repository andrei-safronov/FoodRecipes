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

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.android.foodrecipes.app.utils.PollingCheck;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.android.foodrecipes.app.data.local.RecipeContract.RecipeEntry.*;


public class TestUtilities extends AndroidTestCase {

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());
        validateCurrentRecord(valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            Object value = entry.getValue();

            int idx = valueCursor.getColumnIndex(columnName);
            assertTrue(idx > -1);
            if (value instanceof byte[]) {
                byte[] bytesValue = (byte[]) value;
                assertTrue(Arrays.equals(bytesValue, valueCursor.getBlob(idx)));
            } else {
                String expectedValue = value.toString();
                assertEquals(expectedValue, valueCursor.getString(idx));
            }
        }
    }

    static long insertTestRecipeValues(Context context) {
        RecipeDbHelper dbHelper = new RecipeDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = createTestRecipeValues();

        long locationRowId = db.insert(TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        return locationRowId;
    }

	static ContentValues createTestRecipeValues() {
        ContentValues recipeValues = new ContentValues();
        List<String> ingredients = Arrays.asList("Chicken", "Potato");
        recipeValues.put(COLUMN_EXTERNAL_ID, "id");
        recipeValues.put(COLUMN_TITLE, "title");
        recipeValues.put(COLUMN_IMAGE_URL, "image-url");
        recipeValues.put(COLUMN_IMAGE_CONTENT, "image".getBytes());
        recipeValues.put(COLUMN_SOCIAL_RANK, 4.5D);
        recipeValues.put(COLUMN_SOURCE_URL, "source-url");
        recipeValues.put(COLUMN_INGREDIENTS, buildIngredientsString(ingredients));
        return recipeValues;
	}



    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
