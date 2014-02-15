package com.trashcam;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class HashTable extends MainDatabase {

	public static final String HASH_TABLE_DUMP = "dump";
	public static final String HASH_COLUMN_ID = "_id";
	public static final String HASH_COLUMN_CONTENT = "content";
	public static final String DELETABLES = "DELETABLES";
	public static final String[] COLUMN_DATA = { HASH_COLUMN_CONTENT };

	static final String TABLE_CREATE = "create table " + HASH_TABLE_DUMP + "("
			+ HASH_COLUMN_ID + " text not null, " + HASH_COLUMN_CONTENT
			+ " text not null);";

	public static void insert_entry(String entry_id, String data) {
		ContentValues values = new ContentValues();
		values.put(HASH_COLUMN_ID, entry_id);
		values.put(HASH_COLUMN_CONTENT, data);
		String before = get_entry(entry_id);
		Log.wtf("Before", before);
		if (before != null) {
			getDatabase().delete(HASH_TABLE_DUMP, HASH_COLUMN_ID + "=?",
					new String[] { entry_id });
		}

		int res = (int) getDatabase().replace(HASH_TABLE_DUMP, null, values);

		Log.wtf("After", get_entry(entry_id));
		Log.v("result code from database operation", res + "");
	}

	public static String get_entry(String entry_id) {
		Cursor cursor = null;
		try {
			cursor = getDatabase().query(HASH_TABLE_DUMP, COLUMN_DATA,
					HASH_COLUMN_ID + "='" + entry_id + "'", null, null, null,
					null);
			cursor.moveToFirst();
			if (cursor.getCount() != 0) {
				return cursor.getString(0);
			} else {
				return null;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
}
