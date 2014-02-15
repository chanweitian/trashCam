/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.trashcam;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

/**
 * Simple FragmentActivity to hold the main {@link ImageGridFragment} and not
 * much else.
 */
public class ImageGridActivity extends SingleFragmentActivity {

	String data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		data = getIntent().getExtras().getString("wtf");

		super.onCreate(savedInstanceState);

	}

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new ImageGridFragment(data);
	}
}
