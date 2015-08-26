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

import java.util.List;

/**
 * Includes all recipe properties we are going to play with
 *
 * @author Andrei Safronov
 */
public class Recipe {

	/**
	 * Unique identifier of recipe
	 */
	protected String externalId;

	/**
	 * Title of recipe
	 */
	protected String title;

	/**
	 * URL of image of dish created by this recipe
	 */
	protected String imageUrl;

	/**
	 * Image content
	 */
	protected byte[] imageBytes;

	/**
	 * Social rating of this recipe
	 */
	protected double socialRank;

	/**
	 * Ingredients required for this recipe
	 */
	private List<String> ingredients;

	/**
	 * URL of external description of recipe
	 */
	private String sourceUrl;

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public byte[] getImageBytes() {
		return imageBytes;
	}

	public void setImageBytes(byte[] imageBytes) {
		this.imageBytes = imageBytes;
	}

	public double getSocialRank() {
		return socialRank;
	}

	public void setSocialRank(double socialRank) {
		this.socialRank = socialRank;
	}

	public List<String> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<String> ingredients) {
		this.ingredients = ingredients;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
}
