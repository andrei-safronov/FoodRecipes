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
package com.example.android.foodrecipes.app.common;

import java.util.Collections;
import java.util.List;

/**
 * Container to store results of the recipe data loading. Contains the code {@code List<Recipe>}
 * if the loading completed successfully or {@link Exception} instance that was thrown during the
 * loading process.
 *
 * <p></p>
 * Calling UI component handles exceptions received in this class instance or displays the
 * downloaded recipe data.
 *
 * @author Andrei Safronov
 */
public class RecipesLoadResult {

	private final List<Recipe> result;
	private final Exception error;

	public RecipesLoadResult(Recipe result) {
		this(Collections.singletonList(result));
	}

	public RecipesLoadResult(List<Recipe> result) {
		this.result = result;
		error = null;
	}

	public RecipesLoadResult(Exception error) {
		result = null;
		this.error = error;
	}

	public List<Recipe> getResult() {
		return result;
	}

	public Recipe getSingleResult() {
		return result == null || result.isEmpty() ? null : result.get(0);
	}

	public boolean hasError() {
		return error != null;
	}
}
