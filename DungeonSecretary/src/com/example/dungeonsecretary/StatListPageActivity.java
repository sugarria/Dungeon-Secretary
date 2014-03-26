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

public class StatListPageActivity extends Fragment implements OnClickListener{
	Intent intent;
	TextView statId;
	Button btnPlus;
    DungeonDataSource dbData;
    ArrayList<StatData> statDataList;
	StatListAdapter statListAdapter;
	ListView statView;
	long charId;
	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// Get saved data if there is any
		super.onCreate(savedInstanceState);
		
		View rootView = inflater.inflate(R.layout.stat_list_page, container, false);
		
        btnPlus = (Button)rootView.findViewById(R.id.btn_plus);
        btnPlus.setOnClickListener(this);
        
        dbData = DungeonDataSource.getInstance(getActivity().getApplicationContext());
		
		Bundle bundle = this.getArguments();
		charId = bundle.getLong("charId");

		// Get the ListView and assign an event handler to it
		Log.i("StatList", "before getListView");
		statView = (ListView) rootView.findViewById(R.id.list_stat_view);
		Log.i("StatList", "before setOnItemClickListener");
		statView.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				// When an item is clicked get the TextView
				// with a matching checkId
				Log.i("StatList", "before findView(statId)");
				statId = (TextView) view.findViewById(R.id.statId);
				
				
				
				// Calls for EditStat pop up window
				
			}
		}); 
		Log.i("StatList", "before fillStat");
		fillStat();
		
		
		return rootView;
	}
	
	public void fillStat(){	
		Log.i("StatList", "before getAllStats");
		List<StatData> allStats =  dbData.getAllStatsForCharacter(charId);
		Log.i("StatList", "before loop");
		statDataList = new ArrayList<StatData>();
		for(int i = 0; i < allStats.size(); i++)
		{
			statDataList.add(allStats.get(i));
		}
		Log.i("StatList", "before new StatListAdapter");
		statListAdapter = new StatListAdapter(getActivity().getApplicationContext(), statDataList);
		Log.i("StatList", "before setAdapter");
        statView.setAdapter(statListAdapter);
		
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_plus:
    	{
    		//pop up add window
    		break;
		}
	
		
	}
		
	}
		
		

}