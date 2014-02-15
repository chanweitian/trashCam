package com.trashcam;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	private static final String TAG = null;
	TrashAdapter mAdapter;
	ViewPager mPager;

	private int mActivePointerId = 0;
	private float mLastTouchY = 0;
	private float dy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * TODO: SHOULD INIT FROM DB!
		 */

		// Set this APK Full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Set this APK no title
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
		mAdapter = new TrashAdapter(getFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(1);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// DO NOT DO ANYTHING IF NOT AT CAMEAR
		Log.wtf(TAG, "reached" + mPager.getCurrentItem());
		if (mPager.getCurrentItem() != 1)
			return super.onTouchEvent(ev);

		int action = MotionEventCompat.getActionMasked(ev);

		switch (action) {
		case (MotionEvent.ACTION_DOWN): {
			Log.d(TAG, "Action was DOWN");
			final int pointerIndex = MotionEventCompat.getActionIndex(ev);
			final float y = MotionEventCompat.getY(ev, pointerIndex) * -1;
			Log.d(TAG, "Y is: " + y);

			// Remember where we started (for dragging)

			mLastTouchY = y;
			// Save the ID of this pointer (for dragging)
			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
			break;
		}
		case (MotionEvent.ACTION_MOVE): {

			Log.d(TAG, "Action was MOVE");
			final int pointerIndex = MotionEventCompat.findPointerIndex(ev,
					mActivePointerId);
			final float y = MotionEventCompat.getY(ev, pointerIndex) * -1;
			Log.d(TAG, "Y is: " + y);
			// Calculate the distance mov
			dy = y - mLastTouchY;

			if (dy < 0) {
				dy = 0;
			}

			int val = (int) dy / 70;
			Log.v(TAG, "" + val);
			Log.d(TAG, "Distance is: " + dy);

			break;
		}

		case (MotionEvent.ACTION_UP): {
			Log.d(TAG, "Action was UP");

			break;
		}
		case (MotionEvent.ACTION_CANCEL):
			Log.d(TAG, "Action was CANCEL");
			return true;
		case (MotionEvent.ACTION_OUTSIDE):
			Log.d(TAG, "Movement occurred outside bounds "
					+ "of current screen element");
			return true;
			// default :
			// return super.onTouchEvent(ev);
		}
		return true;
	}
}
