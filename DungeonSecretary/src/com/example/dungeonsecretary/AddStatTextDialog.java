package com.example.dungeonsecretary;


import java.util.ArrayList;
import java.util.List;

import com.example.dungeonsecretary.adapter.StatListAdapter;
import com.example.dungeonsecretary.cloud.CloudOperations;
import com.example.dungeonsecretary.interfaces.DialogListener;
import com.example.dungeonsecretary.model.CharacterData;
import com.example.dungeonsecretary.model.StatData;
import com.example.dungeonsecretary.sql.DungeonDataSource;
import com.example.dungeonsecretary.StatListPageActivity;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.view.inputmethod.EditorInfo;


public class AddStatTextDialog extends DialogFragment implements OnClickListener {
	private EditText mEditTextStatName;
	private EditText mEditTextStatText;
	
	String statName;
	String statValue;
	
	
	DungeonDataSource dbData;
	long charId;
	int value;

	private List<DialogListener> listeners;
	
	public AddStatTextDialog() {
	       listeners = new ArrayList<DialogListener>();
    }
	
	 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_add_stat_text, container);
       
       mEditTextStatName = (EditText) view.findViewById(R.id.txt_your_name);
       mEditTextStatText = (EditText) view.findViewById(R.id.txt_your_stat_text);
      
       Button btn_save= (Button) view.findViewById(R.id.btn_save);
       Button btn_cancel= (Button) view.findViewById(R.id.btn_cancel);
    
       getDialog().setTitle("Enter Stat Name");
       dbData = DungeonDataSource.getInstance(getActivity().getApplicationContext());     
  
       btn_save.setOnClickListener(this);
       btn_cancel.setOnClickListener(this);

        return view;
    }
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_save:
    	{
    		Bundle bundle = this.getArguments();
    		charId = bundle.getLong("charId");
    		statName = mEditTextStatName.getText().toString();
    		if(mEditTextStatText.getText().toString().equals("")){
    			statValue ="";
    		}else{
	    		statValue = mEditTextStatText.getText().toString();
    		}
    		
    	
    		StatData newStat = new StatData();	
    		newStat.setName(statName);
    		newStat.setCharacterId(charId);
    		newStat.setType("Text");
    		newStat.setValue(statValue);
    		dbData.insertStat(newStat);
    		
    		// update character to cloud every time a stat is created; currently uploads all characters as public
    		CharacterData thisChar = dbData.getCharacter(charId);
    		if (dbData.getCurrentUser().getId() == thisChar.getOwnerId() && (thisChar.getShared() || thisChar.getPublic()))
    		{
    			CloudOperations.sendCharacterToCloud(thisChar.getName(), dbData.getCurrentUser().getId(), thisChar.getSystem(),
    											 	 thisChar.getShared(), thisChar.getPublic(), getActivity().getApplicationContext());
    		}
    		
    		callDialogListeners();
    		this.dismiss();
    		
    		break;
		}
		case R.id.btn_cancel:
		{
			//do nothing
			this.dismiss();
			break;
		}	
		}
		
	}
	
	   	

	private void callDialogListeners()
	{
		for(int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).onDialogFinish(R.id.dialog_add_stat);
		}
	}
	
	public void addDialogListener(DialogListener dl)
	{
		listeners.add(dl);
	}
} 

   

    

