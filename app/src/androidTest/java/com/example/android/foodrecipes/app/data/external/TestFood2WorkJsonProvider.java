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

/**
 * @author Andrei Safronov
 */
public class TestFood2WorkJsonProvider extends AndroidTestCase {

	public void testGetRecipesJson() throws Exception {
		String json = Food2WorkClient.getRecipesJson("tomato,chicken,lime,mayo", 1);
		assertNotNull(json);
		assertFalse(json.isEmpty());
	}

	public void testGetRecipeJson() throws Exception {
		String json = Food2WorkClient.getRecipeJson("42055");
		assertNotNull(json);
		assertFalse(json.isEmpty());
	}
}
