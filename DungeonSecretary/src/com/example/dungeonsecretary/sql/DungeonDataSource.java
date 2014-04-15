package com.example.dungeonsecretary.sql;

import java.util.ArrayList;
import java.util.List;

import com.example.dungeonsecretary.model.CharacterData;
import com.example.dungeonsecretary.model.SheetFieldData;
import com.example.dungeonsecretary.model.StatData;
import com.example.dungeonsecretary.model.UserData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DungeonDataSource {
	
	public static final long dbNullNum = -100;
	
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
			instance.open();
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
	private String[] fieldColumns = {MySQLiteHelper.FIELDS_COLUMN_CHAR_ID, 
			MySQLiteHelper.FIELDS_COLUMN_INDEX, MySQLiteHelper.FIELDS_COLUMN_STAT_ID,
			MySQLiteHelper.FIELDS_COLUMN_LABEL};
	
	//The current logged in user
	private UserData currentUser;
	//The currently selected character
	private CharacterData currentCharacter;
	
	
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
	public void insertUser(UserData user)
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
	
	public void updateUser(UserData user)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.USERS_COLUMN_GOOGLE_ACCOUNT, user.getGoogleAccount());
		values.put(MySQLiteHelper.USERS_COLUMN_USER_NAME, user.getUserName());
		long id = user.getId();
		database.update(MySQLiteHelper.TABLE_USERS, values,  MySQLiteHelper.USERS_COLUMN_ID + " = " + id, null);
	}
		
	public UserData getUser(String gAccount) {
		//string for the where clause to compare both google account and user name
		String where = MySQLiteHelper.USERS_COLUMN_GOOGLE_ACCOUNT + " = '" + gAccount + "'";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS, userColumns, 
				where, null, null, null, null);
		UserData foundUser;
		if(cursor.getCount() == 0)
		{
			foundUser = null;
		} else {
			cursor.moveToFirst();
			foundUser = userAtCursor(cursor);
		}
		cursor.close();
		return foundUser;
	}
	
	public UserData getUser(long userId) {
		//string for the where clause to compare both google account and user name
		String where = MySQLiteHelper.USERS_COLUMN_ID + " = " + userId;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS, userColumns, 
				where, null, null, null, null);
		UserData foundUser;
		if(cursor.getCount() == 0)
		{
			foundUser = null;
		} else {
			cursor.moveToFirst();
			foundUser = userAtCursor(cursor);
		}
		cursor.close();
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
			UserData user = userAtCursor(cursor);
			users.add(user);
			cursor.moveToNext();
		}
		//make sure to close the cursor
		cursor.close();
		return users;
	}
	
	private UserData userAtCursor(Cursor cursor) {
		UserData user = new UserData();
		if(cursor.isAfterLast())
		{
			user = null;
		}else
		{
		user.setId(cursor.getLong(0));
		user.setGoogleAccount(cursor.getString(1));
		user.setUserName(cursor.getString(2));
		}
		return user;
	}
	
	public boolean insertCharacter(CharacterData character)
	{
		//duplication/error checking
		CharacterData duplicate = getCharacter(character.getId(), character.getName());
		if(duplicate != null)
		{
			Log.e("SQL", "Attempt to insert duplicate character " + character.getName());
			return false;
		}
		else
			{
			//get data from character and insert it
			ContentValues values = new ContentValues();
			values.put(MySQLiteHelper.CHARACTERS_COLUMN_OWNER_ID, character.getOwnerId());
			values.put(MySQLiteHelper.CHARACTERS_COLUMN_NAME, character.getName());
			values.put(MySQLiteHelper.CHARACTERS_COLUMN_PUBLIC, character.getPublic());
			values.put(MySQLiteHelper.CHARACTERS_COLUMN_SHARED, character.getShared());
			values.put(MySQLiteHelper.CHARACTERS_COLUMN_SYSTEM, character.getSystem());
			long insertId = database.insert(MySQLiteHelper.TABLE_CHARACTERS,  null,  values);
			character.setId(insertId);
			return true;
		}
	}

	public void updateCharacter(CharacterData character)
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
				+ " AND " + MySQLiteHelper.CHARACTERS_COLUMN_NAME + " = '" + characterName + "'";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CHARACTERS, characterColumns, 
				where, null, null, null, null);
		cursor.moveToFirst();
		if (cursor.getCount() <= 0)
		{
			return null;
		}
		CharacterData foundCharacter = characterAtCursor(cursor);
		cursor.close();
		return foundCharacter;
	}
	
	public CharacterData getCharacter(long characterId) {
		//string for the where clause to compare both owner name and character name
		String where = MySQLiteHelper.CHARACTERS_COLUMN_ID + " = " + characterId;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CHARACTERS, characterColumns, 
				where, null, null, null, null);
		cursor.moveToFirst();
		CharacterData foundCharacter = characterAtCursor(cursor);
		cursor.close();
		return foundCharacter;
	}
	
	public List<CharacterData> getAllCharacters() {
		List<CharacterData> characters = new ArrayList<CharacterData>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CHARACTERS, 
				characterColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			CharacterData chara = characterAtCursor(cursor);
			characters.add(chara);
			cursor.moveToNext();
		}
		//make sure to close the cursor
		cursor.close();
		return characters;
	}
	
	public void DuplicateCharacter(CharacterData baseChar, String newName, long newOwnerId)
	{
		//first make and insert the new character
		CharacterData newChar = new CharacterData();
		newChar.setName(newName);
		newChar.setOwnerId(newOwnerId);
		newChar.setSystem(baseChar.getSystem());
		newChar.setPublic(false);
		newChar.setShared(false);
		//get data from character and insert it
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.CHARACTERS_COLUMN_OWNER_ID, newChar.getOwnerId());
		values.put(MySQLiteHelper.CHARACTERS_COLUMN_NAME, newChar.getName());
		values.put(MySQLiteHelper.CHARACTERS_COLUMN_PUBLIC, newChar.getPublic());
		values.put(MySQLiteHelper.CHARACTERS_COLUMN_SHARED, newChar.getShared());
		values.put(MySQLiteHelper.CHARACTERS_COLUMN_SYSTEM, newChar.getSystem());
		long insertId = database.insert(MySQLiteHelper.TABLE_CHARACTERS,  null,  values);
		newChar.setId(insertId);
		
		//duplicate all of the stats
		List<StatData> baseStats = getAllStatsForCharacter(baseChar.getId());
		for(int i = 0; i < baseStats.size(); i++)
		{
			StatData stat = baseStats.get(i);
			stat.setCharacterId(newChar.getId());
			insertStat(stat);
		}
		//duplicate all of the sheet fields
		List<SheetFieldData> baseFields = getAllFieldsForCharacter(baseChar.getId());
		for(int i = 0; i < baseFields.size(); i++)
		{
			SheetFieldData field = baseFields.get(i);
			field.setCharId(newChar.getId());
			//if it was a reference to a stat, need to find the new stat
			if(field.getStatId() != dbNullNum)
			{
				StatData baseStat = getStat(field.getStatId());
				StatData newStat = getStat(newChar.getId(), baseStat.getName());
				field.setStatId(newStat.getId());
			}
			insertSheetField(field);
		}
	}
	
	private CharacterData characterAtCursor(Cursor cursor) {
		if(cursor.isAfterLast())
		{
			return null;
		}
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
		//delete all of its fields
		List<SheetFieldData> fields = getAllFieldsForCharacter(id);
		for(int i = 0; i < fields.size(); i++)
		{
			deleteField(fields.get(i).getCharId(), fields.get(i).getIndex());
		}
		//delete all of its stats
		List<StatData> stats = getAllStatsForCharacter(id);
		for(int i = 0; i < stats.size(); i++)
		{
			deleteStat(stats.get(i).getId());
		}
		//delete character
		database.delete(MySQLiteHelper.TABLE_CHARACTERS, 
				MySQLiteHelper.CHARACTERS_COLUMN_ID + " = " + id, null);
		//delete all of the stats and fields for this character
	}
	
	public long insertStat(StatData stat)
	{
		//duplication/error checking
		//check for same charId and name
		//get data from character and insert it
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.STATS_COLUMN_CHARACTER_ID, stat.getCharacterId());
		values.put(MySQLiteHelper.STATS_COLUMN_NAME, stat.getName());
		values.put(MySQLiteHelper.STATS_COLUMN_TYPE, stat.getType());
		values.put(MySQLiteHelper.STATS_COLUMN_VALUE, stat.getValue());
		long insertId = database.insert(MySQLiteHelper.TABLE_STATS,  null,  values);
		stat.setId(insertId);
		return insertId;
	}
	
	public void updateStat(StatData stat)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.STATS_COLUMN_CHARACTER_ID, stat.getCharacterId());
		values.put(MySQLiteHelper.STATS_COLUMN_NAME, stat.getName());
		values.put(MySQLiteHelper.STATS_COLUMN_TYPE, stat.getType());
		values.put(MySQLiteHelper.STATS_COLUMN_VALUE, stat.getValue());
		long id = stat.getId();
		database.update(MySQLiteHelper.TABLE_STATS, values,  MySQLiteHelper.STATS_COLUMN_ID + " = " + id, null);
	}
	
	public StatData getStat(long characterId, String statName)
	{
		//string for the where clause to compare both owner name and character name
		String where = MySQLiteHelper.STATS_COLUMN_CHARACTER_ID + " = " + characterId
				+ " AND " + MySQLiteHelper.STATS_COLUMN_NAME + " = '" + statName + "'";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_STATS, statColumns, 
				where, null, null, null, null);
		cursor.moveToFirst();
		StatData foundStat = StatAtCursor(cursor);
		cursor.close();
		return foundStat;
	}
	
	public StatData getStat(long statId)
	{
		//string for the where clause to compare both owner name and character name
		String where = MySQLiteHelper.STATS_COLUMN_ID + " = " + statId;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_STATS, statColumns, 
				where, null, null, null, null);
		cursor.moveToFirst();
		StatData foundStat = StatAtCursor(cursor);
		cursor.close();
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
		if(cursor.isAfterLast())
		{
			return null;
		}
		StatData stat = new StatData();
		stat.setId(cursor.getLong(0));
		stat.setCharacterId(cursor.getLong(1));
		stat.setName(cursor.getString(2));
		stat.setType(cursor.getString(3));
		stat.setValue(cursor.getString(4));
		return stat;
	}

	public boolean checkForUser(String gAccount)
	{
		String where = MySQLiteHelper.USERS_COLUMN_GOOGLE_ACCOUNT + " = '" + gAccount + "'";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS, userColumns, 
				where, null, null, null, null);		
		if(cursor.getCount() == 0)
		{
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}
	
	///////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////
	public void insertSheetField(SheetFieldData field)
	{
		SheetFieldData dupCheck = getSheetField(field.getCharId(), field.getIndex());
		if(dupCheck == null)
		{
			//duplication/error checking
			//get data from field and insert it
			ContentValues values = new ContentValues();
			values.put(MySQLiteHelper.FIELDS_COLUMN_CHAR_ID, field.getCharId());
			values.put(MySQLiteHelper.FIELDS_COLUMN_INDEX, field.getIndex());
			values.put(MySQLiteHelper.FIELDS_COLUMN_STAT_ID, field.getStatId());
			if(field.getStatId() == dbNullNum)
			{
				values.put(MySQLiteHelper.FIELDS_COLUMN_LABEL, field.getLabel());
			}
			database.insert(MySQLiteHelper.TABLE_CHAR_SHEET_FIELDS,  null,  values);	
		}
		else
		{
			updateSheetField(field);
		}
	}
	
	public void updateSheetField(SheetFieldData field)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.FIELDS_COLUMN_CHAR_ID, field.getCharId());
		values.put(MySQLiteHelper.FIELDS_COLUMN_INDEX, field.getIndex());
		values.put(MySQLiteHelper.FIELDS_COLUMN_STAT_ID, field.getStatId());
		if(field.getStatId() == dbNullNum)
		{
			values.put(MySQLiteHelper.FIELDS_COLUMN_LABEL, field.getLabel());
		}
		
		String where = MySQLiteHelper.FIELDS_COLUMN_CHAR_ID + " = " + field.getCharId()
				+ " AND " + MySQLiteHelper.FIELDS_COLUMN_INDEX + " = " + field.getIndex();
		
		database.update(MySQLiteHelper.TABLE_CHAR_SHEET_FIELDS, values,  where, null);
	}
	
	public SheetFieldData getSheetField(long characterId, long index)
	{
		//string for the where clause to compare both owner name and character name
		String where = MySQLiteHelper.FIELDS_COLUMN_CHAR_ID + " = " + characterId
				+ " AND " + MySQLiteHelper.FIELDS_COLUMN_INDEX + " = " + index;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CHAR_SHEET_FIELDS, fieldColumns, 
				where, null, null, null, null);
		cursor.moveToFirst();
		SheetFieldData foundfield = FieldAtCursor(cursor);
		cursor.close();
		return foundfield;
	}
	
	public List<SheetFieldData> getAllFieldsForCharacter(long characterId)
	{
		List<SheetFieldData> fields = new ArrayList<SheetFieldData>();
		String where = MySQLiteHelper.FIELDS_COLUMN_CHAR_ID + " = " + characterId;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CHAR_SHEET_FIELDS, 
				fieldColumns, where, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			SheetFieldData field = FieldAtCursor(cursor);
			fields.add(field);
			cursor.moveToNext();
		}
		//make sure to close the cursor
		cursor.close();
		return fields;
	}
	
	public void deleteField(long charId, long index) {
		
		String where = MySQLiteHelper.FIELDS_COLUMN_CHAR_ID + " = " + charId
				+ " AND " + MySQLiteHelper.FIELDS_COLUMN_INDEX + " = " + index;
		database.delete(MySQLiteHelper.TABLE_CHAR_SHEET_FIELDS, where, null);		
	}
	
	private SheetFieldData FieldAtCursor(Cursor cursor) {
		if(cursor.isAfterLast())
		{
			return null;
		}
		SheetFieldData field = new SheetFieldData();
		field.setCharId(cursor.getLong(0));
		field.setIndex(cursor.getLong(1));
		field.setStatId(cursor.getLong(2));
		field.setLabel(cursor.getString(3));
		return field;
	}
	
	public void setCurrentUser(String gAccount, String userName)
	{
		UserData user;
		if(!checkForUser(gAccount))
		{
			user = new UserData();
			user.setGoogleAccount(gAccount);
			user.setUserName(userName);
			insertUser(user);
		}
		else
		{
			user = getUser(gAccount);
		}
		currentUser = user;
	}
	
	public UserData getCurrentUser()
	{
		return currentUser;
	}

	public void setCurrentCharacter(long characterId)
	{
		currentCharacter = getCharacter(characterId);
	}
	
	public CharacterData getCurrentCharacter()
	{
		return currentCharacter;
	}
	
	
}
