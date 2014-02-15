package com.trashcam;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class FullScreenActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_image);

		// get intent data
		Intent i = getIntent();
		String url = i.getExtras().getString("url");
		final String newURL = url.substring(6, url.length());
		Log.w("XR", newURL);
		ImageView thisView = (ImageView) findViewById(R.id.full_image_view);
		ImageLoader.getInstance().displayImage(url, thisView);

		ImageView shareButton = (ImageView) findViewById(R.id.full_share_option);
		shareButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Utility.openShareDialog(FullScreenActivity.this,
						"I have uploaded a picture via TrashCam!", newURL);
			}
		});

	}
}
