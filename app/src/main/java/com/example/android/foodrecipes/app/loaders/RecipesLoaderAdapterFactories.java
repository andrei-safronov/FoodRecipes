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
package com.example.android.foodrecipes.app.loaders;

/**
 * Helper class which contains main implementations of {@link RecipesLoaderAdapterFactory}
 *
 * @author Andrei Safronov
 */
public final class RecipesLoaderAdapterFactories {

	private RecipesLoaderAdapterFactories() {
	}

	/**
	 * Creates loaders which load data from the internet
	 */
	public static RecipesLoaderAdapterFactory EXTERNAL = new RecipesLoaderAdapterFactory() {
		@Override
		public RecipesLoaderAdapter newLoaderAdapter() {
			return new ExternalRecipesLoaderAdapter();
		}
	};

	/**
	 * Creates loaders which load data from the local db, but include only basic columns, i.e.
	 * don't load recipe ingredient list
	 */
	public static RecipesLoaderAdapterFactory LOCAL_BASIC = new RecipesLoaderAdapterFactory() {
		@Override
		public RecipesLoaderAdapter newLoaderAdapter() {
			return new LocalRecipesLoaderAdapter(false);
		}
	};

	/**
	 * Creates loaders which load data from the local db, including all columns
	 */
	public static RecipesLoaderAdapterFactory LOCAL_EXTENDED = new RecipesLoaderAdapterFactory() {
		@Override
		public RecipesLoaderAdapter newLoaderAdapter() {
			return new LocalRecipesLoaderAdapter(true);
		}
	};

}
