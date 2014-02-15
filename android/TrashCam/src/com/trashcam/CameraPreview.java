package com.trashcam;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;

public class CameraPreview implements SurfaceHolder.Callback,
		Camera.PreviewCallback {
	private Camera mCamera = null;
	private int PreviewSizeWidth;
	private int PreviewSizeHeight;
	private String NowPictureFileName;
	private Boolean TakePicture = false;

	public CameraPreview(int PreviewlayoutWidth, int PreviewlayoutHeight) {
		PreviewSizeWidth = PreviewlayoutWidth;
		PreviewSizeHeight = PreviewlayoutHeight;
	}

	@Override
	public void onPreviewFrame(byte[] arg0, Camera arg1) {
		// At preview mode, the frame data will push to here.
		// But we do not want these data.
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		Parameters parameters;

		parameters = mCamera.getParameters();
		// Set the camera preview size
		Size mPreviewSize = getOptimalPreviewSize(mCamera.getParameters()
				.getSupportedPreviewSizes(), PreviewSizeWidth,
				PreviewSizeHeight);

		parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height); 
//		parameters.setPictureSize(PreviewSizeWidth, PreviewSizeHeight);

		// Turn on the camera flash.
		String NowFlashMode = parameters.getFlashMode();
		if (NowFlashMode != null)
			parameters.setFlashMode(Parameters.FLASH_MODE_ON);
		// Set the auto-focus.
		String NowFocusMode = parameters.getFocusMode();
		if (NowFocusMode != null)
			parameters.setFocusMode("auto");

		mCamera.setParameters(parameters);

		mCamera.startPreview();
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

}
