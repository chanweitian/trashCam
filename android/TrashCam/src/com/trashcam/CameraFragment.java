package com.trashcam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.trashcam.model.DeleteFileModelManager;
import com.trashcam.widget.CameraPreview;

public class CameraFragment extends Fragment implements OnClickListener {
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final String TAG = CameraFragment.class.getSimpleName();
	private CameraPreview mPreview;
	private ImageView capture;
	private FrameLayout cameraFrameLayout;
	private ImageView flip;
	public int state_of_camera;
	public static final int PICTURE_STATE = 1;
	public static final int CAMERA_STATE = 0;
	public int days = 1;
	private boolean hasPaused;
	private ImageView share;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (!checkCameraHardware(getActivity())) {
			// what todo if camera not found?
			Log.wtf(TAG, "CAMERA NOT FOUND");
			throw new IllegalStateException("You got no camera");
		}
		if (Camera.getNumberOfCameras() == 1) {
			flip.setVisibility(View.GONE);
		}
		state_of_camera = CAMERA_STATE;
		mPreview = new CameraPreview(getActivity());
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_camera, container,
				false);
		capture = (ImageView) rootView.findViewById(R.id.camera_button);
		cameraFrameLayout = (FrameLayout) rootView.findViewById(R.id.camera);
		flip = (ImageView) rootView.findViewById(R.id.flip);
		share = (ImageView) rootView.findViewById(R.id.share);

		capture.setOnClickListener(this);
		flip.setOnClickListener(this);
		share.setOnClickListener(this);

		cameraFrameLayout.addView(mPreview);
		hasPaused = false;
		share.setVisibility(View.GONE);
		return rootView;
	}

	@Override
	public void onResume() {
		if (hasPaused)
			mPreview.startCamera(CameraPreview.BACK_CAM_ID);
		super.onResume();
	}

	@Override
	public void onPause() {
		hasPaused = true;
		mPreview.stopCamera();
		super.onPause();
	}

	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			currentPictureFile = CameraPreview
					.getOutputMediaFile(MEDIA_TYPE_IMAGE);

			if (currentPictureFile == null) {
				Log.d(TAG,
						"Error creating media file, check storage permissions: ");
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(currentPictureFile);
				fos.write(data);
				fos.close();
				DeleteFileModelManager.getInstance().addFile(
						currentPictureFile, 1);
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}

			if (currentPictureFile == null) {
				throw new IllegalStateException("this should not be null");
			}
		}
	};
	private File currentPictureFile;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.camera_button:
			if (state_of_camera == CAMERA_STATE) {
				mPreview.mCamera.takePicture(null, null, mPicture);
				state_of_camera = PICTURE_STATE;
				flip.setImageResource(R.drawable.ic_trash);
				capture.setImageResource(R.drawable.day1);
				share.setVisibility(View.VISIBLE);
			} else {
				if (days == 4) {
					days = 1;
				}
				switch (days) {
				case 1:
					capture.setImageResource(R.drawable.day1);
					DeleteFileModelManager.getInstance().addFile(
							currentPictureFile, 1);
					break;
				case 2:
					capture.setImageResource(R.drawable.day2);
					DeleteFileModelManager.getInstance().addFile(
							currentPictureFile, 2);
					break;
				case 3:
					capture.setImageResource(R.drawable.day3);
					DeleteFileModelManager.getInstance().addFile(
							currentPictureFile, 3);
					break;
				}
				days++;
			}
			break;

		case R.id.flip:
			if (state_of_camera == CAMERA_STATE) {
				mPreview.flipCamera();
			} else {
				/*
				 * Delete current file
				 */
				DeleteFileModelManager.getInstance().deleteFileNow(
						currentPictureFile);
				resetCameraUI();
			}
			break;
		case R.id.share:
			Utility.openShareDialog(getActivity(), Constants.TRASHCAM_MESSAGE,
					currentPictureFile.getAbsolutePath());
			break;
		}
	}

	public void resetCameraUI() {
		mPreview.startCamera(CameraPreview.BACK_CAM_ID);
		flip.setImageResource(R.id.flip);
		capture.setImageResource(R.drawable.ic_camera);
		state_of_camera = CAMERA_STATE;
		share.setVisibility(View.GONE);
		currentPictureFile = null;
	}
}
