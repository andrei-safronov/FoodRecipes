package com.example.android.foodrecipes.app.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.android.foodrecipes.app.R;

/**
 * Helper for some UI operations
 *
 * @author Andrei Safronov
 */
public final class UIUtil {

	private UIUtil() {
	}

	public static void hideSoftKeyboard(Context context, View editText) {
		InputMethodManager imm = (InputMethodManager)
				context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	public static ProgressDialog createAndShowDefaultProgressDialog(Context context) {
		String title = context.getString(R.string.text_please_wait);
		String text = context.getString(R.string.text_loading_best_recipes);
		return ProgressDialog.show(context, title, text);
	}
}
