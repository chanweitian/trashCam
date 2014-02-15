package com.trashcam;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
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
	private boolean hasPaused;
	private ImageView gallery;

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
		gallery = (ImageView) rootView.findViewById(R.id.gallery);
		flip = (ImageView) rootView.findViewById(R.id.flip);
		share = (ImageView) rootView.findViewById(R.id.share);

		capture.setOnClickListener(this);
		flip.setOnClickListener(this);
		share.setOnClickListener(this);
		gallery.setOnClickListener(this);
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
		hasPaused = false;
		return rootView;
	}

	@Override
	public void onResume() {
		if (hasPaused)
			resetCameraUI();
		super.onResume();
	}

	@Override
	public void onPause() {
		hasPaused = true;
		DeleteFileModelManager.save();
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

	private Runnable takePicture = new Runnable() {
		String extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString();

		public void run() {
			File file = getOutputMediaFile();
			currentPicturePath = file.getAbsolutePath();
			camPreview.CameraTakePicture(currentPicturePath);
		}
	};
	private int day;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.camera_button:
			if (state_of_camera == CAMERA_STATE) {
				mHandler.postDelayed(takePicture, 50);
				state_of_camera = PICTURE_STATE;
				flip.setImageResource(R.drawable.ic_trash);
				flip.setVisibility(View.VISIBLE);
				capture.setImageResource(R.drawable.day1);
				share.setVisibility(View.VISIBLE);
			} else {
				if (days == 4) {
					days = 1;
				}
				switch (days) {
				case 1:
					day = 1;
					capture.setImageResource(R.drawable.day1);
					DeleteFileModelManager.getInstance().addFile(
							currentPicturePath, 1, getActivity());
					break;
				case 2:
					day = 2;
					capture.setImageResource(R.drawable.day2);
					DeleteFileModelManager.getInstance().addFile(
							currentPicturePath, 2, getActivity());
					break;
				case 3:
					day = 3;
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
				DeleteFileModelManager.getInstance().deleteFileNow(
						currentPicturePath);
				resetCameraUI();
				camPreview.startCameraPreview();
			}
			break;
		case R.id.share:
			Toast.makeText(getActivity(), currentPicturePath, Toast.LENGTH_LONG)
					.show();
			Utility.openShareDialog(getActivity(), Constants.TRASHCAM_MESSAGE,
					currentPicturePath);
			break;
		case R.id.gallery:
			// START GALLERY
			// Intent intent = new Intent(getActivity(),);
			// startActivity(intent);
			String s = HashTable.get_entry(HashTable.DELETABLES);
			Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
			break;
		}
	}

	public void resetCameraUI() {
		flip.setImageResource(R.id.flip);
		capture.setImageResource(R.drawable.ic_camera);
		state_of_camera = CAMERA_STATE;
		share.setVisibility(View.GONE);
		flip.setVisibility(View.GONE);
		currentPicturePath = null;
	}

	private static File getOutputMediaFile() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
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
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");
		return mediaFile;
	}

	public void setDayForCurrentFile(int i) {
		switch (i) {
		case 1:
			day = 1;
			capture.setImageResource(R.drawable.day1);
			break;
		case 2:
			day = 2;
			capture.setImageResource(R.drawable.day2);
			break;
		case 3:
			day = 3;
			capture.setImageResource(R.drawable.day3);
			break;
		case 4:
			day = 4;
			capture.setImageResource(R.drawable.day4);
			break;
		case 5:
			day = 5;
			capture.setImageResource(R.drawable.day5);
			break;
		case 6:
			day = 6;
			capture.setImageResource(R.drawable.day6);
			break;
		default:
			day = 7;
			capture.setImageResource(R.drawable.day7);
			break;
		}
	}

	public void confirm() {
		if (currentPicturePath != null && !"".equals(currentPicturePath))
			DeleteFileModelManager.getInstance().addFile(currentPicturePath,
					day, getActivity());
	}
}
