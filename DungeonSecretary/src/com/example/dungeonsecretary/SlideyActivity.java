package com.example.dungeonsecretary;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.dungeonsecretary.adapter.CharacterDrawerListAdapter;
import com.example.dungeonsecretary.cloud.CloudOperations;
import com.example.dungeonsecretary.interfaces.DialogListener;
import com.example.dungeonsecretary.model.CharacterData;
import com.example.dungeonsecretary.model.CharacterDrawerItem;
import com.example.dungeonsecretary.model.SheetFieldData;
import com.example.dungeonsecretary.model.StatData;
import com.example.dungeonsecretary.model.UserData;
import com.example.dungeonsecretary.sql.DungeonDataSource;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;


public class SlideyActivity extends FragmentActivity implements OnClickListener, DialogListener{

	private DrawerLayout leftMDrawerLayout, rightMDrawerLayout;
	private ListView leftMDrawerList, rightMDrawerList;
	private ActionBarDrawerToggle leftMDrawerToggle, rightMDrawerToggle;
	private DungeonDataSource dbData;
	private LinearLayout leftDrawer, rightDrawer;
	private Button btnNewChar, btnAddBuddy;
	
	//nav drawer title
	private CharSequence leftMDrawerTitle, rightMDrawerTitle;
	
	//used to store app title
	private CharSequence mTitle;
	
	//left slide menu items
	private List<CharacterData> allCharacters;
	
	private ArrayList<CharacterDrawerItem> leftCharDrawerItems;
	private CharacterDrawerListAdapter charLeftAdapter;
	
	//right slide menu items
	private List<CharacterData> activeCharacters;
	private ArrayList<CharacterDrawerItem> rightCharDrawerItems;
	private CharacterDrawerListAdapter charRightAdapter;
	private CharacterData tempChar;
	private String[] buddies;
	private boolean dirtySearch = false;
		
	private void fillCharacterList()
	{
		allCharacters = dbData.getAllCharacters();
		leftCharDrawerItems = new ArrayList<CharacterDrawerItem>();
		for(int i = 0; i < allCharacters.size(); i++)
		{
			leftCharDrawerItems.add(new CharacterDrawerItem(allCharacters.get(i), dbData.getUser(allCharacters.get(i).getOwnerId()).getUserName()));
		}

		charLeftAdapter = new CharacterDrawerListAdapter(getApplicationContext(), leftCharDrawerItems);
        leftMDrawerList.setAdapter(charLeftAdapter);
	}
	
