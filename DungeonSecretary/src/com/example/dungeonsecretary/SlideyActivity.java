package com.example.dungeonsecretary;


import java.util.ArrayList;
import java.util.List;

import com.example.dungeonsecretary.adapter.CharacterDrawerListAdapter;
import com.example.dungeonsecretary.adapter.NavDrawerListAdapter;
import com.example.dungeonsecretary.model.CharacterData;
import com.example.dungeonsecretary.model.CharacterDrawerItem;
import com.example.dungeonsecretary.model.NavDrawerItem;
import com.example.dungeonsecretary.model.UserData;
import com.example.dungeonsecretary.sql.DungeonDataSource;
import com.example.dungeonsecretary.sql.MySQLiteHelper;

import android.os.Bundle;
import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;


public class SlideyActivity extends FragmentActivity {

	private DrawerLayout leftMDrawerLayout, rightMDrawerLayout;
	private LinearLayout leftDrawer;
	private ListView leftMDrawerList, rightMDrawerList;
	private ActionBarDrawerToggle leftMDrawerToggle, rightMDrawerToggle;
	private DungeonDataSource dbData;
	
	//nav drawer title
	private CharSequence leftMDrawerTitle, rightMDrawerTitle;
	
	//used to store app title
	private CharSequence mTitle;
	
	//left slide menu items
	private List<CharacterData> allCharacters;
	
	private ArrayList<CharacterDrawerItem> leftCharDrawerItems;
	private CharacterDrawerListAdapter charLeftAdapter;
	
	//right slide menu items
	private List<CharacterData> sharedCharacters;
	private ArrayList<CharacterDrawerItem> rightCharDrawerItems;
	private CharacterDrawerListAdapter charRightAdapter;
	
	private Button btnNewChar;
	//private String[] rightNavMenuTitles;
	//private TypedArray rightNavMenuIcons;
	
	//private ArrayList<NavDrawerItem> rightNavDrawerItems;
	//private NavDrawerListAdapter rightAdapter;
	
	//If you need to manually reset the database and build it from scratch 
	//when the activity starts set this to true.
	private boolean DEV_resetDatabase = true;
	
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
	
	private void fillSharedCharacters()
	{
		// get friends 
		sharedCharacters = dbData.getAllCharacters();
		rightCharDrawerItems = new ArrayList<CharacterDrawerItem>();
		
		// prune unshared files
		List<CharacterData> temp = new ArrayList<CharacterData>();
		
		for(int i = 0; i < sharedCharacters.size(); i++)
		{
			CharacterData tempChar = sharedCharacters.get(i);
			if (!tempChar.getShared() /*|| sharedCharacters.get(i).getOwnerName() == "Shawn"*/)  // using this for now to test
			{
				temp.add(tempChar);
			}			
		}
		
		for (int i = 0; i < temp.size(); i++)
		{
			sharedCharacters.remove(temp.get(i));
		}
		
		// add characters to the list
		for (int i = 0; i < sharedCharacters.size(); i++)
		{
			rightCharDrawerItems.add(new CharacterDrawerItem(sharedCharacters.get(i), "User that isn't me")); // there's no way to look up the user's name if you only have the CharacterData object. you can only get user by the google account, and CharacterData only stores the user id.
		}
		
		charRightAdapter = new CharacterDrawerListAdapter(getApplicationContext(), rightCharDrawerItems);
		rightMDrawerList.setAdapter(charRightAdapter);
	}
	
	private void setupLeftDrawer()
	{
		leftMDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		leftDrawer = (LinearLayout) findViewById(R.id.left_drawer);
		leftMDrawerList = (ListView) findViewById(R.id.list_slidermenu_left);
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
        rightMDrawerList = (ListView) findViewById(R.id.list_slidermenu_right);
        fillSharedCharacters();
        
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
            	fillSharedCharacters();
                getActionBar().setTitle(rightMDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        rightMDrawerLayout.setDrawerListener(rightMDrawerToggle); 

        rightMDrawerList.setOnItemClickListener(new SlideMenuClickListener());
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
		//dbData.open();
		/*if(DEV_resetDatabase)
		{
			dbData.resetDatabase();
			dbData.close();
			dbData.open();
		}*/
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
    }
 
	private class SlideMenuClickListener implements ListView.OnItemClickListener
	{
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		{
			displayView(position);
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
			fragment = new StatListPageActivity();
			Bundle bundle = new Bundle();
			
			long charId = allCharacters.get(position).getId();
			
			bundle.putLong("charId",charId);
			
			fragment.setArguments(bundle);
			setTitle(allCharacters.get(position).getName());
		}
		
			
		// Signals an intention to do something
		// getApplication() returns the application that owns
		// this activity
		
		if (fragment != null)
		{
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container,  fragment).commit();
			//update selected item and title, then close the drawer
			leftMDrawerList.setItemChecked(position, true);
			leftMDrawerList.setSelection(position);
			leftMDrawerLayout.closeDrawer(Gravity.LEFT);
		}
		

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
    	leftMDrawerLayout.openDrawer(leftDrawer);
    }

    public void openRightDrawer()
    {
    	rightMDrawerLayout.openDrawer(Gravity.RIGHT);
    }

}
