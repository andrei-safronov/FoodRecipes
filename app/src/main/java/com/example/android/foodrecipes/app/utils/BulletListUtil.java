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
package com.example.android.foodrecipes.app.utils;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.UnderlineSpan;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to create bulleted lists from the {@code List<String>}.
 * If the line starts with one of {@link BulletListUtil#BAD_FIRST_SYMBOLS}, it removes this
 * symbol. Also if the line consists of the upper case words, it means this line is used as the
 * header and this header is underlined.
 *
 * @author Andrei Safronov
 */
public final class BulletListUtil {

	private BulletListUtil() {
	}

	/**
	 * If the line starts with one of this symbols, such symbol should be removed from that line
	 */
	private static final char[] BAD_FIRST_SYMBOLS = {'.', ',', ':', ';'};

	/**
	 * Returns the bulleted list based on the given {@code lines}.
	 * If one of lines starts with {@link BulletListUtil#BAD_FIRST_SYMBOLS}, then such symbol is
	 * removed from there (sometimes bad lines are received from the Food2Work).
	 * Also if line consists of the upper case words, then this line is used like a header and is
	 * underlined.
	 *
	 * @param leadingMargin In pixels, the space between the left edge of the bullet and the left
	 *                         edge of the text
	 * @param lines List of strings. Each string will be a separate item in the bulleted list
	 * @return The bulleted list based on the given {@code lines}
	 */
	public static CharSequence makeBulletList(List<String> lines, int leadingMargin) {
		List<Spanned> spanned = new ArrayList<>(lines.size());
		for (String line : lines) {
			if (!line.trim().isEmpty()) {
				Spanned spannedLine = Html.fromHtml(removeBadFirstCharacters(line.trim()));
				spanned.add(spannedLine);
			}
		}

		SpannableStringBuilder sb = new SpannableStringBuilder();
		for (int i = 0; i < spanned.size(); i++) {
			CharSequence line = spanned.get(i) + (i < spanned.size() - 1 ? "\n" : "");
			boolean underlineNeeded = isUpperCase(line);
			Spannable spannable = new SpannableString(line);
			if (underlineNeeded) {
				spannable.setSpan(new UnderlineSpan(), 0, spannable.length(),
						Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			} else {
				spannable.setSpan(new BulletSpan(leadingMargin), 0, spannable.length(),
						Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			}
			sb.append(spannable);
		}
		return sb;
	}

	private static String removeBadFirstCharacters(String line) {
		boolean shouldRemove = false;
		char first = line.charAt(0);
		for (char badFirstSymbol : BAD_FIRST_SYMBOLS) {
			if (first == badFirstSymbol) {
				shouldRemove = true;
				break;
			}
		}

		return shouldRemove ? line.substring(1) : line;
	}

	private static boolean isUpperCase(CharSequence sequence) {
		for (int i = 0; i < sequence.length(); i++) {
			if (Character.isLowerCase(sequence.charAt(i)))
				return false;
		}

		return true;
	}
}