	private void setupLeftDrawer()
	{
		leftMDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		leftMDrawerList = (ListView) findViewById(R.id.drawer_left_list);
		leftDrawer = (LinearLayout) findViewById(R.id.drawer_left);
		fillCharacterList();
        
        leftMDrawerToggle = new ActionBarDrawerToggle(this, leftMDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
 
            public void onDrawerOpened(View drawerView) {
            	fillCharacterList();
            	Log.i("SlideyActivity","inonDrawerOpened");
                getActionBar().setTitle(leftMDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        leftMDrawerLayout.setDrawerListener(leftMDrawerToggle); 

        leftMDrawerList.setOnItemClickListener(new SlideMenuClickListener());
	}
	
	private void setupRightDrawer()
	{
		
		rightMDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        rightMDrawerList = (ListView) findViewById(R.id.drawer_right_list);
        rightDrawer = (LinearLayout) findViewById(R.id.drawer_right);
        sharedCharacterQuery();
        
        rightMDrawerToggle = new ActionBarDrawerToggle(this, rightMDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
 
            public void onDrawerOpened(View drawerView) {
            	sharedCharacterQuery();
                getActionBar().setTitle(rightMDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        rightMDrawerLayout.setDrawerListener(rightMDrawerToggle); 

        rightMDrawerList.setOnItemClickListener(new RightSlideMenuClickListener());
        
	}
	
	private void SampleDBEntries()
	{
		UserData user = dbData.getCurrentUser();

		CharacterData test1 = new CharacterData();
		CharacterData test2 = new CharacterData();
		CharacterData test3 = new CharacterData();
		
		
		test1.setName("Character1");
		test2.setName("Character2");
		test3.setName("Character3");
				
		test1.setOwnerId(user.getId());
		test2.setOwnerId(user.getId());
		test3.setOwnerId(user.getId());
		
		
		test1.setSystem("D&D 3.5");
		test2.setSystem("Pathfinder");
		test3.setSystem("FATE Core");

		test1.setPublic(false);
		test2.setPublic(false);
		test3.setPublic(false);

		test1.setShared(true);
		test2.setShared(false);
		test3.setShared(true);

		dbData.insertCharacter(test1);
		dbData.insertCharacter(test2);
		dbData.insertCharacter(test3);
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.slidey_layout);
		
		mTitle = leftMDrawerTitle = rightMDrawerTitle= getTitle();
		
		dbData = DungeonDataSource.getInstance(getApplicationContext());
		allCharacters = dbData.getAllCharacters();
		if(allCharacters.size() == 0)
		{
			SampleDBEntries();
			allCharacters = dbData.getAllCharacters();
		}
		
		setupLeftDrawer();
		setupRightDrawer();
 
        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(-1);
        }
        
        btnNewChar = (Button)findViewById(R.id.btn_drawer_new_char);
        btnNewChar.setOnClickListener(this);
        
        btnAddBuddy = (Button)findViewById(R.id.btn_drawer_add_buddy);
        btnAddBuddy.setOnClickListener(this);
        
        SearchView searchView = (SearchView)findViewById(R.id.right_search_widget);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.d("bacon", "text submitted");
				if (dirtySearch)
				{
					publicSearchQuery(query);
					dirtySearch = false;
				}
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				Log.d("bacon", "text changed");
				dirtySearch = true;
				if(newText.length() == 0)
				{
					sharedCharacterQuery();
				}
				return false;
			}
		});
        
        /*
        // populate the cloud with test data
        List<StatData> stats = new ArrayList<StatData>();
        StatData temp = new StatData();
        temp.setName("Strength");
        temp.setType("Number");
        temp.setValue("16");
        stats.add(temp);
        temp = new StatData();
        temp.setName("Dexterity");
        temp.setType("Number");
        temp.setValue("10");
        stats.add(temp);
        CloudOperations.sendCharacterToCloud("Erevan", "thepostit@gmail.com", "D&D 4e", stats, true, true);
        stats = new ArrayList<StatData>();
        temp = new StatData();
        temp.setName("Strength");
        temp.setType("Number");
        temp.setValue("8");
        stats.add(temp);
        temp = new StatData();
        temp.setName("Dexterity");
        temp.setType("Number");
        temp.setValue("10");
        stats.add(temp);
        CloudOperations.sendCharacterToCloud("Wycliff", "dummy@gmail.com", "Pathfinder", stats, true, false);
        stats = new ArrayList<StatData>();
        temp = new StatData();
        temp.setName("Strength");
        temp.setType("Number");
        temp.setValue("18");
        stats.add(temp);
        temp = new StatData();
        temp.setName("Dexterity");
        temp.setType("Number");
        temp.setValue("16");
        stats.add(temp);
        CloudOperations.sendCharacterToCloud("Waldo", "dummy@gmail.com", "Pathfinder", stats, false, true);
        stats = new ArrayList<StatData>();
        temp = new StatData();
        temp.setName("Strength");
        temp.setType("Number");
        temp.setValue("5");
        stats.add(temp);
        temp = new StatData();
        temp.setName("Dexterity");
        temp.setType("Number");
        temp.setValue("6");
        stats.add(temp);
        CloudOperations.sendCharacterToCloud("test1", "dummy2@gmail.com", "Pathfinder", stats, false, true);
        stats = new ArrayList<StatData>();
        temp = new StatData();
        temp.setName("Strength");
        temp.setType("Number");
        temp.setValue("2");
        stats.add(temp);
        temp = new StatData();
        temp.setName("Dexterity");
        temp.setType("Number");
        temp.setValue("3");
        stats.add(temp);
        CloudOperations.sendCharacterToCloud("test2", "dummy2@gmail.com", "Fate Core", stats, true, false);
        stats = new ArrayList<StatData>();
        temp = new StatData();
        temp.setName("Strength");
        temp.setType("Number");
        temp.setValue("99");
        stats.add(temp);
        temp = new StatData();
        temp.setName("Dexterity");
        temp.setType("Number");
        temp.setValue("30");
        stats.add(temp);
        CloudOperations.sendCharacterToCloud("test3", "dummy2@gmail.com", "Pathfinder", stats, false, false);
		*/
    }
	
	private class SlideMenuClickListener implements ListView.OnItemClickListener
	{
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		{
			displayView(position);
		}
	}
	
	private class RightSlideMenuClickListener implements ListView.OnItemClickListener
	{
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			CharacterData importChar = activeCharacters.get(position);
			UserData importUser;
			long importID;
			String email = rightCharDrawerItems.get(position).getOwnerEmail();
			/* obsolete... want to test before i delete
			ParseQuery<ParseObject> emailQuery = ParseQuery.getQuery("User");
			emailQuery.whereEqualTo("GPlusEmail", rightCharDrawerItems.get(position).getOwnerEmail());
			try {
				email = emailQuery.find().get(0).getString("GPlusEmail");
			} catch (ParseException e1)	{
				e1.printStackTrace();
				email = "blank email";
			}
			*/
			if(!dbData.checkForUser(email))  // if user doesn't exist in internal db, add them to the internal db
			{
				importUser = new UserData();
				importUser.setUserName(rightCharDrawerItems.get(position).getOwner());
				importUser.setGoogleAccount(email);
				
				dbData.insertUser(importUser);
			}
			
			importUser = dbData.getUser(email);
			importChar.setOwnerId(importUser.getId());
			
			// add character to internal db if not duplicate
			CharacterData temp = dbData.getCharacter(importUser.getId(), importChar.getName());
			if(temp == null)
			{
				dbData.insertCharacter(importChar);
				importID = dbData.getCharacter(importUser.getId(), importChar.getName()).getId();
			}
			else // delete local stats for the character and download stats from cloud to update
			{
				importID = temp.getId();
				
				// delete local stats
				List<StatData> tempStats = dbData.getAllStatsForCharacter(importID);
				for (StatData stat : tempStats) {
					dbData.deleteStat(stat.getId());
				}
				
				// delete local sheet fields
				List<SheetFieldData> tempFields = dbData.getAllFieldsForCharacter(importID);
				for (SheetFieldData field : tempFields)	{
					dbData.deleteField(field.getCharId(), field.getIndex());
				}
			}
			
			/*
			// import cloud stats
			for(StatData stat : importChar.stats) {
				stat.setCharacterId(importID);
				dbData.insertStat(stat);
			}
			*/
			
			// search for sheet fields on the cloud and import
			// search for the character on the cloud
			ParseQuery<ParseObject> charQuery = ParseQuery.getQuery("Character");
			charQuery.whereEqualTo("Name", temp.getName());
			charQuery.whereEqualTo("System", temp.getSystem());
			charQuery.whereEqualTo("Owner", email);
			try {
				List<ParseObject> chars = charQuery.find();
				
				// retrieve stats from cloud and add locally
				List<ParseObject> stats = statQuery(chars.get(0));
				Map<String, Long> statIdMap = new HashMap<String, Long>();
				for(ParseObject parseStat : stats) {
					StatData stat = new StatData();
					stat.setCharacterId(importID);
					stat.setName(parseStat.getString("Name"));
					stat.setType(parseStat.getString("Type"));
					stat.setValue(parseStat.getString("Value"));
					statIdMap.put(parseStat.getObjectId(), dbData.insertStat(stat));
				}
				
				// retrieve all the fields from the cloud and add locally
				ParseQuery<ParseObject> fieldQuery = ParseQuery.getQuery("SheetField");
				fieldQuery.whereEqualTo("Character", chars.get(0));
				try {
					List<ParseObject> fieldList = fieldQuery.find();
					for(ParseObject field : fieldList) {	// add each stat to the local database
						SheetFieldData tempField = new SheetFieldData();
						tempField.setCharId(importID);
						tempField.setIndex(field.getLong("Index"));
						tempField.setLabel(field.getString("Label"));
						if (statIdMap.get(field.getString("Stat")) == null) {
							tempField.setStatId(DungeonDataSource.dbNullNum);
						} else {
							tempField.setStatId(statIdMap.get(field.getString("Stat")));
						}
						dbData.insertSheetField(tempField);
					}
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			// setup statlist fragment
			Fragment fragment = fragmentBuilder(importID, importChar.getName());
			
			if (fragment != null)
			{
				fragmentDisplayer(rightMDrawerList, rightMDrawerLayout, rightDrawer, fragment, position);
			}
			
			fillCharacterList();
		}
	}
	
	private void displayView(int position)
	{
		Fragment fragment;
		if(position == -1)
		{
			fragment = new MainMenuFragment();
			setTitle("Dungeon Secretary");
		}
		else
		{
			fragment = fragmentBuilder(allCharacters.get(position).getId(), allCharacters.get(position).getName());
			
		}
		
		// Signals an intention to do something
		// getApplication() returns the application that owns
		// this activity
		
		if (fragment != null)
		{
			fragmentDisplayer(leftMDrawerList, leftMDrawerLayout, leftDrawer, fragment, position);
			
		}
	}
		
	private Fragment fragmentBuilder(long charId, String title)
	{
		Fragment fragment = new StatListPageActivity();
		Bundle bundle = new Bundle();
		
		bundle.putLong("charId", charId);
		
		fragment.setArguments(bundle);
		setTitle(title);
		
		dbData.setCurrentCharacter(charId);
		
		return fragment;
	}
	
	private void fragmentDisplayer(ListView sideMDrawerList, DrawerLayout sideMDrawerLayout, LinearLayout drawer, Fragment fragment, int position)
	{
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
		// update selected item and title, then close the drawer
		sideMDrawerList.setItemChecked(position, true);
		sideMDrawerList.setSelection(position);
		sideMDrawerLayout.closeDrawer(drawer);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
    	if (leftMDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
    	//Put fragment out here because for some reason inside the case is not a local variable...
    	Fragment fragment;
        switch (item.getItemId()) {
        case R.id.action_open_list:
        {
        	fragment = new StatListPageActivity();
        	FragmentManager fMan = getSupportFragmentManager();
        	fMan.beginTransaction().replace(R.id.frame_container, fragment).commit();    
        	return true;
        }
        case R.id.action_open_sheet:
        {
        	fragment = new CharSheetFragment();
        	FragmentManager fMan = getSupportFragmentManager();
        	fMan.beginTransaction().replace(R.id.frame_container, fragment).commit();   
        	return true;
        }
        case R.id.select_private:
        {
        	//don't waste time updating the db if we don't need to
        	if(!item.isChecked())
        	{
        		//private is both shared and public false
        		item.setChecked(true);
        		CharacterData currentChar = dbData.getCurrentCharacter();
        		currentChar.setShared(false);
        		currentChar.setPublic(false);
        		dbData.updateCharacter(currentChar);
        	}
        	return true;
        }
        case R.id.select_shared:
        {
        	if(!item.isChecked())
        	{
        		//private is both shared and public false
        		item.setChecked(true);
        		CharacterData currentChar = dbData.getCurrentCharacter();
        		currentChar.setShared(true);
        		currentChar.setPublic(false);
        		dbData.updateCharacter(currentChar);
        	}
        	return true;
        }
        case R.id.select_public:
        {
        	if(!item.isChecked())
        	{
        		//private is both shared and public false
        		item.setChecked(true);
        		CharacterData currentChar = dbData.getCurrentCharacter();
        		currentChar.setShared(true);
        		currentChar.setPublic(true);
        		dbData.updateCharacter(currentChar);
        	}
        	return true;
        }
        case R.id.action_game_system:
        {
        	FragmentManager fm = getSupportFragmentManager();
        	ChangeSystemDialog csys = new ChangeSystemDialog();
        	csys.addDialogListener(this);
        	csys.show(fm, "fragment_change_system");
        }
        case R.id.action_delete:
        {
        	dbData.deleteCharacter(dbData.getCurrentCharacter().getId());
        	fillCharacterList();
        	displayView(-1);
        }
        default:
            return super.onOptionsItemSelected(item);
        }
    }
 
    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = leftMDrawerLayout.isDrawerOpen(leftDrawer);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
 
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
 
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
 
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        leftMDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        leftMDrawerToggle.onConfigurationChanged(newConfig);    
	}
    
    public void openLeftDrawer()
    {
    	leftMDrawerLayout.openDrawer(Gravity.LEFT);
    }

    public void openRightDrawer()
    {
    	rightMDrawerLayout.openDrawer(Gravity.RIGHT);
    }
    
    @Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_drawer_new_char:
	    	{   		
	    		//pop up add window
	    		FragmentManager fm = getSupportFragmentManager();
	    		NewCharacterDialog dlog = new NewCharacterDialog();
	    		dlog.addDialogListener(this);
	    		dlog.show(fm, "fragment_new_character");
	    		break;
	    		
			}
			case R.id.btn_drawer_add_buddy:
			{
				FragmentManager fm = getSupportFragmentManager();
				AddBuddyDialog dlog = new AddBuddyDialog();
				dlog.addDialogListener(this);
				dlog.show(fm, "fragment_add_buddy");
				Log.d("bacon", "I am killin' it");
				break;
			}
		}		
	}
    
