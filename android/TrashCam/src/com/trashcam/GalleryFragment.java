package com.trashcam;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GalleryFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// WEITIAN this load first
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// WEITIAN this load second
		View rootView = inflater.inflate(R.layout.fragment_gallery,
				container, false);
		return rootView;
	}
	
}
