package com.trashcam;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	public static final String TAG = DBHelper.class.getSimpleName();
	private static final String DATABASE_NAME = "hashmap.common.trash.db";
	private static final int DATABASE_VERSION = 1;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(HashTable.TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(TAG, "Upgrading Database from " + oldVersion + " to "
				+ newVersion);
		clearDatabase(database);
	}

	public void clearDatabase(SQLiteDatabase database) {
		database.execSQL("DROP TABLE IF EXISTS " + HashTable.HASH_TABLE_DUMP);
		onCreate(database);
	}
}
