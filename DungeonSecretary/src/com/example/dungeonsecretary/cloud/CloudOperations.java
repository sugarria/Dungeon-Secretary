package com.example.dungeonsecretary.cloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.example.dungeonsecretary.model.SheetFieldData;
import com.example.dungeonsecretary.model.StatData;
import com.example.dungeonsecretary.model.UserData;
import com.example.dungeonsecretary.sql.DungeonDataSource;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class CloudOperations {
	public static void sendCharacterToCloud(String name, String ownerEmail, String ownerName, String system, List<StatData> stats, List<SheetFieldData> sheetFields, boolean sharedFlag, boolean publicFlag) {
    	deleteCharacterFromCloud(name, ownerEmail, system);
    	
      	ParseObject characterObject = new ParseObject("Character");
		characterObject.put("Name", name);
		characterObject.put("Owner", ownerEmail);
		characterObject.put("System", system);
		characterObject.put("Shared", sharedFlag);
		characterObject.put("Public", publicFlag);
		characterObject.put("OwnerName", ownerName);
		characterObject.saveInBackground();
		
		Map<Long, ParseObject> idStatObjectMap = new HashMap<Long, ParseObject>();
		for(int i = 0; i < stats.size(); i++)
		{
			ParseObject statObject = new ParseObject("Stat");
			
			statObject.put("ParentID", characterObject);
			statObject.put("Name", stats.get(i).getName());
			statObject.put("Type", stats.get(i).getType());
			statObject.put("Value", stats.get(i).getValue());
			statObject.saveInBackground();
			
			idStatObjectMap.put(stats.get(i).getId(), statObject);
		}
		for (int i = 0; i < sheetFields.size(); i++) {
			ParseObject sheetFieldObject = new ParseObject("SheetField");
			sheetFieldObject.put("Character", characterObject);
			if(sheetFields.get(i).getStatId() >= 0) {
				sheetFieldObject.put("Stat", idStatObjectMap.get(sheetFields.get(i).getStatId()));
			}
			sheetFieldObject.put("Index", sheetFields.get(i).getIndex());
			if(sheetFields.get(i).getLabel() != null) {
				sheetFieldObject.put("Label", sheetFields.get(i).getLabel());
			}
			sheetFieldObject.saveInBackground();
		}
    }
	
	public static void sendCharacterToCloud(String name, long ownerId, String system, boolean sharedFlag, boolean publicFlag, Context context) {
		List<StatData> stats;
		List<SheetFieldData> sheetFields;
		DungeonDataSource db = DungeonDataSource.getInstance(context);
		UserData owner = db.getUser(ownerId);
		
		stats = db.getAllStatsForCharacter(db.getCharacter(ownerId, name).getId());
		sheetFields = db.getAllFieldsForCharacter(db.getCharacter(ownerId, name).getId());
		
		sendCharacterToCloud(name, owner.getGoogleAccount(), owner.getUserName(), system, stats, sheetFields, sharedFlag, publicFlag);
	}
	
	public static void deleteCharacterFromCloud(String name, String ownerEmail, String system) {
		ParseQuery<ParseObject> qResults = ParseQuery.getQuery("Character");
		qResults.whereEqualTo("Name", name);
		qResults.whereEqualTo("Owner", ownerEmail);
		qResults.whereEqualTo("System", system);
		qResults.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> characterList, ParseException e) {
				if (e == null && characterList.size() != 0) {
		            ParseQuery<ParseObject> statQuery = ParseQuery.getQuery("Stat");
		    		statQuery.whereEqualTo("ParentID", characterList.get(0));
		    		statQuery.findInBackground(new FindCallback<ParseObject>() {
		    			public void done(List<ParseObject> statList, ParseException e) {
		    		        if (e == null) {  // successful query
		    		            for (ParseObject stat : statList)
		    		            {
		    		            	stat.deleteInBackground();
		    		            }
		    		        } else {
		    		            e.printStackTrace();
		    		        }
		    		    }
		    		});
		    		
		    		ParseQuery<ParseObject> sheetFieldQuery = ParseQuery.getQuery("SheetField");
		    		sheetFieldQuery.whereEqualTo("Character", characterList.get(0));
		    		sheetFieldQuery.findInBackground(new FindCallback<ParseObject>() {
		    			public void done(List<ParseObject> sheetFieldList, ParseException e) {
		    				if (e == null) {
		    					for (ParseObject sheetField : sheetFieldList) {
		    						sheetField.deleteInBackground();
		    					}
		    				} else {
		    					e.printStackTrace();
		    				}
		    			}
		    		});
		            characterList.get(0).deleteInBackground();
		        }
			}
		});
	}
	
	public static void addUserToCloud(Context context) {
		DungeonDataSource db = DungeonDataSource.getInstance(context);
		ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("User");		
		userQuery.whereEqualTo("GPlusEmail", db.getCurrentUser().getGoogleAccount());
		try {
			List<ParseObject> userList = userQuery.find();
			if (userList.size() == 0)
			{
				ParseObject user = new ParseObject("User");
				user.put("UserName", db.getCurrentUser().getUserName());
				user.put("GPlusEmail", db.getCurrentUser().getGoogleAccount());
				user.saveInBackground();
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
	}
	
	public static boolean addCurrentUserAsBuddy(Context context, String gAccountEmail) {
		// adds the buddy pairing to the cloud and tells you whether it was successful or not (false indicates entered email does not exist in cloud)
		boolean success = false;
		DungeonDataSource db = DungeonDataSource.getInstance(context);
		ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("User");		
		userQuery.whereEqualTo("GPlusEmail", gAccountEmail);
		try {
			List<ParseObject> userList = userQuery.find();
			if (userList.size() > 0)
			{
				success = true;
				// TODO: check if this pair already exists (if i have time...)
				ParseObject buddyPair = new ParseObject("UserFriends");
				buddyPair.put("User", gAccountEmail);
				buddyPair.put("IsFriendsWith", db.getCurrentUser().getGoogleAccount());
				buddyPair.saveInBackground();
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		return success;
	}
}
