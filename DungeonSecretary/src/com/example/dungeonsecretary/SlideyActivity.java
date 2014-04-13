package com.example.dungeonsecretary;


import java.util.ArrayList;
import java.util.List;

import com.example.dungeonsecretary.adapter.CharacterDrawerListAdapter;
import com.example.dungeonsecretary.adapter.NavDrawerListAdapter;
import com.example.dungeonsecretary.interfaces.DialogListener;
import com.example.dungeonsecretary.model.CharacterData;
import com.example.dungeonsecretary.model.CharacterDrawerItem;
import com.example.dungeonsecretary.model.NavDrawerItem;
import com.example.dungeonsecretary.model.UserData;
import com.example.dungeonsecretary.sql.DungeonDataSource;
import com.example.dungeonsecretary.sql.MySQLiteHelper;

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
	private List<CharacterData> sharedCharacters;
	private ArrayList<CharacterDrawerItem> rightCharDrawerItems, rightSearchResult;
	private CharacterDrawerListAdapter charRightAdapter;
	private boolean searching;
	private String query;
	
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
		sharedCharacters = dbData.getAllCharacters();
		rightCharDrawerItems = new ArrayList<CharacterDrawerItem>();

		List<CharacterData> temp = new ArrayList<CharacterData>();
		if(searching)
		{
			// prune non-public files
			for(int i = 0; i < sharedCharacters.size(); i++)
			{
				CharacterData tempChar = sharedCharacters.get(i);
				if (!tempChar.getPublic() || tempChar.getName() != query)
				{
					temp.add(tempChar);
				}			
			}
		}
		else
		{
			// get friends (will use this later to only get shared files from friends)
			
			
			// prune unshared files
			for(int i = 0; i < sharedCharacters.size(); i++)
			{
				CharacterData tempChar = sharedCharacters.get(i);
				if (!tempChar.getShared() || dbData.getCurrentUser().getId() == tempChar.getOwnerId())
				{
					temp.add(tempChar);
				}			
			}
		}
		for (int i = 0; i < temp.size(); i++)
		{
			sharedCharacters.remove(temp.get(i));
		}
		
		// add characters to the list
		for (int i = 0; i < sharedCharacters.size(); i++)
		{
			rightCharDrawerItems.add(new CharacterDrawerItem(sharedCharacters.get(i), dbData.getUser(sharedCharacters.get(i).getOwnerId()).getUserName())); // there's no way to look up the user's name if you only have the CharacterData object. you can only get user by the google account, and CharacterData only stores the user id.
		}
		
		charRightAdapter = new CharacterDrawerListAdapter(getApplicationContext(), rightCharDrawerItems);
		rightMDrawerList.setAdapter(charRightAdapter);
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
        
        // handle search intent
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
        	query = intent.getStringExtra(SearchManager.QUERY);
        	searching = true;
        }
        else
        {
        	searching = false;
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
			
			//Probably change this to use the adapter
			long charId = allCharacters.get(position).getId();
			
			bundle.putLong("charId",charId);
			
			fragment.setArguments(bundle);
			setTitle(allCharacters.get(position).getName());
			
			dbData.setCurrentCharacter(charId);
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
			leftMDrawerLayout.closeDrawer(leftDrawer);
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
        	Fragment fragment = new CharSheetFragment();
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container,  fragment).commit();
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

}
