package com.trashcam;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

public class TrashAdapter extends FragmentPagerAdapter {
	private GalleryFragment gallery;
	
	public TrashAdapter(FragmentManager fm) {
		super(fm);
		gallery = new GalleryFragment();
	}

	@Override
	public int getCount() {
		return 1;//NEED TO CHANGE
	}

	@Override
	public Fragment getItem(int position) {
		return gallery;
	}
}