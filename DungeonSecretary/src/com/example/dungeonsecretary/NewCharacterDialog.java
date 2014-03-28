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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.view.inputmethod.EditorInfo;


public class NewCharacterDialog extends DialogFragment implements OnClickListener{
	private EditText mEditName;
	private EditText mEditSystem;
	private String charName;
	private String system;
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
       mEditSystem = (EditText) view.findViewById(R.id.txt_new_character_system);
       
       getDialog().setTitle("Create New Character");
       dbData = DungeonDataSource.getInstance(getActivity().getApplicationContext());
       
       Button btn_save= (Button) view.findViewById(R.id.btn_new_char_save);
       Button btn_cancel= (Button) view.findViewById(R.id.btn_new_char_cancel);
       btn_save.setOnClickListener(this);
       btn_cancel.setOnClickListener(this);
       
       //input = new EditText(getActivity());

        return view;
    }
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_new_char_save:
	    	{
	    		charName = mEditName.getText().toString();
	    		system = mEditSystem.getText().toString();
	    		UserData owner = dbData.getCurrentUser();
	    		CharacterData newChar = new CharacterData();	
	    		newChar.setName(charName);
	    		newChar.setOwnerId(owner.getId());
	    		newChar.setSystem(system);
	    		newChar.setPublic(true);
	    		newChar.setShared(true);
	    		dbData.insertCharacter(newChar);
	
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

   

    

