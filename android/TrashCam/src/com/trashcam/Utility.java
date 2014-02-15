package com.trashcam;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class Utility {
	public static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");

	public static int[] getScreenSize(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int[] ret = new int[2];
		ret[0] = size.x;
		ret[1] = size.y;
		return ret;
	}

	public static String getDateAfter(int days) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, days);
		return dateFormat.format(c.getTime());
	}
	
	public static long getDateAfterInLong(int days) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, days);
		return c.getTimeInMillis();
	}
	


	public static void openShareDialog(final Context context,
			final String message, final String imagePath) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.share_dialog);

		// DisplayMetrics metrics = getResources().getDisplayMetrics();
		// int width = metrics.widthPixels;
		int height = Utility.getScreenSize(context)[1];
		RelativeLayout relativeLayout = (RelativeLayout) dialog
				.findViewById(R.id.sharedialog);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, height / 4);
		relativeLayout.setLayoutParams(params);

		Typeface tf = Typeface.createFromAsset(context.getAssets(),
				"fonts/FuturaStd-Condensed.otf");
		TextView text = (TextView) dialog.findViewById(R.id.text);
		text.setText("SHARE TO");
		text.setTypeface(tf);
		text.setTextColor(Color.parseColor("#7b7a79"));

		ImageView closeButton = (ImageView) dialog
				.findViewById(R.id.shareclose);
		closeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		ImageView facebookButton = (ImageView) dialog
				.findViewById(R.id.shareFB);
		facebookButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				shareFB(context, message, imagePath);
				Log.w("Clicked", "Clicked");
			}
		});

		ImageView twitterButton = (ImageView) dialog
				.findViewById(R.id.shareTwitter);
		twitterButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				shareTwitter(context, message, imagePath);
				Log.w("Clicked", "Clicked");
			}

		});

		ImageView emailButton = (ImageView) dialog
				.findViewById(R.id.shareEmail);
		emailButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				shareEmail(context, message, imagePath);
				Log.w("Clicked", "Clicked");
			}

		});

		ImageView whatappsButton = (ImageView) dialog
				.findViewById(R.id.shareWhatapps);
		whatappsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				shareWhatapps(context, message, imagePath);
				Log.w("Clicked", "Clicked");
			}

		});

		dialog.show();
	}

	public static void shareFB(Context context, String message, String imagePath) {
		try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("image/jpeg");

			final PackageManager pm = context.getPackageManager();
			final List activityList = pm.queryIntentActivities(intent, 0);
			int len = activityList.size();
			Log.w("len", len + "");
			for (int i = 0; i < len; i++) {
				final ResolveInfo app = (ResolveInfo) activityList.get(i);
				Log.w("Name", app.activityInfo.name);
				if ("com.facebook.katana.activity.composer.ImplicitShareIntentHandler"
						.equals(app.activityInfo.name)) {
					final ActivityInfo activity = app.activityInfo;
					final ComponentName name = new ComponentName(
							activity.applicationInfo.packageName, activity.name);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					intent.setComponent(name);
					intent.putExtra(Intent.EXTRA_TEXT, message);
					intent.putExtra(Intent.EXTRA_STREAM,
							Uri.fromFile(new File(imagePath)));
					context.startActivity(intent);
					break;
				}
			}
		} catch (final ActivityNotFoundException e) {
			Log.i("mufumbo", "no fb native", e);
		}

	}

	public static void shareTwitter(Context context, String message,
			String imagePath) {
		try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT, message);
			// intent.setType("text/plain");
			intent.setType("image/jpeg");
			final PackageManager pm = context.getPackageManager();
			final List activityList = pm.queryIntentActivities(intent, 0);
			int len = activityList.size();
			Log.w("len", len + "");
			for (int i = 0; i < len; i++) {
				final ResolveInfo app = (ResolveInfo) activityList.get(i);
				Log.w("Name", app.activityInfo.name);
				if ("com.twitter.applib.composer.TextFirstComposerActivity"
						.equals(app.activityInfo.name)) {
					final ActivityInfo activity = app.activityInfo;
					final ComponentName name = new ComponentName(
							activity.applicationInfo.packageName, activity.name);
					intent = new Intent(Intent.ACTION_SEND);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					intent.setComponent(name);
					intent.putExtra(Intent.EXTRA_TEXT, message);
					intent.putExtra(Intent.EXTRA_STREAM,
							Uri.fromFile(new File(imagePath)));
					context.startActivity(intent);
					break;
				}
			}
		} catch (final ActivityNotFoundException e) {
			Log.i("mufumbo", "no twitter native", e);
		}
	}

	public static void shareEmail(Context context, String message,
			String imagePath) {
		try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT, message);
			intent.putExtra(Intent.EXTRA_STREAM,
					Uri.fromFile(new File(imagePath)));
			intent.setType("image/jpeg");
			final PackageManager pm = context.getPackageManager();
			final List activityList = pm.queryIntentActivities(intent, 0);
			int len = activityList.size();
			Log.w("len", len + "");
			for (int i = 0; i < len; i++) {
				final ResolveInfo app = (ResolveInfo) activityList.get(i);
				Log.w("Name", app.activityInfo.name);
				if ("com.google.android.gm.ComposeActivityGmail"
						.equals(app.activityInfo.name)) {
					final ActivityInfo activity = app.activityInfo;
					final ComponentName name = new ComponentName(
							activity.applicationInfo.packageName, activity.name);
					intent = new Intent(Intent.ACTION_SEND);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					intent.setComponent(name);
					intent.setPackage(app.activityInfo.packageName);
					intent.putExtra(Intent.EXTRA_TEXT, message);
					intent.putExtra(Intent.EXTRA_STREAM,
							Uri.fromFile(new File(imagePath)));
					context.startActivity(intent);
					break;
				}
			}
		} catch (final ActivityNotFoundException e) {
			Log.i("mufumbo", "no email native", e);
		}
	}

	public static void shareWhatapps(Context context, String message,
			String imagePath) {

		try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("image/jpeg");

			final PackageManager pm = context.getPackageManager();
			final List activityList = pm.queryIntentActivities(intent, 0);
			int len = activityList.size();
			Log.w("len", len + "");
			for (int i = 0; i < len; i++) {
				final ResolveInfo app = (ResolveInfo) activityList.get(i);
				Log.w("Name", app.activityInfo.name);
				if ("com.whatsapp.ContactPicker".equals(app.activityInfo.name)) {
					final ActivityInfo activity = app.activityInfo;
					final ComponentName name = new ComponentName(
							activity.applicationInfo.packageName, activity.name);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					intent.setComponent(name);
					intent.putExtra(Intent.EXTRA_TEXT, message);
					intent.putExtra(Intent.EXTRA_STREAM,
							Uri.fromFile(new File(imagePath)));
					context.startActivity(intent);
					break;
				}
			}
		} catch (final ActivityNotFoundException e) {
			Log.i("mufumbo", "no whatapps native", e);
		}
	}
}
