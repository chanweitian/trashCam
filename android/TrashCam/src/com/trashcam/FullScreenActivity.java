package com.trashcam;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class FullScreenActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);
 
        // get intent data
        Intent i = getIntent();
        String url = i.getExtras().getString("url");
        ImageView thisView = (ImageView) findViewById(R.id.full_image_view);
        ImageLoader.getInstance().displayImage(url, thisView);

    }
}
