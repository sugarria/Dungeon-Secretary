package com.example.dungeonsecretary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.dungeonsecretary.sql.*;
import com.example.dungeonsecretary.NewStat;
import com.example.dungeonsecretary.adapter.CharacterDrawerListAdapter;
import com.example.dungeonsecretary.adapter.StatListAdapter;
import com.example.dungeonsecretary.interfaces.DialogListener;
import com.example.dungeonsecretary.model.CharacterData;
import com.example.dungeonsecretary.model.CharacterDrawerItem;
import com.example.dungeonsecretary.model.StatData;
import com.example.dungeonsecretary.EditStatDialog.EditNameDialogListener;

import com.example.dungeonsecretary.R;
import android.app.AlertDialog;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ListView;

public class StatListPageActivity extends Fragment implements OnClickListener, DialogListener{
	Intent intent;
	TextView statId;
	Button btnPlus;
    DungeonDataSource dbData;
    ArrayList<StatData> statDataList;
	StatListAdapter statListAdapter;
	ListView statView;
	long charId;
	StatData newStat;
	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// Get saved data if there is any
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		View rootView = inflater.inflate(R.layout.stat_list_page, container, false);
		
        btnPlus = (Button)rootView.findViewById(R.id.btn_plus);
        btnPlus.setOnClickListener(this);
        
        btnPlus = (Button)rootView.findViewById(R.id.btn_plus_text);
        btnPlus.setOnClickListener(this);
        
        dbData = DungeonDataSource.getInstance(getActivity().getApplicationContext());
		
		charId = dbData.getCurrentCharacter().getId();
		
		// Get the ListView and assign an event handler to it
		statView = (ListView) rootView.findViewById(R.id.list_stat_view);
		statView.setOnItemClickListener(new StatListClickListener());
		//populate the stats
		fillStats();
		return rootView;
	}
	
	public void fillStats(){	
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
	    		FragmentManager fm = getActivity().getSupportFragmentManager();
	    		AddStatDialog ad = new AddStatDialog();
	    		ad.addDialogListener(this);
	    		Bundle bundle = new Bundle();	
				bundle.putLong("charId",charId);
				ad.setArguments(bundle);
	    		ad.show(fm, "fragment_add_stat");
	    		break;
			}
			case R.id.btn_plus_text:
	    	{   		
	    		//pop up add window
	    		FragmentManager fm = getActivity().getSupportFragmentManager();
	    		AddStatTextDialog adText = new AddStatTextDialog();
	    		adText.addDialogListener(this);
	    		Bundle bundle = new Bundle();	
				bundle.putLong("charId",charId);
				adText.setArguments(bundle);
	    		adText.show(fm, "fragment_add_stat_text");
	    		break;
			}
		}		
	}
	
	private class StatListClickListener implements ListView.OnItemClickListener
	{
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		{
			//open the edit dialog here
			FragmentManager fm = getActivity().getSupportFragmentManager();
			//for edit stat value
    		EditStatDialog ed = new EditStatDialog();
    		ed.addDialogListener(StatListPageActivity.this);
    		//for edit stat text
    		EditStatTextDialog edText = new EditStatTextDialog();
    		edText.addDialogListener(StatListPageActivity.this);
    		//bundle
    		Bundle bundle = new Bundle();	
			bundle.putLong("charId",charId);
			StatData statToEdit = (StatData)statListAdapter.getItem(position);
			String statNameToEdit = statToEdit.getName();
			String statType = statToEdit.getType();
			bundle.putString("statNameToEdit", statNameToEdit);
			bundle.putString("statType", statType);
			if(statType.equals("Text")){
				edText.setArguments(bundle);
	    		edText.show(fm, "fragment_edit_stat_text");
			}else{
				ed.setArguments(bundle);
	    		ed.show(fm, "fragment_edit_stat");
			}
		}
	}

	@Override
	public void onDialogFinish(int dialogId) {
		switch(dialogId)
		{
			case R.id.dialog_add_stat:
			{
				fillStats();
			}
			case R.id.dialog_edit_stat:
			{
				fillStats();
			}
		}
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
        inflater.inflate(R.menu.stat_list_menu, menu);
      //Set the initial values of the check boxes
        //Only the most visible option will be checked (so if public, don't check shared, etc)
        CharacterData currentChar = dbData.getCurrentCharacter();
        if(currentChar.getPublic())
        {
        	menu.findItem(R.id.select_public).setChecked(true);
        } else if(currentChar.getShared())         
        {
        	menu.findItem(R.id.select_shared).setChecked(true);
        } else
        {
        	menu.findItem(R.id.select_private).setChecked(true);
        }
    }

}
