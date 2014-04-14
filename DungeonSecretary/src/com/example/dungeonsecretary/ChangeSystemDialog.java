package com.example.dungeonsecretary;


import java.util.ArrayList;
import java.util.List;

import com.example.dungeonsecretary.adapter.StatListAdapter;
import com.example.dungeonsecretary.cloud.CloudOperations;
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


public class ChangeSystemDialog extends DialogFragment implements OnClickListener{
	private EditText mEditSystem;
	private DialogListener listener;
	DungeonDataSource dbData;
	long charId;
	
	 public ChangeSystemDialog() {
	    }
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_change_system, container);
       mEditSystem = (EditText) view.findViewById(R.id.txt_change_system);
       
       getDialog().setTitle("Set Game System");
       dbData = DungeonDataSource.getInstance(getActivity().getApplicationContext());
       
       Button btn_save= (Button) view.findViewById(R.id.btn_system_save);
       btn_save.setOnClickListener(this);
       
       
        return view;
    }
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_system_save:
	    	{
	    		String newSystem = mEditSystem.getText().toString();
	    		CharacterData ch = dbData.getCurrentCharacter();
	    		if (ch.getOwnerId() == dbData.getCurrentUser().getId())
	    		{
		    		CloudOperations.deleteCharacterFromCloud(ch.getName(), dbData.getCurrentUser().getGoogleAccount(), ch.getSystem());
	    		}
	    		ch.setSystem(newSystem);
	    		dbData.updateCharacter(ch);
	    		if (ch.getOwnerId() == dbData.getCurrentUser().getId())
	    		{
	    			CloudOperations.sendCharacterToCloud(ch.getName(), dbData.getCurrentUser().getId(), ch.getSystem(), ch.getShared(), ch.getPublic(), getActivity().getApplicationContext());
	    		}
	    		callDialogListeners();
	    		this.dismiss();
	    		
	    		break;
			}
		}	
	}	   	
	
	private void callDialogListeners()
	{
		listener.onDialogFinish(R.id.dialog_change_system);
	}
	
	public void addDialogListener(DialogListener dl)
	{
		listener = dl;
	}
} 

   

    

