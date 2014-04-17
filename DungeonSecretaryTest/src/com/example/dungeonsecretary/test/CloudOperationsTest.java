package com.example.dungeonsecretary.test;

import java.util.ArrayList;
import java.util.List;

import com.example.dungeonsecretary.cloud.CloudOperations;
import com.example.dungeonsecretary.model.CharacterData;
import com.example.dungeonsecretary.model.SheetFieldData;
import com.example.dungeonsecretary.model.StatData;
import com.example.dungeonsecretary.sql.DungeonDataSource;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

public class CloudOperationsTest extends AndroidTestCase {
	DungeonDataSource dbData;
	RenamingDelegatingContext context;
	
	protected void setUp() throws Exception {
		super.setUp();
		context	= new RenamingDelegatingContext(getContext(), "test_");
		
		dbData = DungeonDataSource.TESTgetInstance(context, "testDB.db");
		Log.d("UnitTesting", "set up");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSendCharacterToCloudStringStringStringStringListOfStatDataListOfSheetFieldDataBooleanBoolean() {
		CloudOperations.sendCharacterToCloud("bring home", "testemail@ihatethis.com", "why not zoidberg", "the bacon", new ArrayList<StatData>(), new ArrayList<SheetFieldData>(), true, true);
		ParseQuery<ParseObject> charQuery = ParseQuery.getQuery("Character");
		charQuery.whereEqualTo("Name", "bring home");
		charQuery.whereEqualTo("Owner", "testemail@ihatethis.com");
		charQuery.whereEqualTo("System", "the bacon");
		charQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> charList, ParseException e) {
				if (e == null) {
					if (charList.size() > 0)
					{
						Log.d("UnitTesting", "character added");
						
					}
				}
			}
			
		});
		CloudOperations.deleteCharacterFromCloud("bring home", "testemail@ihatethis.com", "the bacon");
	}

	public void testSendCharacterToCloudStringLongStringBooleanBooleanContext() {
		CharacterData newChar = new CharacterData();
		newChar.setName("why not zoidberg");
		newChar.setPublic(true);
		newChar.setShared(true);
		newChar.setSystem("woop woop woop woop woop");
		newChar.setOwnerId(dbData.getCurrentUser().getId());
		dbData.insertCharacter(newChar);
		CloudOperations.sendCharacterToCloud("why not zoidberg", dbData.getCurrentUser().getId(), "woop woop woop woop woop", true, true, context);	
		ParseQuery<ParseObject> charQuery = ParseQuery.getQuery("Character");
		charQuery.whereEqualTo("Name", "why not zoidberg");
		charQuery.whereEqualTo("Owner", dbData.getCurrentUser().getGoogleAccount());
		charQuery.whereEqualTo("System", "woop woop woop woop woop");
		charQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> charList, ParseException e) {
				if (e == null) {
					if (charList.size() > 0)
					{
						Log.d("UnitTesting", "character added");
						
					}
				}
			}
			
		});

		CloudOperations.deleteCharacterFromCloud("why not zoidberg", dbData.getCurrentUser().getGoogleAccount(), "woop woop woop woop woop");
	}
	
	public void testAddUserToCloud() {
		CloudOperations.addUserToCloud(context);
		Log.d("UnitTesting", "user added");
	}

	public void testAddCurrentUserAsBuddy() {
		CloudOperations.addCurrentUserAsBuddy(context, "doctor@zoidberg.com");
		ParseQuery<ParseObject> friendQuery = ParseQuery.getQuery("UserFriends");
		friendQuery.whereEqualTo("User", "doctor@zoidberg.com");
		friendQuery.whereEqualTo("IsFriendsWith", dbData.getCurrentUser().getGoogleAccount());
		friendQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> friendList, ParseException e) {
				if (e == null) {
					if (friendList.size() > 0)
					{
						Log.d("UnitTesting", "character added");
						
					}
				}
			}
			
		});
		
	}

}
