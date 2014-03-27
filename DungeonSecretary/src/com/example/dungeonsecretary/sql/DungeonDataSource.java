package com.example.dungeonsecretary.sql;

import java.util.ArrayList;
import java.util.List;

import com.example.dungeonsecretary.model.CharacterData;
import com.example.dungeonsecretary.model.StatData;
import com.example.dungeonsecretary.model.UserData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DungeonDataSource {
	
	//make it a singleton
	private static DungeonDataSource instance;
	private DungeonDataSource(Context context)
	{
		dbHelper = new MySQLiteHelper(context);
	}
	
	public static DungeonDataSource getInstance(Context context)
	{
		if(instance == null)
		{
			instance = new DungeonDataSource(context);
		}
		return instance;
	}
	
	
	//Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper; 
	//The fields in the user table
	private String[] userColumns = { MySQLiteHelper.USERS_COLUMN_ID, 
			MySQLiteHelper.USERS_COLUMN_GOOGLE_ACCOUNT, MySQLiteHelper.USERS_COLUMN_USER_NAME };
	//the fields in the character table
	private String[] characterColumns = { MySQLiteHelper.CHARACTERS_COLUMN_ID, 
			MySQLiteHelper.CHARACTERS_COLUMN_OWNER_ID, MySQLiteHelper.CHARACTERS_COLUMN_NAME,
			MySQLiteHelper.CHARACTERS_COLUMN_PUBLIC, MySQLiteHelper.CHARACTERS_COLUMN_SHARED, 
			MySQLiteHelper.CHARACTERS_COLUMN_SYSTEM};
	private String[] statColumns = {MySQLiteHelper.STATS_COLUMN_ID, 
			MySQLiteHelper.STATS_COLUMN_CHARACTER_ID, MySQLiteHelper.STATS_COLUMN_NAME,
			MySQLiteHelper.STATS_COLUMN_TYPE, MySQLiteHelper.STATS_COLUMN_VALUE};
	
	
	
	public void open() throws SQLException {
		Log.i("SQLSetup", "Calling getWritableDatabase");
		database = dbHelper.getWritableDatabase();
		Log.i("SQLSetup", "After getWritableDatabase");
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public void resetDatabase()
	{
		dbHelper.resetDatabase(database);
	}
	
	/**
	 * Inserts an already defined user into the database. 
	 * Also sets the ID of the user (so don't set that yourself)
	 * 
	 * @param user
	 * 		The user to insert into the database. 
	 */
	public void InsertUser(UserData user)
	{
		//TODO Duplication/error checking		
		//read values from the user
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.USERS_COLUMN_GOOGLE_ACCOUNT, user.getGoogleAccount());
		values.put(MySQLiteHelper.USERS_COLUMN_USER_NAME, user.getUserName());
		//insert it into the table and set the insert id
		long insertId = database.insert(MySQLiteHelper.TABLE_USERS,  null,  values);
		user.setId(insertId);
	}
	
	public void UpdateUser(UserData user)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.USERS_COLUMN_GOOGLE_ACCOUNT, user.getGoogleAccount());
		values.put(MySQLiteHelper.USERS_COLUMN_USER_NAME, user.getUserName());
		long id = user.getId();
		database.update(MySQLiteHelper.TABLE_USERS, values,  MySQLiteHelper.USERS_COLUMN_ID + " = " + id, null);
	}
		
	public UserData getUser(String gAccount) {
		//string for the where clause to compare both google account and user name
		String where = MySQLiteHelper.USERS_COLUMN_GOOGLE_ACCOUNT + " = " + gAccount;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS, userColumns, 
				where, null, null, null, null);
		cursor.moveToFirst();
		UserData foundUser = UserAtCursor(cursor);
		return foundUser;
	}
	
	public void deleteUser(long userId) {
		System.out.println("User deleted with id: " + userId);
		database.delete(MySQLiteHelper.TABLE_USERS, 
				MySQLiteHelper.USERS_COLUMN_ID + " = " + userId, null);		
	}
	
	public List<UserData> getAllUsers() {
		List<UserData> users = new ArrayList<UserData>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS, 
				userColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			UserData user = UserAtCursor(cursor);
			users.add(user);
			cursor.moveToNext();
		}
		//make sure to close the cursor
		cursor.close();
		return users;
	}
	
	private UserData UserAtCursor(Cursor cursor) {
		UserData user = new UserData();
		user.setId(cursor.getLong(0));
		user.setGoogleAccount(cursor.getString(1));
		user.setUserName(cursor.getString(2));
		return user;
	}
	
	public void InsertCharacter(CharacterData character)
	{
		//duplication/error checking
		//get data from character and insert it
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.CHARACTERS_COLUMN_OWNER_ID, character.getOwnerId());
		values.put(MySQLiteHelper.CHARACTERS_COLUMN_NAME, character.getName());
		values.put(MySQLiteHelper.CHARACTERS_COLUMN_PUBLIC, character.getPublic());
		values.put(MySQLiteHelper.CHARACTERS_COLUMN_SHARED, character.getShared());
		values.put(MySQLiteHelper.CHARACTERS_COLUMN_SYSTEM, character.getSystem());
		long insertId = database.insert(MySQLiteHelper.TABLE_CHARACTERS,  null,  values);
		character.setId(insertId);
	}

	public void UpdateCharacter(CharacterData character)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.CHARACTERS_COLUMN_OWNER_ID, character.getOwnerId());
		values.put(MySQLiteHelper.CHARACTERS_COLUMN_NAME, character.getName());
		values.put(MySQLiteHelper.CHARACTERS_COLUMN_PUBLIC, character.getPublic());
		values.put(MySQLiteHelper.CHARACTERS_COLUMN_SHARED, character.getShared());
		values.put(MySQLiteHelper.CHARACTERS_COLUMN_SYSTEM, character.getSystem());
		long id = character.getId();
		database.update(MySQLiteHelper.TABLE_CHARACTERS, values,  MySQLiteHelper.CHARACTERS_COLUMN_ID + " = " + id, null);
	}
	
	public CharacterData getCharacter(long ownerId, String characterName) {
		//string for the where clause to compare both owner name and character name
		String where = MySQLiteHelper.CHARACTERS_COLUMN_OWNER_ID + " = " + ownerId
				+ " AND " + MySQLiteHelper.CHARACTERS_COLUMN_NAME + " = " + characterName;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CHARACTERS, characterColumns, 
				where, null, null, null, null);
		cursor.moveToFirst();
		CharacterData foundCharacter = CharacterAtCursor(cursor);
		return foundCharacter;
	}
	
	public List<CharacterData> getAllCharacters() {
		List<CharacterData> characters = new ArrayList<CharacterData>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CHARACTERS, 
				characterColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			CharacterData chara = CharacterAtCursor(cursor);
			characters.add(chara);
			cursor.moveToNext();
		}
		//make sure to close the cursor
		cursor.close();
		return characters;
	}
	
	private CharacterData CharacterAtCursor(Cursor cursor) {
		CharacterData character = new CharacterData();
		character.setId(cursor.getLong(0));
		character.setOwnerId(cursor.getLong(1));
		character.setName(cursor.getString(2));
		character.setPublic((cursor.getInt(3) == 1));
		character.setShared((cursor.getInt(4) == 1));
		character.setSystem(cursor.getString(5));
		return character;
	}
	
	public void deleteCharacter(long id) {
		System.out.println("Character deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_CHARACTERS, 
				MySQLiteHelper.CHARACTERS_COLUMN_ID + " = " + id, null);		
	}
	
	public void InsertStat(StatData stat)
	{
		//duplication/error checking
		//get data from character and insert it
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.STATS_COLUMN_CHARACTER_ID, stat.getCharacterId());
		values.put(MySQLiteHelper.STATS_COLUMN_NAME, stat.getName());
		values.put(MySQLiteHelper.STATS_COLUMN_TYPE, stat.getType());
		values.put(MySQLiteHelper.STATS_COLUMN_VALUE, stat.getValue());
		long insertId = database.insert(MySQLiteHelper.TABLE_STATS,  null,  values);
		stat.setId(insertId);
	}
	
	public void UpdateStat(StatData stat)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.STATS_COLUMN_CHARACTER_ID, stat.getCharacterId());
		values.put(MySQLiteHelper.STATS_COLUMN_NAME, stat.getName());
		values.put(MySQLiteHelper.STATS_COLUMN_TYPE, stat.getType());
		values.put(MySQLiteHelper.STATS_COLUMN_VALUE, stat.getValue());
		long id = stat.getId();
		database.update(MySQLiteHelper.TABLE_STATS, values,  MySQLiteHelper.STATS_COLUMN_ID + " = " + id, null);
	}
	
	private StatData getStat(long characterId, String statName)
	{
		//string for the where clause to compare both owner name and character name
		String where = MySQLiteHelper.STATS_COLUMN_CHARACTER_ID + " = " + characterId
				+ " AND " + MySQLiteHelper.STATS_COLUMN_NAME + " = " + statName;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_STATS, statColumns, 
				where, null, null, null, null);
		cursor.moveToFirst();
		StatData foundStat = StatAtCursor(cursor);
		return foundStat;
	}
	
	public List<StatData> getAllStatsForCharacter(long characterId)
	{
		List<StatData> stats = new ArrayList<StatData>();
		String where = MySQLiteHelper.STATS_COLUMN_CHARACTER_ID + " = " + characterId;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_STATS, 
				statColumns, where, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			StatData stat = StatAtCursor(cursor);
			stats.add(stat);
			cursor.moveToNext();
		}
		//make sure to close the cursor
		cursor.close();
		return stats;
	}
	
	public void deleteStat(long id) {
		System.out.println("Stat deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_STATS, 
				MySQLiteHelper.STATS_COLUMN_ID + " = " + id, null);		
	}
	
	private StatData StatAtCursor(Cursor cursor) {
		StatData stat = new StatData();
		stat.setId(cursor.getLong(0));
		stat.setCharacterId(cursor.getLong(1));
		stat.setName(cursor.getString(2));
		stat.setType(cursor.getString(3));
		stat.setValue(cursor.getString(4));
		return stat;
	}
	
}
