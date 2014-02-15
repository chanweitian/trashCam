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
	private Camera mCamera;
	private CameraPreview mPreview;
	private ImageView capture;
	private FrameLayout cameraFrameLayout;
	private ImageView flip;
	public int state_of_camera;
	public static final int PICTURE_STATE = 1;
	public static final int CAMERA_STATE = 0;
	public int days = 1;
	private boolean hasPaused;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_camera, container,
				false);
		capture = (ImageView) rootView.findViewById(R.id.camera_button);
		cameraFrameLayout = (FrameLayout) rootView.findViewById(R.id.camera);
		flip = (ImageView) rootView.findViewById(R.id.flip);
		cameraFrameLayout.addView(mPreview);
		capture.setOnClickListener(this);
		flip.setOnClickListener(this);
		hasPaused = false;
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
		super.onPause();
	}

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
		mCamera = getCameraInstance();
		mPreview = new CameraPreview(getActivity(), mCamera);
		super.onCreate(savedInstanceState);
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

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			currentPictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

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
		}
	};
	private File currentPictureFile;

	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				Constants.TRASH_FOLDER_PATH);
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile = null;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");
		return mediaFile;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.camera_button:
			if (state_of_camera == CAMERA_STATE) {
				mCamera.takePicture(null, null, mPicture);
				state_of_camera = PICTURE_STATE;
				flip.setImageResource(R.drawable.ic_trash);
				capture.setImageResource(R.drawable.day1);
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
				currentPictureFile = null;
				mPreview.startCamera(CameraPreview.BACK_CAM_ID);
				capture.setImageResource(R.drawable.ic_camera);
				state_of_camera = CAMERA_STATE;
			}
			break;
		}
	}
}