    public void onDialogFinish(int dialogId) {
		switch(dialogId){
			case R.id.dialog_new_character:
			{
				fillCharacterList();
			}
			case R.id.dialog_change_system:
			{
				fillCharacterList();
			}
		}
	}
	
	private void publicSearchQuery(String searchString) // searches only public charaters, non-public shared characters will not be added
	{
		ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("User");
		userQuery.whereMatches("UserName", searchString, "i");
		String emails[];
		try {
			List<ParseObject> userEmails = userQuery.find();
			emails = new String[userEmails.size()];
			for (int i = 0; i < userEmails.size(); i++) {
				emails[i] = userEmails.get(i).getString("GPlusEmail");
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
			emails = new String[0];
		}
		
		ParseQuery<ParseObject> ownerQuery = ParseQuery.getQuery("Character");
		ownerQuery.whereContainedIn("Owner", Arrays.asList(emails));
		ownerQuery.whereEqualTo("Public", true);
		//ownerQuery.whereNotEqualTo("Owner", dbData.getCurrentUser().getGoogleAccount());
		
		ParseQuery<ParseObject> ownerSharedQuery = ParseQuery.getQuery("Character");
		ownerSharedQuery.whereContainedIn("Owner", Arrays.asList(emails));
		ownerSharedQuery.whereEqualTo("Shared", true);
		//ownerSharedQuery.whereNotEqualTo("Owner", dbData.getCurrentUser().getGoogleAccount());
		
		ParseQuery<ParseObject> nameQuery = ParseQuery.getQuery("Character");
		nameQuery.whereMatches("Name", searchString, "i");
		nameQuery.whereEqualTo("Public", true);
		//nameQuery.whereNotEqualTo("Owner", dbData.getCurrentUser().getGoogleAccount());
		
		ParseQuery<ParseObject> nameSharedQuery = ParseQuery.getQuery("Character");
		nameSharedQuery.whereMatches("Name", searchString, "i");
		nameSharedQuery.whereEqualTo("Shared", true);
		//nameSharedQuery.whereNotEqualTo("Owner", dbData.getCurrentUser().getGoogleAccount());
		
		ParseQuery<ParseObject> systemQuery = ParseQuery.getQuery("Character");
		systemQuery.whereMatches("System", searchString, "i");
		systemQuery.whereEqualTo("Public", true);
		//systemQuery.whereNotEqualTo("Owner", dbData.getCurrentUser().getGoogleAccount());
		
		ParseQuery<ParseObject> systemSharedQuery = ParseQuery.getQuery("Character");
		systemSharedQuery.whereMatches("System", searchString, "i");
		systemSharedQuery.whereEqualTo("Shared", true);
		//systemSharedQuery.whereNotEqualTo("Owner", dbData.getCurrentUser().getGoogleAccount());
		
		/* the whereNotEqualTo statements are commented out for testing purposes. in the final version they will make it so your own characters are not included in search results */
		
		List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
		queries.add(ownerQuery);
		queries.add(ownerSharedQuery);
		queries.add(nameQuery);
		queries.add(nameSharedQuery);
		queries.add(systemQuery);
		queries.add(systemSharedQuery);
		
		ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
		mainQuery.findInBackground(new FindCallback<ParseObject>() {
			  public void done(List<ParseObject> characterList, ParseException e) {
				  // results has the list of characters with searchString in the
			      // name, owner, or system field.
				  if (e == null) { // query responds with success
					  rightCharDrawerItems = generateCharacterDrawerList(characterList);
					  charRightAdapter = new CharacterDrawerListAdapter(getApplicationContext(), rightCharDrawerItems);
				      rightMDrawerList.setAdapter(charRightAdapter);
				      //tempChar = null;
				  } else {
				      // no response from cloud
				  }
			  }
		});
	}

	private void sharedCharacterQuery()
	{
		ParseQuery<ParseObject> buddyQuery = ParseQuery.getQuery("UserFriends");
		buddyQuery.whereEqualTo("User", dbData.getCurrentUser().getGoogleAccount());
		buddyQuery.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> buddyList, ParseException e) {
				// results has the list of buddies to the user
				if (e == null) { // query responds with success
					buddies = new String[buddyList.size()];
					for(int i = 0; i < buddyList.size(); i++)
					{
						buddies[i] = buddyList.get(i).getString("IsFriendsWith");
					}
					ParseQuery<ParseObject> sharedQuery = ParseQuery.getQuery("Character");
					sharedQuery.whereContainedIn("Owner", Arrays.asList(buddies));
					sharedQuery.whereEqualTo("Shared", true);
					try {
						List<ParseObject> characterList = sharedQuery.find();
						rightCharDrawerItems = generateCharacterDrawerList(characterList);
						charRightAdapter = new CharacterDrawerListAdapter(getApplicationContext(), rightCharDrawerItems);
						rightMDrawerList.setAdapter(charRightAdapter);
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
				} else {
					// no response from cloud
				}
			}
		});
	}

	private List<ParseObject> statQuery(ParseObject character) {  // method may change for an optimization
		ParseQuery<ParseObject> statQuery = ParseQuery.getQuery("Stat");
		statQuery.whereEqualTo("ParentID", character);
		List<ParseObject> statList;
		try {
			statList = statQuery.find();
			/*
            for (ParseObject stat : statList)
            {
            	StatData temp = new StatData();
            	temp.setName(stat.getString("Name"));
            	temp.setType(stat.getString("Type"));
            	temp.setValue(stat.getString("Value"));
            	stats.add(temp);
            }
            tempChar.stats = stats;
            */
		} catch (ParseException e1) {
			e1.printStackTrace();
			statList = new ArrayList<ParseObject>();
		}
		return statList;
	}

	private ArrayList<CharacterDrawerItem> generateCharacterDrawerList(List<ParseObject> characterList)
	{
		activeCharacters = new ArrayList<CharacterData>(); // clear character list
		ArrayList<CharacterDrawerItem> temp = new ArrayList<CharacterDrawerItem>();
		
		for(ParseObject character : characterList)
		{
			tempChar = new CharacterData();
	    	tempChar.setName(character.getString("Name"));
	    	tempChar.setSystem(character.getString("System"));
	    	tempChar.setPublic(true);
	    	tempChar.setShared(character.getBoolean("Shared"));
		    //statQuery(character);  // this may be left out here for optimization (stats would be retrieved at a later point)
		    activeCharacters.add(tempChar);
		    temp.add(new CharacterDrawerItem(tempChar, character.getString("OwnerName"), character.getString("Owner")));
		}
		
		return temp;
	}
	
}
