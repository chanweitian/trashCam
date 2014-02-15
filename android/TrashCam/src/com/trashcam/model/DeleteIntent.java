package com.trashcam.model;

import java.io.File;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class DeleteIntent extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		Toast.makeText(this, "Deleting...", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Bundle bundle = intent.getExtras();
		String path = bundle.getString("path");
		Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
		File thisFile = new File(path);
		thisFile.delete();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	
	

}
