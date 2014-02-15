package com.trashcam;

import java.io.File;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.trashcam.model.DeleteFileModelManager;

public class CameraFragment extends Fragment implements OnClickListener {
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final String TAG = CameraFragment.class.getSimpleName();
	private ImageView capture;
	private FrameLayout cameraFrameLayout;
	private ImageView flip;
	public int state_of_camera;
	public static final int PICTURE_STATE = 1;
	public static final int CAMERA_STATE = 0;
	public int days = 1;
	private ImageView share;
	private CameraPreview camPreview;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private String currentPicturePath;


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
		super.onCreate(savedInstanceState);
	}

	@SuppressWarnings("deprecation")
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

		share.setVisibility(View.GONE);
		int xy[] = Utility.getScreenSize(getActivity());
		int previewSizeWidth = xy[0];
		int previewSizeHeight = xy[1];
		SurfaceView camView = new SurfaceView(getActivity());
		SurfaceHolder camHolder = camView.getHolder();
		camPreview = new CameraPreview(getActivity(), previewSizeWidth,
				previewSizeHeight);

		camHolder.addCallback(camPreview);
		camHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		cameraFrameLayout.addView(camView, new LayoutParams(previewSizeWidth,
				previewSizeHeight));
		return rootView;
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

	private Runnable takePicture = new Runnable() {
		String extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString();

		public void run() {
			String MyDirectory_path = extStorageDirectory;

			File file = new File(MyDirectory_path);
			if (!file.exists())
				file.mkdirs();
			String PictureFileName = MyDirectory_path + "/MyPicture.jpg";
			currentPicturePath = PictureFileName;
			camPreview.CameraTakePicture(PictureFileName);
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.camera_button:
			if (state_of_camera == CAMERA_STATE) {
				mHandler.postDelayed(takePicture, 50);
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
							currentPicturePath, 1, getActivity());
					break;
				case 2:
					capture.setImageResource(R.drawable.day2);
					DeleteFileModelManager.getInstance().addFile(
							currentPicturePath, 2, getActivity());
					break;
				case 3:
					capture.setImageResource(R.drawable.day3);
					DeleteFileModelManager.getInstance().addFile(
							currentPicturePath, 3, getActivity());
					break;
				}
				days++;
			}
			break;

		case R.id.flip:
			if (state_of_camera == CAMERA_STATE) {
				// mPreview.flipCamera();
			} else {
				/*
				 * Delete current file
				 */
				DeleteFileModelManager.getInstance().deleteFileNow(
						currentPicturePath);
				resetCameraUI();
			}
			break;
		case R.id.share:
			Toast.makeText(getActivity(), currentPicturePath, Toast.LENGTH_LONG)
					.show();
			Utility.openShareDialog(getActivity(), Constants.TRASHCAM_MESSAGE,
					currentPicturePath);
			break;
		}
	}

	public void resetCameraUI() {
		flip.setImageResource(R.id.flip);
		capture.setImageResource(R.drawable.ic_camera);
		state_of_camera = CAMERA_STATE;
		share.setVisibility(View.GONE);
		currentPicturePath = null;
		camPreview.startCameraPreview();
	}
}
