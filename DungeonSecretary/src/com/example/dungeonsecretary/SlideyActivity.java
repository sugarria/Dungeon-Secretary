package com.example.dungeonsecretary;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.dungeonsecretary.adapter.CharacterDrawerListAdapter;
import com.example.dungeonsecretary.adapter.NavDrawerListAdapter;
import com.example.dungeonsecretary.interfaces.DialogListener;
import com.example.dungeonsecretary.model.CharacterData;
import com.example.dungeonsecretary.model.CharacterDrawerItem;
import com.example.dungeonsecretary.model.NavDrawerItem;
import com.example.dungeonsecretary.model.StatData;
import com.example.dungeonsecretary.model.UserData;
import com.example.dungeonsecretary.sql.DungeonDataSource;
import com.example.dungeonsecretary.sql.MySQLiteHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ListFragment;
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
	private Button btnNewChar;
	
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
	private String query;
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
        sendCharacterToCloud("Erevan", "Eric Mathews", "D&D 4e", stats, true, true);
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
        sendCharacterToCloud("Wycliff", "Shawn", "Pathfinder", stats, true, false);
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
        sendCharacterToCloud("Waldo", "Shawn", "Pathfinder", stats, false, true);
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
        sendCharacterToCloud("test1", "Sean", "Pathfinder", stats, false, true);
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
        sendCharacterToCloud("test2", "Sean", "Fate Core", stats, true, false);
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
        sendCharacterToCloud("test3", "Sean", "Pathfinder", stats, false, false);
		*/
    }
 
	@Override
	protected void onNewIntent(Intent intent) {
		query = intent.getStringExtra(SearchManager.QUERY);
		Log.d("bacon", query);
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
			String email;
			ParseQuery<ParseObject> emailQuery = ParseQuery.getQuery("User");
			emailQuery.whereEqualTo("UserName", rightCharDrawerItems.get(position).getOwner());
			try {
				email = emailQuery.find().get(0).getString("GPlusEmail");
			} catch (ParseException e1)	{
				e1.printStackTrace();
				email = "blank email";
			}
			
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
				for(StatData stat : importChar.stats)
				{
					stat.setCharacterId(importID);
					dbData.insertStat(stat);
				}
			}
			else
			{
				importID = temp.getId();
			}
			
			// setup statlist fragment
			Fragment fragment = fragmentBuilder(importID, importChar.getName());
			
			if (fragment != null)
			{
				fragmentDisplayer(rightMDrawerList, rightMDrawerLayout, rightDrawer, fragment, position);
			}
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
			
			/* I extracted the following code into fragmentBuilder - Eric
			Fragment fragment = new StatListPageActivity();
			Bundle bundle = new Bundle();
		
			//Probably change this to use the adapter
			long charId = allCharacters.get(position).getId();
			bundle.putLong("charId", charId);
		
			fragment.setArguments(bundle);
			setTitle(allCharacters.get(position).getName());
		
			dbData.setCurrentCharacter(charId);
			 */
		}
		
		// Signals an intention to do something
		// getApplication() returns the application that owns
		// this activity
		
		if (fragment != null)
		{
			fragmentDisplayer(leftMDrawerList, leftMDrawerLayout, leftDrawer, fragment, position);
			/* I extracted the following code into fragmentDisplayer - Eric
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container,  fragment).commit();
			//update selected item and title, then close the drawer
			leftMDrawerList.setItemChecked(position, true);
			leftMDrawerList.setSelection(position);
			leftMDrawerLayout.closeDrawer(leftDrawer);
			*/
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
        switch (item.getItemId()) {
        case R.id.action_settings:
            return true;
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
		}		
	}
    
    public void onDialogFinish(int dialogId) {
		switch(dialogId){
			case R.id.dialog_new_character:
			{
				fillCharacterList();
			}
		}
	}
    
    private void sendCharacterToCloud(String name, String ownerName, String system, List<StatData> stats, boolean sharedFlag, boolean publicFlag)
    {
    	deleteOldCharacter(name, ownerName, system);
    	
      	ParseObject characterObject = new ParseObject("Character");
		characterObject.put("Name", name);
		characterObject.put("Owner", ownerName);
		characterObject.put("System", system);
		characterObject.put("Shared", sharedFlag);
		characterObject.put("Public", publicFlag);
		
		for(int i = 0; i < stats.size(); i++)
		{
			ParseObject statObject = new ParseObject("Stat");
			
			statObject.put("ParentID", characterObject);
			statObject.put("Name", stats.get(i).getName());
			statObject.put("Type", stats.get(i).getType());
			statObject.put("Value", stats.get(i).getValue());
			statObject.saveInBackground();
		}
    }

	private void deleteOldCharacter(String name, String ownerName,
			String system) {
		ParseQuery<ParseObject> qResults = ParseQuery.getQuery("Character");
		qResults.whereEqualTo("Name", name);
		qResults.whereEqualTo("Owner", ownerName);
		qResults.whereEqualTo("System", system);
		qResults.whereEqualTo("Deleted", false);
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
		    		            
		    		        }
		    		    }
		    		});
		    		
		            characterList.get(0).deleteInBackground();
		        }
			}
		});
	}
	
	private void publicSearchQuery(String searchString) // searches only public charaters, non-public shared characters will not be added
	{
		ParseQuery<ParseObject> ownerQuery = ParseQuery.getQuery("Character");
		ownerQuery.whereMatches("Owner", searchString, "i");
		ownerQuery.whereEqualTo("Public", true);
		ownerQuery.whereNotEqualTo("Owner", dbData.getCurrentUser().getUserName());
		
		ParseQuery<ParseObject> nameQuery = ParseQuery.getQuery("Character");
		nameQuery.whereMatches("Name", searchString, "i");
		nameQuery.whereEqualTo("Public", true);
		nameQuery.whereNotEqualTo("Owner", dbData.getCurrentUser().getUserName());
		
		ParseQuery<ParseObject> systemQuery = ParseQuery.getQuery("Character");
		systemQuery.whereMatches("System", searchString, "i");
		systemQuery.whereEqualTo("Public", true);
		systemQuery.whereNotEqualTo("Owner", dbData.getCurrentUser().getUserName());
		
		List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
		queries.add(ownerQuery);
		queries.add(nameQuery);
		queries.add(systemQuery);
		
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
		buddyQuery.whereEqualTo("User", dbData.getCurrentUser().getUserName());
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

	protected void statQuery(ParseObject character) {
		ParseQuery<ParseObject> statQuery = ParseQuery.getQuery("Stat");
		statQuery.whereEqualTo("ParentID", character);
		try {
			List<ParseObject> statList = statQuery.find();
			List<StatData> stats = new ArrayList<StatData>();
            for (ParseObject stat : statList)
            {
            	StatData temp = new StatData();
            	temp.setName(stat.getString("Name"));
            	temp.setType(stat.getString("Type"));
            	temp.setValue(stat.getString("Value"));
            	// temp.setCharacterId(*characterId*);
            	// temp.setId(*id*);
            	// TODO:ERIC to put the character into the database the IDs need to be set
            	stats.add(temp);
            }
            tempChar.stats = stats;  // not sure how stats work in the internal database, this line may change
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
	}

	private ArrayList<CharacterDrawerItem> generateCharacterDrawerList(List<ParseObject> characterList)
	{
		if (activeCharacters != null)
		{
			activeCharacters.clear();
		}
		else
		{
			activeCharacters = new ArrayList<CharacterData>();
		}
		ArrayList<CharacterDrawerItem> temp = new ArrayList<CharacterDrawerItem>();
		for(ParseObject character : characterList)
		{
			tempChar = new CharacterData();
	    	tempChar.setName(character.getString("Name"));
	    	tempChar.setSystem(character.getString("System"));
	    	tempChar.setPublic(true);
	    	tempChar.setShared(character.getBoolean("Shared"));
	    	// tempChar.setOwnerId(dbData.getUser(character.getString("Owner")).getId());
	    	// tempChar.setId(*id*);
	    	// TODO:ERIC need to set IDs to put the character into the internal database
		    statQuery(character);
		    activeCharacters.add(tempChar);
		    temp.add(new CharacterDrawerItem(tempChar, character.getString("Owner")));
		}
		
		return temp;
	}
	
}