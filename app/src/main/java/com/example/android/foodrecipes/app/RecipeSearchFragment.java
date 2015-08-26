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
import android.content.DialogInterface;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.foodrecipes.app.common.Recipe;
import com.example.android.foodrecipes.app.common.RecipesLoadResult;
import com.example.android.foodrecipes.app.data.external.RecipeExternalProvider;
import com.example.android.foodrecipes.app.loaders.RecipesLoaderAdapter;
import com.example.android.foodrecipes.app.loaders.RecipesLoaderAdapterFactory;
import com.example.android.foodrecipes.app.utils.PrefsUtil;
import com.example.android.foodrecipes.app.utils.UIUtil;

import java.util.List;

import static android.view.inputmethod.EditorInfo.*;

/**
 * Encapsulates fetching the recipes and displaying it as a {@link GridView} layout.
 *
 * @author Andrei Safronov
 */
public class RecipeSearchFragment extends Fragment implements LoaderCallbacks<RecipesLoadResult> {

	/**
	 * Key to get the flag indicating whether it's needed to forcibly show first recipe after load
	 */
	public static final String AUTO_LOAD_FIRST_RECIPE = "AUTO_LOAD_FIRST_RECIPE";

	/**
	 * Key to get the instance of {@link RecipesLoaderAdapterFactory}
	 */
	public static final String LOADER_ADAPTER_FACTORY = "LOADER_ADAPTER_FACTORY";

	/**
	 * Key to get the flag indicating whether it's needed to show the search query input
	 */
	public static final String SHOW_SEARCH_QUERY_INPUT = "SHOW_SEARCH_QUERY_INPUT";

	/**
	 * Key to get the flag indicating whether it's needed to load more data on scroll to bottom
	 */
	public static final String ENABLE_LOAD_ON_SCROLL = "ENABLE_LOAD_ON_SCROLL";

	/**
	 * Name of the shared preferences key to get the last show recipe id
	 */
	public static final String LAST_SHOWN_RECIPE_ID_KEY_NAME = "LAST_SHOWN_RECIPE_ID_KEY_NAME";

	private static final int LOADER_ID = 1;

	private RecipesAdapter mRecipesAdapter;
	private RecipesLoaderAdapter mLoaderAdapter;
	private ProgressDialog mProgressDialog;

	private boolean mAutoLoadFirstRecipe;
	private boolean mIsItFirstTimeRecipesLoading = true;
	private boolean mEnableLoadOnScroll;

	private String mCurrentQuery;
	private int mCurrentPage = 1;
	private int mPreviousTotal = 0;
	private boolean mLoading = true;
	private boolean mHasMoreResults = true;

	private GridView mRecipesGridView;
	private LinearLayout mOfflineItems;
	private EditText mRecipeText;

	private boolean mNoActiveLoading = true;

	private String lastRecipeIdKey;

	@Override
	public Loader<RecipesLoadResult> onCreateLoader(int id, Bundle args) {
		return mLoaderAdapter.createLoader();
	}

	@Override
	public void onLoadFinished(Loader<RecipesLoadResult> loader, RecipesLoadResult data) {
		mNoActiveLoading = true;

		if (data.hasError()) {
			mOfflineItems.setVisibility(View.VISIBLE);
			mRecipesGridView.setVisibility(View.GONE);
		} else {
			List<Recipe> recipes = data.getResult();
			mHasMoreResults = recipes.size() == RecipeExternalProvider.MAX_RECIPES_PER_PAGE;

			// if scroll is enabled, then we add new recipes to the adapter;
			// otherwise we just replace all existing recipes
			if (mEnableLoadOnScroll)
				mRecipesAdapter.addRecipes(recipes);
			else
				mRecipesAdapter.setRecipes(recipes);

			mOfflineItems.setVisibility(View.GONE);
			mRecipesGridView.setVisibility(View.VISIBLE);

			// if we get something and should show first recipe - let's show it
			if (mAutoLoadFirstRecipe && mIsItFirstTimeRecipesLoading) {
				mIsItFirstTimeRecipesLoading = false;
				String lastShownId = PrefsUtil.getLastShownRecipeId(getActivity(), lastRecipeIdKey);
				String toShow = lastShownId != null ? lastShownId : recipes.get(0).getExternalId();
				((Callback) getActivity()).onItemSelected(toShow);
			}

			if (mRecipesAdapter.getCount() == 0) {
				String noRecipesMsg = getActivity().getString(R.string.no_recipes_found);
				Toast.makeText(getActivity(), noRecipesMsg, Toast.LENGTH_LONG).show();
			}
		}

		mProgressDialog.dismiss();
	}

	@Override
	public void onLoaderReset(Loader<RecipesLoadResult> loader) {
		mRecipesAdapter.clearRecipes();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 final Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_recipe_descriptions, container, false);

