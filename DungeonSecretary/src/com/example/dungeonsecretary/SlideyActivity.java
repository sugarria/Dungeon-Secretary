package com.example.dungeonsecretary;


import java.util.ArrayList;

import com.example.dungeonsecretary.adapter.NavDrawerListAdapter;
import com.example.dungeonsecretary.model.NavDrawerItem;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SlideyActivity extends Activity {

	private DrawerLayout leftMDrawerLayout, rightMDrawerLayout;
	private ListView leftMDrawerList, rightMDrawerList;
	private ActionBarDrawerToggle leftMDrawerToggle, rightMDrawerToggle;
	
	//nav drawer title
	private CharSequence leftMDrawerTitle, rightMDrawerTitle;
	
	//used to store app title
	private CharSequence mTitle;
	
	//left slide menu items
	private String[] leftNavMenuTitles;
	private TypedArray leftNavMenuIcons;
	
	private ArrayList<NavDrawerItem> leftNavDrawerItems;
	private NavDrawerListAdapter leftAdapter;
	
	//right slide menu items
	private String[] rightNavMenuTitles;
	private TypedArray rightNavMenuIcons;
	
	private ArrayList<NavDrawerItem> rightNavDrawerItems;
	private NavDrawerListAdapter rightAdapter;
	
	private void setupLeftDrawer()
	{
		//load slide menu items
		leftNavMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		
		//nav drawer icons from resources
		leftNavMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		
		leftMDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftMDrawerList = (ListView) findViewById(R.id.list_slidermenu_left);
 
		leftNavDrawerItems = new ArrayList<NavDrawerItem>();
		
		//adding nav drawer items to array
		//home
		leftNavDrawerItems.add(new NavDrawerItem(leftNavMenuTitles[0], leftNavMenuIcons.getResourceId(0, -1)));
		// Find People
        leftNavDrawerItems.add(new NavDrawerItem(leftNavMenuTitles[1], leftNavMenuIcons.getResourceId(1, -1)));
        // Photos
        leftNavDrawerItems.add(new NavDrawerItem(leftNavMenuTitles[2], leftNavMenuIcons.getResourceId(2, -1)));
        // Communities, Will add a counter here
        //leftNavDrawerItems.add(new NavDrawerItem(leftNavMenuTitles[3], leftNavMenuIcons.getResourceId(3, -1), true, "22"));
        // Pages
        //leftNavDrawerItems.add(new NavDrawerItem(leftNavMenuTitles[4], leftNavMenuIcons.getResourceId(4, -1)));
        // What's hot, We  will add a counter here
        //leftNavDrawerItems.add(new NavDrawerItem(leftNavMenuTitles[5], leftNavMenuIcons.getResourceId(5, -1), true, "50+"));


        // Recycle the typed array
        leftNavMenuIcons.recycle();
 
        // setting the nav drawer list adapter
        leftAdapter = new NavDrawerListAdapter(getApplicationContext(),
                leftNavDrawerItems);
        leftMDrawerList.setAdapter(leftAdapter);
        
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
		//load slide menu items
		rightNavMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		
		//nav drawer icons from resources
		rightNavMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		
		rightMDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        rightMDrawerList = (ListView) findViewById(R.id.list_slidermenu_right);
 
		rightNavDrawerItems = new ArrayList<NavDrawerItem>();
		
		//adding nav drawer items to array
		//home
		//rightNavDrawerItems.add(new NavDrawerItem(rightNavMenuTitles[0], rightNavMenuIcons.getResourceId(0, -1)));
		// Find People
        //rightNavDrawerItems.add(new NavDrawerItem(rightNavMenuTitles[1], rightNavMenuIcons.getResourceId(1, -1)));
        // Photos
        //rightNavDrawerItems.add(new NavDrawerItem(rightNavMenuTitles[2], rightNavMenuIcons.getResourceId(2, -1)));
        // Communities, Will add a counter here
        rightNavDrawerItems.add(new NavDrawerItem(rightNavMenuTitles[3], rightNavMenuIcons.getResourceId(3, -1), true, "22"));
        // Pages
        rightNavDrawerItems.add(new NavDrawerItem(rightNavMenuTitles[4], rightNavMenuIcons.getResourceId(4, -1)));
        // What's hot, We  will add a counter here
        rightNavDrawerItems.add(new NavDrawerItem(rightNavMenuTitles[5], rightNavMenuIcons.getResourceId(5, -1), true, "50+"));


        // Recycle the typed array
        rightNavMenuIcons.recycle();
 
        // setting the nav drawer list adapter
        rightAdapter = new NavDrawerListAdapter(getApplicationContext(),
                rightNavDrawerItems);
        rightMDrawerList.setAdapter(rightAdapter);
        
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
                getActionBar().setTitle(rightMDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        rightMDrawerLayout.setDrawerListener(rightMDrawerToggle); 

        rightMDrawerList.setOnItemClickListener(new SlideMenuClickListener());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slidey_layout);
		
		mTitle = leftMDrawerTitle = rightMDrawerTitle= getTitle();
		
		setupLeftDrawer();
		setupRightDrawer();
 
        // enabling action bar app icon and behaving it as toggle button
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);
 

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
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
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new HomeFragment();
			break;
		default:
				break;
		}
		
		if (fragment != null)
		{
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container,  fragment).commit();
			//update selected item and title, then close the drawer
			leftMDrawerList.setItemChecked(position, true);
			leftMDrawerList.setSelection(position);
			setTitle(leftNavMenuTitles[position]);
			leftMDrawerLayout.closeDrawer(leftMDrawerList);
		} else 
		{
			Log.e("MainActivity", "Error in creating fragment");
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
        boolean drawerOpen = leftMDrawerLayout.isDrawerOpen(leftMDrawerList);
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


}
