package com.trashcam;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

@SuppressLint("NewApi")
public class PictureFragment extends Fragment {
	private RelativeLayout relativeLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View rootView = inflater.inflate(R.layout.fragment_picture,
				container, false);
		
		Button shareButton = (Button) rootView.findViewById(R.id.button1);
		shareButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final Dialog dialog = new Dialog(getActivity());
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);   
				dialog.setContentView(R.layout.share_dialog);  
				
				//DisplayMetrics metrics = getResources().getDisplayMetrics();
				int height = Utility.getScreenSize(getActivity())[1];
				//int width = metrics.widthPixels;
				relativeLayout = (RelativeLayout) dialog.findViewById(R.id.sharedialog);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/4);
				relativeLayout.setLayoutParams(params);
				
				
				Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FuturaStd-Condensed.otf");
				TextView text = (TextView) dialog.findViewById(R.id.text);
				text.setText("SHARE TO");
				text.setTypeface(tf);
				text.setTextColor(Color.parseColor("#7b7a79"));
				
				
				dialog.show();
			}
		});
		
		return rootView;
	}
}
