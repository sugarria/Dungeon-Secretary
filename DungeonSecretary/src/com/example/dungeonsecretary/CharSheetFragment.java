package com.example.dungeonsecretary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.dungeonsecretary.adapter.StatFieldAdapter;
import com.example.dungeonsecretary.interfaces.DialogListener;
import com.example.dungeonsecretary.model.CharacterData;
import com.example.dungeonsecretary.model.SheetFieldData;
import com.example.dungeonsecretary.model.StatData;
import com.example.dungeonsecretary.sql.DungeonDataSource;
import com.google.android.gms.internal.db;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import android.widget.GridLayout;
import android.widget.GridView;
 
public class CharSheetFragment extends Fragment implements OnClickListener, DialogListener{
    private final int numRows = 15;
    private final int numColumns = 6;
	
	
	private DungeonDataSource dbData;
	private StatFieldAdapter adapter;
	private Map<Long, SheetFieldData> statFields;
	//Track the field that is being changed by a dialog so we know which one to update in dialogFinished
	private long changingField;
	
    public CharSheetFragment(){}
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.char_sheet_layout, container, false);
        setHasOptionsMenu(true);
          
        dbData = DungeonDataSource.getInstance(getActivity().getApplicationContext());
        
        adapter = new StatFieldAdapter(this.getActivity());
        
        GridView grid = (GridView)rootView.findViewById(R.id.char_sheet_grid);
        grid.setNumColumns(numColumns);
        
        //get list of all stat fields for this character from db
        List<SheetFieldData> fields = dbData.getAllFieldsForCharacter(dbData.getCurrentCharacter().getId());
        //put them in the map
        statFields = new HashMap<Long, SheetFieldData>();
        for(int i = 0; i < fields.size(); i++)
        {
        	statFields.put(fields.get(i).getIndex(), fields.get(i));
        }
        //fill in the grid with blank fields
        for(int i = 0; i < numRows * numColumns; i++)
        {
        	adapter.setValue(i, "");
        }
        //put all the fields in the adapter
        for(long key : statFields.keySet())
    	{
        	adapter.setValue(key, getFieldValue(statFields.get(key)));
    	}
       //set the screen size for the adapter
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        adapter.setScreenSize(metrics.heightPixels, metrics.widthPixels);
        adapter.setGridSize(numRows, numColumns);
        grid.setAdapter(adapter);
        
        grid.setOnItemClickListener(new OnItemClickListener()
        {
        	@Override
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id)
    		{
    			//pop up add window
        		FragmentManager fm = getActivity().getSupportFragmentManager();
        		SheetFieldDialog sf = new SheetFieldDialog();
        		sf.addDialogListener(CharSheetFragment.this);
        		Bundle bundle = new Bundle();	
    			bundle.putLong("index",(long)position);
    			sf.setArguments(bundle);
        		sf.show(fm, "fragment_sheet_field");
        		changingField = id;
    		}
        });
        return rootView;
    }
    
    private String getFieldValue(SheetFieldData field)
    {
    	if(field.getStatId() == DungeonDataSource.dbNullNum)
    	{
    		return field.getLabel();
    	}
    	StatData stat = dbData.getStat(field.getStatId());
    	if(stat.getType().equals("Text")){
    		return stat.getValue();
    	}else{
	    	EquationAlgorithm algor = new EquationAlgorithm(this.getActivity().getApplicationContext());
	    	int result = algor.getValue(stat.getValue(), stat.getCharacterId());
	    	return Integer.toString(result);
    	}
    }

    
    public void onClick(View v)
    {
    	switch(v.getId()){
    		case R.id.btn_add_character:
	    	{
	    		break;
    		}
    	
    		
    	}
    	
    }

	@Override
	public void onDialogFinish(int dialogId) {
		switch(dialogId)
		{
		case R.id.dialog_sheet_field:
		{
			SheetFieldData changed = dbData.getSheetField(dbData.getCurrentCharacter().getId(), changingField);
			adapter.setValue(changingField, getFieldValue(changed));
			adapter.notifyDataSetChanged();
		}
		}
		
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
        inflater.inflate(R.menu.char_sheet_menu, menu);
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
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       
        // Handle action bar actions click
        switch (item.getItemId()) {
        case R.id.action_settings:
        {
        	//Fragment fragment = new CharSheetFragment();
			//FragmentManager fragmentManager = getSupportFragmentManager();
			//fragmentManager.beginTransaction().replace(R.id.frame_container,  fragment).commit();
            return true;
        }
        
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	
}
