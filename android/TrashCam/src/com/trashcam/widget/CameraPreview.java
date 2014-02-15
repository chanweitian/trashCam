package com.trashcam.widget;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.trashcam.Constants;

import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	private static final String TAG = CameraPreview.class.getSimpleName();
	private SurfaceHolder mHolder;
	public Camera mCamera;
	private Context mContext;
	public static final int BACK_CAM_ID = 0;
	public static final int FRONT_CAM_ID = 1;
	private int currentCameraId = 0;

	@SuppressWarnings("deprecation")
	public CameraPreview(Context context) {
		super(context);
		mContext = context;
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		startCamera(0);
		setCameraDisplayOrientation();
	}

	public void setCameraDisplayOrientation() {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(0, info);

		WindowManager winManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		int rotation = winManager.getDefaultDisplay().getRotation();

		int degrees = 0;

		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		mCamera.setDisplayOrientation(result);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (IOException e) {
			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
		}
	}

	public static File getOutputMediaFile(int type) {
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
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	public void startCamera(int cameraId) {
		mCamera = Camera.open(cameraId);

		// get Camera parameters
		Camera.Parameters params = mCamera.getParameters();
		// set the focus mode
		params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		// set Camera parameters
		params.set("orientation", "landscape");
		mCamera.setParameters(params);
		try {
			// this step is critical or preview on new camera will no know where
			// to render to
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.startPreview();
	}

	public void stopCamera() {
		if (mCamera == null)
			return;
		try {
			mCamera.cancelAutoFocus();
			mCamera.stopPreview();

		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			// Make sure that at least these two calls are made
			mCamera.release();
			mCamera = null;
		}
	}

	public void flipCamera() {
		stopCamera();
		// swap the id of the camera to be used
		if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
			currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
		} else {
			currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
		}
		startCamera(currentCameraId);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}
		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here

		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}
}