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
package com.example.android.foodrecipes.app;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.foodrecipes.app.common.Recipe;
import com.example.android.foodrecipes.app.utils.SquareImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter working over the instances of {@link Recipe}.
 * Transforms instance of {@link Recipe} into the Image (recipe picture) and the
 * TextView (recipe title).
 *
 * @author Andrei Safronov
 */
public class RecipesAdapter extends BaseAdapter {

	private final Context mContext;
	private List<Recipe> mRecipes = new ArrayList<>();

	public RecipesAdapter(Context context) {
		mContext = context;
	}

	public void addRecipes(List<Recipe> descriptions) {
		mRecipes.addAll(descriptions);
		notifyDataSetChanged();
	}

	public void setRecipes(List<Recipe> recipes) {
		mRecipes = recipes;
		notifyDataSetChanged();
	}

	public void clearRecipes() {
		mRecipes = new ArrayList<>();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mRecipes.size();
	}

	@Override
	public Recipe getItem(int position) {
		return mRecipes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Recipe recipe = getItem(position);
		ViewHolder holder;

		if (convertView == null) {
			int layoutId = R.layout.recipe_image_grid_item;
			convertView = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.recipeTitle.setText(Html.fromHtml(recipe.getTitle()));

		if (recipe.getImageBytes() != null) {
			holder.recipePicture.setLocalImageBitmap(recipe.getImageBytes());
		} else {
			holder.recipePicture.setDefaultImageResId(R.drawable.ic_no_recipe_image_96dp);
			holder.recipePicture.setImageUrl(recipe.getImageUrl(),
					AppController.getInstance().getImageLoader());
		}

		return convertView;
	}

	private static class ViewHolder {
		final SquareImageView recipePicture;
		final TextView recipeTitle;

		ViewHolder(View view) {
			recipePicture = (SquareImageView) view.findViewById(R.id.picture);
			recipeTitle = (TextView) view.findViewById(R.id.text);
		}
	}
}