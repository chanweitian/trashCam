package com.trashcam;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.trashcam.model.DeleteFileModelManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class CameraPreview implements SurfaceHolder.Callback,
		Camera.PreviewCallback {
	private Camera mCamera = null;
	private int PreviewSizeWidth;
	private int PreviewSizeHeight;
	private String NowPictureFileName;
	private Boolean TakePicture = false;
	private Context mContext;

	public CameraPreview(Context mContext, int PreviewlayoutWidth,
			int PreviewlayoutHeight) {
		this.mContext = mContext;
		PreviewSizeWidth = PreviewlayoutWidth;
		PreviewSizeHeight = PreviewlayoutHeight;
	}

	@Override
	public void onPreviewFrame(byte[] arg0, Camera arg1) {
		// At preview mode, the frame data will push to here.
		// But we do not want these data.
	}

	public void startCameraPreview() {
		mCamera.startPreview();
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		Parameters parameters;

		parameters = mCamera.getParameters();
		// Set the camera preview size
		Size mPreviewSize = getOptimalPreviewSize(mCamera.getParameters()
				.getSupportedPreviewSizes(), PreviewSizeWidth,
				PreviewSizeHeight);
		List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
		Size mPictureSize = getOptimalPreviewSize(sizes, PreviewSizeWidth,
				PreviewSizeHeight);
		parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
		parameters.setPictureSize(mPictureSize.width, mPictureSize.height);

		// Turn on the camera flash.
		// String NowFlashMode = parameters.getFlashMode();
		// if (NowFlashMode != null)
		// parameters.setFlashMode(Parameters.FLASH_MODE_ON);
		// Set the auto-focus.
		String NowFocusMode = parameters.getFocusMode();
		if (NowFocusMode != null)
			parameters.setFocusMode("auto");
		setCameraDisplayOrientation();
		mCamera.setParameters(parameters);
		mCamera.startPreview();
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

	private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w,
			int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) h / w;

		if (sizes == null)
			return null;

		Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		for (Camera.Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		mCamera = Camera.open();
		try {
			// If did not set the SurfaceHolder, the preview area will be black.
			mCamera.setPreviewDisplay(arg0);
			mCamera.setPreviewCallback(this);
		} catch (IOException e) {
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		mCamera.setPreviewCallback(null);
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	// Take picture interface
	public void CameraTakePicture(String FileName) {
		TakePicture = true;
		NowPictureFileName = FileName;
		mCamera.autoFocus(myAutoFocusCallback);
	}

	// Set auto-focus interface
	public void CameraStartAutoFocus() {
		TakePicture = false;
		mCamera.autoFocus(myAutoFocusCallback);
	}

	// =================================
	//
	// AutoFocusCallback
	//
	// =================================
	AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {
		public void onAutoFocus(boolean arg0, Camera NowCamera) {
			if (TakePicture) {
				NowCamera.takePicture(shutterCallback, rawPictureCallback,
						jpegPictureCallback);
				TakePicture = false;
			}
		}
	};
	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			// Just do nothing.
		}
	};

	PictureCallback rawPictureCallback = new PictureCallback() {
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			// Just do nothing.
		}
	};

	PictureCallback jpegPictureCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera arg1) {
			// Save the picture.
			try {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				FileOutputStream out = new FileOutputStream(NowPictureFileName);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
				DeleteFileModelManager.getInstance().addFile(
						NowPictureFileName, 1, mContext);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

}
