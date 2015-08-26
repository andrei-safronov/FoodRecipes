package com.example.android.foodrecipes.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.foodrecipes.app.R;

/**
 * Helper to operation with application preferences
 *
 * @author Andrei Safronov
 */
public final class PrefsUtil {

	private PrefsUtil() {
	}

	private static final String LAST_RECIPE_KEY = "LAST_RECIPE";

	public static String getLastRecipe(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(LAST_RECIPE_KEY, context.getString(R.string.pref_recipe_query_default));
	}

	public static void updateLastRecipe(Context context, String lastRecipe) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(LAST_RECIPE_KEY, lastRecipe);
		editor.commit();
	}

	public static String getLastShownRecipeId(Context context, String key) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(key, null);
	}

	public static void updateLastShownRecipeId(Context context, String lastRecipeId, String key) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, lastRecipeId);
		editor.commit();
	}

	public static boolean shouldSaveRecipeImages(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String key = context.getString(R.string.prefs_save_images_key);
		return prefs.getBoolean(key, true);
	}
}
