package com.trashcam;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PictureFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// load second
		View rootView = inflater.inflate(R.layout.fragment_picture,
				container, false);
		return rootView;
	}
}
