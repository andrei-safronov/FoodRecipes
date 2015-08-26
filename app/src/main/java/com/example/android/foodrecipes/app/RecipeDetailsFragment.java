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

import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.foodrecipes.app.common.RecipesLoadResult;
import com.example.android.foodrecipes.app.data.local.RecipeContract;
import com.example.android.foodrecipes.app.common.Recipe;
import com.example.android.foodrecipes.app.loaders.RecipesLoaderAdapter;
import com.example.android.foodrecipes.app.loaders.RecipesLoaderAdapterFactory;
import com.example.android.foodrecipes.app.utils.BulletListUtil;
import com.example.android.foodrecipes.app.utils.PrefsUtil;
import com.example.android.foodrecipes.app.utils.SquareImageView;
import com.example.android.foodrecipes.app.utils.UIUtil;

import java.io.ByteArrayOutputStream;

import static com.example.android.foodrecipes.app.data.local.RecipeContract.RecipeEntry.*;

/**
 * The fragment containing detailed information about the recipe.
 *
 * @author Andrei Safronov
 */
public class RecipeDetailsFragment extends Fragment implements LoaderCallbacks<RecipesLoadResult> {

	/**
	 * Key to get the id of recipe
	 */
	public static final String RECIPE_EXTERNAL_ID = "RECIPE_EXTERNAL_ID";

	/**
	 * Key to get the instance of {@link RecipesLoaderAdapterFactory}
	 */
	public static final String LOADER_ADAPTER_FACTORY = "LOADER_ADAPTER_FACTORY";

	/**
	 * Key to get flag indicating whether should add "Remove from favorites" button
	 */
	public static final String EXECUTE_RECIPE_REMOVE = "EXECUTE_RECIPE_REMOVE";

	/**
	 * Key to get flag indicating that the app works in the two pane mode
	 */
	public static final String TWO_PANE = "TWO_PANE";

	/**
	 * Margin for bulleted lists of ingredients
	 */
	private static final int BULLETED_LIST_MARGIN_PX = 5;

	/**
	 * Share hashtag for the recipe
	 */
	private static final String RECIPE_SHARE_HASHTAG = " #FoodRecipesApp";

	private static final int LOADER_ID = 2;

	private RecipesLoaderAdapter mLoaderAdapter;
	private ShareActionProvider mShareActionProvider;

	private ProgressDialog mProgressDialog;

	private ScrollView mRecipeDetailsLayout;
	private LinearLayout mOfflineItems;

	private boolean mExecuteRecipeRemove;
	private boolean mTwoPane;

	private TextView mRecipeTitle;
	private SquareImageView mRecipeImage;
	private RatingBar mSocialRank;
	private TextView mIngredients;
	private Button mFavoritesActionButton;

	private String mRecipeExternalId;
	private Recipe mRecipe;

