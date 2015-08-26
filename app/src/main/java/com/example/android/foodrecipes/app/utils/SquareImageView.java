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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

/**
 * Extension of {@link NetworkImageView} which snaps to width thus creating the square image.
 * Also allows to set custom {@link Bitmap}, bases on local {@code byte[]} and {@link Bitmap}.
 *
 * @author Andrei Safronov
 */
public class SquareImageView extends NetworkImageView {

	private Bitmap mLocalBitmap;
	private boolean mShowLocal;

	public SquareImageView(Context context) {
		super(context);
	}

	public SquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
	}

	public void setLocalImageBitmap(byte[] bytes) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		setLocalImageBitmap(bitmap);
	}

	/**
	 * Sets the local bitmap into this {@link SquareImageView}.
	 * That allows to show the images which are not downloaded from the internet.
	 *
	 * @param bitmap bitmap to show
	 */
	public void setLocalImageBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			mShowLocal = true;
		}
		this.mLocalBitmap = bitmap;
		requestLayout();
	}

	@Override
	public void setImageUrl(String url, ImageLoader imageLoader) {
		mShowLocal = false;
		super.setImageUrl(url, imageLoader);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mShowLocal) {
			setImageBitmap(mLocalBitmap);
		}
	}

}