		Bundle args = getArguments();
		if (args != null) {
			mAutoLoadFirstRecipe = args.getBoolean(AUTO_LOAD_FIRST_RECIPE);
			lastRecipeIdKey = args.getString(LAST_SHOWN_RECIPE_ID_KEY_NAME);

			RecipesLoaderAdapterFactory factory =
					(RecipesLoaderAdapterFactory) args.getSerializable(LOADER_ADAPTER_FACTORY);
			if (factory != null)
				mLoaderAdapter = factory.newLoaderAdapter();

			mEnableLoadOnScroll = args.getBoolean(ENABLE_LOAD_ON_SCROLL);

			mProgressDialog = UIUtil.createAndShowDefaultProgressDialog(getActivity());

			if (mAutoLoadFirstRecipe) {
				mProgressDialog.dismiss();
				if (!mNoActiveLoading) //don't show the dialog if the data has already been loaded
					mProgressDialog.show();
			}

			mProgressDialog.setOnKeyListener(new Dialog.OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						if (mLoaderAdapter != null)
							mLoaderAdapter.cancelLoading();
						mProgressDialog.dismiss();
					}
					return true;
				}
			});

			mRecipesAdapter = new RecipesAdapter(getActivity());
			mOfflineItems = (LinearLayout) rootView.findViewById(R.id.offline_items);
			mRecipesGridView = (GridView) rootView.findViewById(R.id.gridview_recipes);
			mRecipesGridView.setAdapter(mRecipesAdapter);

			mRecipesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
					Recipe recipe = (Recipe) adapterView.getItemAtPosition(position);
					if (recipe != null) {
						String id = recipe.getExternalId();
						PrefsUtil.updateLastShownRecipeId(getActivity(), id, lastRecipeIdKey);
						((Callback) getActivity()).onItemSelected(id);
					}
				}
			});

			if (mEnableLoadOnScroll)
				mRecipesGridView.setOnScrollListener(new RecipesGridScrollListener());

			mRecipeText = (EditText) rootView.findViewById(R.id.recipe_query_text);

			boolean showSearchQueryInput = args.getBoolean(SHOW_SEARCH_QUERY_INPUT);
			if (showSearchQueryInput) {
				mRecipeText.setVisibility(View.VISIBLE);
				mRecipeText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
						if (actionId == IME_ACTION_DONE || actionId == IME_ACTION_NEXT) {
							mCurrentQuery = mRecipeText.getText().toString();
							if (!mCurrentQuery.isEmpty()) {
								resetSearchState();
								mLoaderAdapter.startLoading();
								mProgressDialog.show();
								mNoActiveLoading = false;
								mRecipeText.clearFocus();
								UIUtil.hideSoftKeyboard(getActivity(), v);
								//scroll to the top of grid, after the new search is initiated
								mRecipesGridView.smoothScrollToPosition(0);
								PrefsUtil.updateLastRecipe(getActivity(), mCurrentQuery);
							}

							return true;
						}
						return false;
					}
				});
			} else {
				mRecipeText.setVisibility(View.GONE);
			}
		}

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if (mLoaderAdapter != null) {
			mLoaderAdapter.initLoader(LOADER_ID, getActivity(), savedInstanceState, this);
			mCurrentQuery = PrefsUtil.getLastRecipe(getActivity());
			mLoaderAdapter.setQueryAndPage(mCurrentQuery, mCurrentPage);
			mLoaderAdapter.startLoading();
			mNoActiveLoading = false;
			mRecipeText.setText(mCurrentQuery);
		}

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onPause() {
		if (mProgressDialog != null)
			mProgressDialog.dismiss();

		super.onPause();
	}

	private void resetSearchState() {
		mCurrentPage = 1;
		mPreviousTotal = 0;
		mLoading = true;
		mHasMoreResults = true;

		mLoaderAdapter.setQueryAndPage(mCurrentQuery, mCurrentPage);
		mRecipesAdapter.clearRecipes();
	}

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callback {
		/**
		 * DetailFragmentCallback for when an item has been selected.
		 */
		void onItemSelected(String recipeId);
	}

	private class RecipesGridScrollListener implements AbsListView.OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (mLoading) {
				if (totalItemCount > mPreviousTotal) {
					mLoading = false;
					mPreviousTotal = totalItemCount;
				}
			}

			if (!mLoading && (totalItemCount - visibleItemCount) <= firstVisibleItem) {
				if (mHasMoreResults) {
					mLoaderAdapter.setQueryAndPage(mCurrentQuery, ++mCurrentPage);
					mLoaderAdapter.startLoading();

					mNoActiveLoading = false;
					mProgressDialog.show();

					mLoading = true;
				}
			}
		}
	}
}