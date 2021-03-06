package com.example.dungeonsecretary.sql;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	/* 
	 * Users table 
	 */
	public static final String TABLE_USERS = "users";
	public static final String USERS_COLUMN_ID = "_id";
	public static final String USERS_COLUMN_GOOGLE_ACCOUNT = "google_account";
	public static final String USERS_COLUMN_USER_NAME = "user_name";
	//Statement to create users table
	public static final String USERS_CREATE = "create table " 
			+ TABLE_USERS + "(" 
			+ USERS_COLUMN_ID + " integer primary key autoincrement, "
			+ USERS_COLUMN_GOOGLE_ACCOUNT + " text not null, " 
			+ USERS_COLUMN_USER_NAME + " text not null);";
	/* 
	 * Characters table 
	 */
	public static final String TABLE_CHARACTERS = "characters";
	public static final String CHARACTERS_COLUMN_ID = "_id";
	public static final String CHARACTERS_COLUMN_OWNER_ID = "owner_id";
	public static final String CHARACTERS_COLUMN_NAME = "name";
	public static final String CHARACTERS_COLUMN_PUBLIC = "public";
	public static final String CHARACTERS_COLUMN_SHARED = "shared";
	public static final String CHARACTERS_COLUMN_SYSTEM = "system";
	//Statement to create characters table
	public static final String CHARACTERS_CREATE = "create table " 
			+ TABLE_CHARACTERS + "(" 
			+ CHARACTERS_COLUMN_ID + " integer primary key autoincrement, "
			+ CHARACTERS_COLUMN_OWNER_ID + " integer not null, " 
			+ CHARACTERS_COLUMN_NAME + " text not null, "
			+ CHARACTERS_COLUMN_PUBLIC + " int, "
			+ CHARACTERS_COLUMN_SHARED + " int, "
			+ CHARACTERS_COLUMN_SYSTEM + " text"
			+ ");";
	/* 
	 * Stats table 
	 */
	public static final String TABLE_STATS = "stats";
	public static final String STATS_COLUMN_ID = "_id";
	public static final String STATS_COLUMN_CHARACTER_ID = "character_id";
	public static final String STATS_COLUMN_NAME = "name";
	public static final String STATS_COLUMN_TYPE = "type";
	public static final String STATS_COLUMN_VALUE = "value";
	//Statement to create stats table
	public static final String STATS_CREATE = "create table " 
			+ TABLE_STATS + "(" 
			+ STATS_COLUMN_ID + " integer primary key autoincrement, "
			+ STATS_COLUMN_CHARACTER_ID + " integer not null, "
			+ STATS_COLUMN_NAME + " text not null, "
			+ STATS_COLUMN_TYPE + " text not null, "
			+ STATS_COLUMN_VALUE + " text"
			+ ");";	
	/* 
	 * Charsheet table 
	 */
	public static final String TABLE_CHAR_SHEET_FIELDS = "char_sheet";
	public static final String FIELDS_COLUMN_CHAR_ID = "char_id";
	public static final String FIELDS_COLUMN_INDEX = "grid_index";
	public static final String FIELDS_COLUMN_STAT_ID = "stat_id";
	public static final String FIELDS_COLUMN_LABEL = "label"; 
	public static final String FIELDS_CREATE = "create table " 
			+ TABLE_CHAR_SHEET_FIELDS + "("
			+ FIELDS_COLUMN_CHAR_ID + " integer not null, "
			+ FIELDS_COLUMN_INDEX + " integer not null, "
			+ FIELDS_COLUMN_STAT_ID + " integer, " 
			+ FIELDS_COLUMN_LABEL + " text" + ");";
	
	private static final String DATABASE_NAME = "dungeonSecretary.db";
	private static final int DATABASE_VERSION = 1;
	
	
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/**
	 * Alternate constructor for testing so you don't overwrite the real db
	 */
	public MySQLiteHelper(Context context, String dbName)
	{
		super(context, dbName, null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.i("SQLSetup", "Creating databases");
		database.execSQL(USERS_CREATE);
		database.execSQL(CHARACTERS_CREATE);
		database.execSQL(STATS_CREATE);
		database.execSQL(FIELDS_CREATE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		Log.w(MySQLiteHelper.class.getName(), 
				"Not doing upgrades yet");
		/*
		Log.w(MySQLiteHelper.class.getName(), 
				"Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
		//onCreate(db);*/
	}
	

	public void resetDatabase(SQLiteDatabase db)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHARACTERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAR_SHEET_FIELDS);
		onCreate(db);
	}
	
	
}
