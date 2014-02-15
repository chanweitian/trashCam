package com.trashcam;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class MainDatabase {
	private static SQLiteDatabase database;
	private static DBHelper helper;

	public static void initMainDB(Context context){
		helper = new DBHelper(context);
	}
	/*
	 * @params context can be null if its not the first time singleton is being
	 * init
	 */
	public static DBHelper getHelper() {
		if (helper == null)
			throw new IllegalStateException(
					"You have tried to access a helper without first initializing with context");
		return helper;
	}
	
	public static void clearDB(){
		helper.clearDatabase(getDatabase());
	}
	
	public static SQLiteDatabase getDatabase() {
		if (helper == null)
			throw new IllegalStateException(
					"You have tried to access a database without first initializing the helper with context");
		if (database == null) {
			database = helper.getWritableDatabase();
		}
		return database;
	}

	protected static void close() {
		helper.close();
	}
}
