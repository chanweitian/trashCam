package com.trashcam;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

@SuppressLint("NewApi")
public class PictureFragment extends Fragment {

	
	

	private RelativeLayout relativeLayout;
	private String message = "I have uploaded a new photo via TrashCam!";
	private String mediaStorageDir = new File(
			Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
	"MyCameraApp").getPath();
	private String imagePath = mediaStorageDir + File.separator + "IMG_20140215_191211.jpg";

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
				//int width = metrics.widthPixels;
				int height = Utility.getScreenSize(getActivity())[1];
				relativeLayout = (RelativeLayout) dialog.findViewById(R.id.sharedialog);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/4);
				relativeLayout.setLayoutParams(params);
				
				Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FuturaStd-Condensed.otf");
				TextView text = (TextView) dialog.findViewById(R.id.text);
				text.setText("SHARE TO");
				text.setTypeface(tf);
				text.setTextColor(Color.parseColor("#7b7a79"));
				
				ImageView closeButton = (ImageView) dialog.findViewById(R.id.shareclose);
				closeButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				
				ImageView facebookButton = (ImageView) dialog.findViewById(R.id.shareFB);
				facebookButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						shareFB();
						Log.w("Clicked", "Clicked");
					}
				});
				
				
				ImageView twitterButton = (ImageView) dialog.findViewById(R.id.shareTwitter);
				twitterButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						shareTwitter();
						Log.w("Clicked", "Clicked");
					}
					
				});
				
				ImageView emailButton = (ImageView) dialog.findViewById(R.id.shareEmail);
				emailButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						shareEmail();
						Log.w("Clicked", "Clicked");
					}
					
				});
				
				ImageView whatappsButton = (ImageView) dialog.findViewById(R.id.shareWhatapps);
				whatappsButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						shareWhatapps();
						Log.w("Clicked", "Clicked");
					}
					
				});
				
				dialog.show();
			}
		});
		
		return rootView;
	}
	
	private void shareFB() {
		try{
		    Intent intent = new Intent(Intent.ACTION_SEND);
		    intent.setType("image/jpeg");
		   
		    final PackageManager pm = getActivity().getPackageManager();
		    final List activityList = pm.queryIntentActivities(intent, 0);
		    int len =  activityList.size();
		    Log.w("len", len + "");
		    for (int i = 0; i < len; i++) {
		        final ResolveInfo app = (ResolveInfo) activityList.get(i);
		        Log.w("Name", app.activityInfo.name);
		        if ("com.facebook.katana.activity.composer.ImplicitShareIntentHandler".equals(app.activityInfo.name)) {
		            final ActivityInfo activity=app.activityInfo;
		            final ComponentName name=new ComponentName(activity.applicationInfo.packageName, activity.name);
		            intent.addCategory(Intent.CATEGORY_LAUNCHER);
		            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		            intent.setComponent(name);
		            intent.putExtra(Intent.EXTRA_TEXT, message);
		            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath.toString())) );
		            getActivity().startActivity(intent);
		            break;
		        }
		    }
		}
		catch(final ActivityNotFoundException e) {
		    Log.i("mufumbo", "no fb native",e );
		}

	}
	
	
	private void shareTwitter(){
		try{
		    Intent intent = new Intent(Intent.ACTION_SEND);
		    intent.putExtra(Intent.EXTRA_TEXT, message);
		    //intent.setType("text/plain");
		    intent.setType("image/jpeg");
		    final PackageManager pm = getActivity().getPackageManager();
		    final List activityList = pm.queryIntentActivities(intent, 0);
		    int len =  activityList.size();
		    Log.w("len", len + "");
		    for (int i = 0; i < len; i++) {
		        final ResolveInfo app = (ResolveInfo) activityList.get(i);
		        Log.w("Name", app.activityInfo.name);
		        if ("com.twitter.applib.composer.TextFirstComposerActivity".equals(app.activityInfo.name)) {
		            final ActivityInfo activity=app.activityInfo;
		            final ComponentName name=new ComponentName(activity.applicationInfo.packageName, activity.name);
		            intent=new Intent(Intent.ACTION_SEND);
		            intent.addCategory(Intent.CATEGORY_LAUNCHER);
		            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		            intent.setComponent(name);
		            intent.putExtra(Intent.EXTRA_TEXT, message);
		            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath.toString())) );
		            getActivity().startActivity(intent);
		            break;
		        }
		    }
		}
		catch(final ActivityNotFoundException e) {
		    Log.i("mufumbo", "no twitter native",e );
		}
	}
	
	private void shareEmail(){
		try{
		    Intent intent = new Intent(Intent.ACTION_SEND);
		    intent.putExtra(Intent.EXTRA_TEXT, message);
		    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath.toString())) );
		    intent.setType("image/jpeg");
		    final PackageManager pm = getActivity().getPackageManager();
		    final List activityList = pm.queryIntentActivities(intent, 0);
		    int len =  activityList.size();
		    Log.w("len", len + "");
		    for (int i = 0; i < len; i++) {
		        final ResolveInfo app = (ResolveInfo) activityList.get(i);
		        Log.w("Name", app.activityInfo.name);
		        if ("com.google.android.gm.ComposeActivityGmail".equals(app.activityInfo.name)) {
		            final ActivityInfo activity=app.activityInfo;
		            final ComponentName name=new ComponentName(activity.applicationInfo.packageName, activity.name);
		            intent=new Intent(Intent.ACTION_SEND);
		            intent.addCategory(Intent.CATEGORY_LAUNCHER);
		            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		            intent.setComponent(name);
		            intent.setPackage(app.activityInfo.packageName);
		            intent.putExtra(Intent.EXTRA_TEXT, message);
		            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath)) );
		            getActivity().startActivity(intent);
		            break;
		        }
		    }
		}
		catch(final ActivityNotFoundException e) {
		    Log.i("mufumbo", "no email native",e );
		}
	}
	
	private void shareWhatapps(){
	
		try{
		    Intent intent = new Intent(Intent.ACTION_SEND);
		    intent.setType("image/jpeg");
   
		    final PackageManager pm = getActivity().getPackageManager();
		    final List activityList = pm.queryIntentActivities(intent, 0);
		    int len =  activityList.size();
		    Log.w("len", len + "");
		    for (int i = 0; i < len; i++) {
		        final ResolveInfo app = (ResolveInfo) activityList.get(i);
		        Log.w("Name", app.activityInfo.name);
		        if ("com.whatsapp.ContactPicker".equals(app.activityInfo.name)) {
		        	final ActivityInfo activity=app.activityInfo;
		            final ComponentName name=new ComponentName(activity.applicationInfo.packageName, activity.name);
		            intent.addCategory(Intent.CATEGORY_LAUNCHER);
		            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		            intent.setComponent(name);
		            intent.putExtra(Intent.EXTRA_TEXT, message);
		            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath.toString())) );
		            getActivity().startActivity(intent);
		            break;
		        }
		    }
		}
		catch(final ActivityNotFoundException e) {
		    Log.i("mufumbo", "no whatapps native",e );
		}
	}
}
