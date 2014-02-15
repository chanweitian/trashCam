package com.trashcam.model;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.trashcam.HashTable;
import com.trashcam.Utility;

public class DeleteFileModelManager {
	private static final String TAG = DeleteFileModelManager.class
			.getSimpleName();

	private static DeleteFileModelManager instance;

	public static LinkedHashMap<String, DeleteFileModel> toBeDeleted;

	private DeleteFileModelManager() {
		toBeDeleted = new LinkedHashMap<String, DeleteFileModel>();
		instance = this;
	}

	public static DeleteFileModelManager getInstance() {
		if (instance == null) {
			instance = new DeleteFileModelManager();
			String saves = HashTable.get_entry(HashTable.DELETABLES);
			if (saves != null && "".equals(saves)) {
				Gson gson = new Gson();
				toBeDeleted = new LinkedHashMap<String, DeleteFileModel>();
				for (DeleteFileModel d : gson.fromJson(saves,
						DeleteFileModel[].class)) {
					try {
						if (new File(d.fileUrl).exists()) {
							toBeDeleted.put(d.fileUrl, d);
						}
					} catch (NullPointerException e) {
						Log.v(TAG, " blank filename");
					}
				}
			}
		}
		return instance;
	}

	public static void save() {
		Gson gson = new Gson();
		DeleteFileModel[] data = extract();
		HashTable.insert_entry(HashTable.DELETABLES, gson.toJson(data));
	}

	private static DeleteFileModel[] extract() {
		DeleteFileModel[] data = new DeleteFileModel[toBeDeleted.size()];
		int x = 0;
		for (String s : toBeDeleted.keySet()) {
			data[x] = toBeDeleted.get(s);
			x++;
		}
		return data;
	}

	public void addFile(String path, int days, Context context) {
		addFile(new File(path), days, context, path);
	}

	public void addFile(File new_picture, int days, Context context, String path) {
		if (new_picture == null) {
			throw new IllegalStateException("new_picture NOT INITED");
		}
		if (toBeDeleted == null) {
			throw new IllegalStateException("TOBEDELETED NOT INITED");
		}
		removeIfExist(new_picture);
		toBeDeleted.put(new_picture.getAbsolutePath(), new DeleteFileModel(
				new_picture, days));

		Log.w("Here", "Here");
		Intent intent = new Intent();
		Calendar calendar = Calendar.getInstance();
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		intent.setClass(context, DeleteIntent.class);
		intent.putExtra("path", path);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.set(AlarmManager.RTC_WAKEUP,
				Utility.getDateAfterInLong(days) + 60000, pendingIntent);
		Log.w("There", "THere");

		/*
		 * TODO: save to database also
		 */

	}

	private void removeIfExist(File new_picture) {
		if (toBeDeleted.containsKey(new_picture.getAbsolutePath())) {
			toBeDeleted.remove(new_picture.getAbsolutePath());
			/*
			 * TODO: delete from database
			 */
		}
	}

	public void deleteFileNow(String path) {
		deleteFileNow(new File(path));
	}

	public void deleteFileNow(File file_to_be_deleted) {
		removeIfExist(file_to_be_deleted);
		boolean successful = file_to_be_deleted.delete();
	}

	public static String getJson() {
		Gson gson = new Gson();
		return gson.toJson(extract());
	}
}
