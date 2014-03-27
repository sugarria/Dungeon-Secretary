package com.example.dungeonsecretary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.dungeonsecretary.sql.*;
import com.example.dungeonsecretary.NewStat;
import com.example.dungeonsecretary.EditStat;
import com.example.dungeonsecretary.adapter.CharacterDrawerListAdapter;
import com.example.dungeonsecretary.adapter.StatListAdapter;
import com.example.dungeonsecretary.model.CharacterData;
import com.example.dungeonsecretary.model.CharacterDrawerItem;
import com.example.dungeonsecretary.model.StatData;

import com.example.dungeonsecretary.R;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.content.Context;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ListView;

public class MainMenuFragment extends Fragment implements OnClickListener{
	Button btnNewChar;
	Button btnLoadChar;
	Button btnShare;
    SlideyActivity parentActivity;
	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// Get saved data if there is any
		super.onCreate(savedInstanceState);
		
		View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);
		
		btnNewChar = (Button)rootView.findViewById(R.id.btn_main_new_char);
        btnNewChar.setOnClickListener(this);
        btnLoadChar = (Button)rootView.findViewById(R.id.btn_main_load_char);
        btnLoadChar.setOnClickListener(this);
        btnShare = (Button)rootView.findViewById(R.id.btn_main_share);
        btnShare.setOnClickListener(this);
        
        parentActivity = (SlideyActivity)getActivity();
        
		return rootView;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_main_new_char:
	    	{
	    		parentActivity.openLeftDrawer();
	    		break;
			}
			case R.id.btn_main_load_char:
	    	{
	    		parentActivity.openLeftDrawer();
	    		break;
			}
			case R.id.btn_main_share:
	    	{
	    		parentActivity.openRightDrawer();
	    		break;
			}
		}		
	}
	

}