	public RecipeDetailsFragment() {
		setHasOptionsMenu(true); // to have share menu item
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		Bundle args = getArguments();
		if (args != null) {
			mRecipeExternalId = args.getString(RECIPE_EXTERNAL_ID);
			RecipesLoaderAdapterFactory factory =
					(RecipesLoaderAdapterFactory) args.getSerializable(LOADER_ADAPTER_FACTORY);
			if (factory != null)
				mLoaderAdapter = factory.newLoaderAdapter();

			mExecuteRecipeRemove = args.getBoolean(EXECUTE_RECIPE_REMOVE, false);
			mTwoPane = args.getBoolean(TWO_PANE, false);
		}

		View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
		mRecipeTitle = (TextView) rootView.findViewById(R.id.detail_recipe_title);
		mRecipeImage = (SquareImageView) rootView.findViewById(R.id.detail_recipe_picture);
		mSocialRank = (RatingBar) rootView.findViewById(R.id.detail_recipe_rating);
		mIngredients = (TextView) rootView.findViewById(R.id.detail_recipe_ingredients);

		Button openInBrowserButton = (Button) rootView.findViewById(R.id.detail_recipe_source);
		openInBrowserButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(mRecipe.getSourceUrl()));
				startActivity(i);
			}
		});


		mRecipeDetailsLayout = (ScrollView) rootView.findViewById(R.id.recipe_details_layout);
		mOfflineItems = (LinearLayout) rootView.findViewById(R.id.offline_items);
		mFavoritesActionButton = (Button) rootView.findViewById(R.id.detail_recipe_favourites_action);
		int textId = mExecuteRecipeRemove ? R.string.recipe_details_remove_from_favorites :
				R.string.recipe_details_save_in_favourites;

		mFavoritesActionButton.setText(getActivity().getString(textId));
		mFavoritesActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mRecipe != null) {
					if (mExecuteRecipeRemove)
						removeCurrentRecipeFromFavorites();
					else
						saveCurrentRecipeInFavorites();
				}
			}
		});

		if (mRecipeExternalId != null && mLoaderAdapter != null) {
			// if there is recipe id set and the loader adapter then show the progress dialog
			// indicating the recipe load started
			mProgressDialog = UIUtil.createAndShowDefaultProgressDialog(getActivity());

			mProgressDialog.setOnKeyListener(new Dialog.OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						if (mLoaderAdapter != null)
							mLoaderAdapter.cancelLoading();
						mProgressDialog.dismiss();

						// if it's not the tablet layout and user cancelled the loading,
						// then there is no point to stay in this Activity
						if (!mTwoPane)
							getActivity().finish();
					}
					return true;
				}
			});
		}

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.detailfragment, menu);
		MenuItem menuItem = menu.findItem(R.id.action_share);
		mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

		// If onLoadFinished happens before this, we can go ahead and set the share intent
		if (mRecipe != null)
			mShareActionProvider.setShareIntent(createShareRecipeIntent());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if (mLoaderAdapter != null && mRecipeExternalId != null) {
			mLoaderAdapter.setRecipeId(mRecipeExternalId);
			mLoaderAdapter.initLoader(LOADER_ID, getActivity(), savedInstanceState, this);
			mLoaderAdapter.startLoading();
		}

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onPause() {
		if (mProgressDialog != null)
			mProgressDialog.dismiss();

		if (mLoaderAdapter != null)
			mLoaderAdapter.closeLoader(LOADER_ID);

		super.onPause();
	}

	@Override
	public Loader<RecipesLoadResult> onCreateLoader(int id, Bundle args) {
		return mLoaderAdapter.createLoader();
	}

	@Override
	public void onLoadFinished(Loader<RecipesLoadResult> loader, RecipesLoadResult data) {
		if (data.hasError()) {
			mOfflineItems.setVisibility(View.VISIBLE);
			mRecipeDetailsLayout.setVisibility(View.GONE);
		} else {
			mRecipe = data.getSingleResult();
			if (mRecipe != null) {
				mRecipeTitle.setText(Html.fromHtml(mRecipe.getTitle()));

				if (mRecipe.getImageBytes() != null) {
					mRecipeImage.setLocalImageBitmap(mRecipe.getImageBytes());
				} else {
					mRecipeImage.setDefaultImageResId(R.drawable.ic_no_recipe_image_96dp);
					mRecipeImage.setImageUrl(mRecipe.getImageUrl(),
							AppController.getInstance().getImageLoader());
				}

				mSocialRank.setRating(get5StarRating(mRecipe));

				mIngredients.setText(BulletListUtil.makeBulletList(mRecipe.getIngredients(),
						BULLETED_LIST_MARGIN_PX));

				// If onCreateOptionsMenu has already happened, we need to update the share intent
				if (mShareActionProvider != null)
					mShareActionProvider.setShareIntent(createShareRecipeIntent());
			}

			mOfflineItems.setVisibility(View.GONE);
			mRecipeDetailsLayout.setVisibility(View.VISIBLE);
		}

		if (mProgressDialog != null)
			mProgressDialog.dismiss();
	}

	@Override
	public void onLoaderReset(Loader<RecipesLoadResult> loader) {
	}

	private Intent createShareRecipeIntent() {
		String shareText = getActivity().getString(R.string.recipe_share_text);
		String recipeShareString = String.format(shareText, mRecipe.getTitle(),
				mRecipe.getSourceUrl()) + RECIPE_SHARE_HASHTAG;

		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, recipeShareString);
		return shareIntent;
	}

	private void saveCurrentRecipeInFavorites() {
		String title = getActivity().getString(R.string.text_please_wait);
		String text = getActivity().getString(R.string.text_saving_in_favorites);

		final ProgressDialog dialog = ProgressDialog.show(getActivity(), title, text);
		final BitmapDrawable bitmapDrawable = (BitmapDrawable) mRecipeImage.getDrawable();

		dialog.show();

		final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				ContentValues cv = new ContentValues();
				cv.put(COLUMN_EXTERNAL_ID, mRecipe.getExternalId());
				cv.put(COLUMN_TITLE, mRecipe.getTitle());
				cv.put(COLUMN_IMAGE_URL, mRecipe.getImageUrl());
				cv.put(COLUMN_SOCIAL_RANK, mRecipe.getSocialRank());
				cv.put(COLUMN_SOURCE_URL, mRecipe.getSourceUrl());
				cv.put(COLUMN_INGREDIENTS, buildIngredientsString(mRecipe.getIngredients()));

				if (PrefsUtil.shouldSaveRecipeImages(getActivity())) {
					Bitmap bitmap = bitmapDrawable.getBitmap();
					if (bitmap != null) {
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						cv.put(COLUMN_IMAGE_CONTENT, stream.toByteArray());
					}
				}

				getActivity().getContentResolver().insert(CONTENT_URI, cv);

				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				dialog.dismiss();
				Toast.makeText(getActivity(), getActivity().getString(R.string.recipe_saved_in_favorites),
						Toast.LENGTH_SHORT).show();

				mFavoritesActionButton.setEnabled(false);
			}
		};

		dialog.setOnKeyListener(new Dialog.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					task.cancel(true);
					dialog.dismiss();
				}
				return true;
			}
		});

		task.execute();
	}

	private void removeCurrentRecipeFromFavorites() {
		String title = getActivity().getString(R.string.text_please_wait);
		String text = getActivity().getString(R.string.text_removing_from_favorites);

		final ProgressDialog dialog = ProgressDialog.show(getActivity(), title, text);
		dialog.show();

		final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				String where = RecipeContract.RecipeEntry.COLUMN_EXTERNAL_ID + "=?";
				String[] selectionArgs = new String[]{String.valueOf(mRecipe.getExternalId())};
				getActivity().getContentResolver().delete(CONTENT_URI, where, selectionArgs);
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				dialog.dismiss();
				Toast.makeText(getActivity(), getActivity().getString(R.string.recipe_removed_from_favorites),
						Toast.LENGTH_SHORT).show();

				if (!mTwoPane) {
					// if we operate in the two pane mode we should not finish the activity as
					// we have only one activity, but with two fragments inside
					getActivity().finish();
				}
			}
		};

		dialog.setOnKeyListener(new Dialog.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					task.cancel(true);
					dialog.dismiss();
				}
				return true;
			}
		});

		task.execute();
	}

	private static float get5StarRating(Recipe recipe) {
		return (float) (recipe.getSocialRank() / (100 / 5));
	}
}