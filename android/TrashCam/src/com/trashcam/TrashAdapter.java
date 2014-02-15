package com.trashcam;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

public class TrashAdapter extends FragmentPagerAdapter {
	private GalleryFragment gallery;
	private CameraFragment camera;

	public TrashAdapter(FragmentManager fm) {
		super(fm);
		camera = new CameraFragment();
		gallery = new GalleryFragment();
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return gallery;
		case 1:
			return camera;
		}
		return null;
	}
}