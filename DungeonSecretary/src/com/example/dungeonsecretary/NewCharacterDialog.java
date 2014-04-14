package com.example.dungeonsecretary;


import java.util.ArrayList;
import java.util.List;

import com.example.dungeonsecretary.adapter.StatListAdapter;
import com.example.dungeonsecretary.interfaces.DialogListener;
import com.example.dungeonsecretary.model.CharacterData;
import com.example.dungeonsecretary.model.StatData;
import com.example.dungeonsecretary.model.UserData;
import com.example.dungeonsecretary.sql.DungeonDataSource;
import com.example.dungeonsecretary.StatListPageActivity;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.view.inputmethod.EditorInfo;


public class NewCharacterDialog extends DialogFragment implements OnClickListener{
	private EditText mEditName;
	private String charName;
	private String system;
	private Spinner charSpinner;
	private List<DialogListener> listeners;
	DungeonDataSource dbData;
	long charId;
	
	 public NewCharacterDialog() {
	       listeners = new ArrayList<DialogListener>();
	    }
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_new_character, container);
       mEditName = (EditText) view.findViewById(R.id.txt_new_character_name);
       charSpinner = (Spinner) view.findViewById(R.id.spin_char_to_copy); 
       
       getDialog().setTitle("Create New Character");
       dbData = DungeonDataSource.getInstance(getActivity().getApplicationContext());
       
       Button btn_save= (Button) view.findViewById(R.id.btn_new_char_save);
       Button btn_cancel= (Button) view.findViewById(R.id.btn_new_char_cancel);
       btn_save.setOnClickListener(this);
       btn_cancel.setOnClickListener(this);
       
       //fill in the spinner
       List<CharacterData> chars = dbData.getAllCharacters();
       List<String> charNames = new ArrayList<String>();
       charNames.add("Blank");
       for(int i = 0; i < chars.size(); i++)
       {
    	   charNames.add(chars.get(i).getName());
       }
       ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(),
   	        android.R.layout.simple_spinner_dropdown_item);
       adapter.addAll(charNames);
       charSpinner.setAdapter(adapter);
       charSpinner.setSelection(0);
       
        return view;
    }
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_new_char_save:
	    	{
	    		String baseCharName = charSpinner.getSelectedItem().toString();	    		
	    		if(baseCharName.equals("Blank"))
	    		{
	    			CharacterData newChar = new CharacterData();
		    		charName = mEditName.getText().toString();	    		
		    		UserData owner = dbData.getCurrentUser();	
		    		newChar.setName(charName);
		    		newChar.setOwnerId(owner.getId());
		    		newChar.setSystem("");
		    		newChar.setPublic(false);
		    		newChar.setShared(false);
		    		dbData.insertCharacter(newChar);
		    	}
	    		else
	    		{
	    			CharacterData baseChar = dbData.getCharacter(dbData.getCurrentUser().getId(), baseCharName);
	    			charName = mEditName.getText().toString();
	    			dbData.DuplicateCharacter(baseChar, charName, dbData.getCurrentUser().getId());
	    		}
	
	    		callDialogListeners();
	    		
	    		this.dismiss();
	    		
	    		break;
			}
			case R.id.btn_new_char_cancel:
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
			listeners.get(i).onDialogFinish(R.id.dialog_new_character);
		}
	}
	
	public void addDialogListener(DialogListener dl)
	{
		listeners.add(dl);
	}
} 

   

    

