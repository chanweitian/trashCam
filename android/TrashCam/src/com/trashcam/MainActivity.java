package com.trashcam;

import com.trashcam.model.DeleteFileModelManager;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends SingleFragmentActivity {
	private static final String TAG = null;

	private int mActivePointerId = 0;
	private float mLastTouchY = 0;
	private float dy;

	private CameraFragment camFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Set this APK no title
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
		MainDatabase.initMainDB(this);
		DeleteFileModelManager.getInstance();
		super.onCreate(savedInstanceState);

	}

	@Override
	protected Fragment createFragment() {
		if (camFragment == null) {
			camFragment = new CameraFragment();
		}
		return camFragment;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (camFragment.state_of_camera == CameraFragment.CAMERA_STATE) {
			return super.onTouchEvent(ev);
		}
		int action = MotionEventCompat.getActionMasked(ev);

		switch (action) {
		case (MotionEvent.ACTION_DOWN): {
			Log.d(TAG, "Action was DOWN");
			final int pointerIndex = MotionEventCompat.getActionIndex(ev);
			final float y = MotionEventCompat.getY(ev, pointerIndex) * -1;
			// Log.d(TAG, "Y is: " + y);

			// Remember where we started (for dragging)

			mLastTouchY = y;
			// Save the ID of this pointer (for dragging)
			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
			break;
		}
		case (MotionEvent.ACTION_MOVE): {

			// Log.d(TAG, "Action was MOVE");
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
			Log.v(TAG, "" + val + 1);
			camFragment.setDayForCurrentFile(val + 1);
			// Log.d(TAG, "Distance is: " + dy);

			break;
		}

		case (MotionEvent.ACTION_UP): {
			camFragment.confirm();

			break;
		}
		case (MotionEvent.ACTION_CANCEL):
			// Log.d(TAG, "Action was CANCEL");
			return true;
		case (MotionEvent.ACTION_OUTSIDE):
			// Log.d(TAG, "Movement occurred outside bounds "
			// + "of current screen element");
			return true;
			// default :
			// return super.onTouchEvent(ev);
		}
		return true;
	}
}
