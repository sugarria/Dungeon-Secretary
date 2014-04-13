package com.example.dungeonsecretary;


import java.util.ArrayList;
import java.util.List;

import com.example.dungeonsecretary.adapter.StatListAdapter;
import com.example.dungeonsecretary.interfaces.DialogListener;
import com.example.dungeonsecretary.model.CharacterData;
import com.example.dungeonsecretary.model.SheetFieldData;
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


public class SheetFieldDialog extends DialogFragment implements OnClickListener{
	EditText mEditLabel;
	String labelText;
	DialogListener listener;
	DungeonDataSource dbData;
	long charId;
	long statId;
	long index;
	
	 public SheetFieldDialog() {
	    }
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_sheet_field, container);
       mEditLabel = (EditText) view.findViewById(R.id.txt_label);
       
       //getDialog().setTitle("Create New Character");
       dbData = DungeonDataSource.getInstance(getActivity().getApplicationContext());
       
       Button btn_label= (Button) view.findViewById(R.id.btn_set_label);
       Button btn_stat= (Button) view.findViewById(R.id.btn_set_stat);
       Button btn_clear= (Button) view.findViewById(R.id.btn_clear_display);
       btn_label.setOnClickListener(this);
       btn_stat.setOnClickListener(this);
       btn_clear.setOnClickListener(this);
       
       Bundle bundle = this.getArguments();
       
       index = (Long) bundle.get("index");
       
       //input = new EditText(getActivity());

        return view;
    }
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_set_label:
	    	{
	    		labelText = mEditLabel.getText().toString();
	    		CharacterData currentChar = dbData.getCurrentCharacter();
	    		SheetFieldData fieldData = new SheetFieldData();
	    		fieldData.setCharId(currentChar.getId());
	    		fieldData.setIndex(index);
	    		fieldData.setLabel(labelText);
	    		fieldData.setStatId(DungeonDataSource.dbNullNum);
	    		dbData.insertSheetField(fieldData);
	
	    		callDialogListeners();
	    		
	    		this.dismiss();
	    		
	    		break;
			}
			case R.id.btn_set_stat:
			{
				String statName = "blah";
	    		CharacterData currentChar = dbData.getCurrentCharacter();
				StatData stat = dbData.getStat(currentChar.getId(), statName);
	    		SheetFieldData fieldData = new SheetFieldData();
	    		fieldData.setCharId(currentChar.getId());
	    		fieldData.setIndex(index);
	    		fieldData.setLabel("");
	    		fieldData.setStatId(stat.getId());
	    		dbData.insertSheetField(fieldData);
	    		callDialogListeners();
				this.dismiss();
				break;
			}
		}	
	}	   	
	
	private void callDialogListeners()
	{
		listener.onDialogFinish(R.id.dialog_sheet_field);
		
	}
	
	public void addDialogListener(DialogListener dl)
	{
		listener = dl;
	}
} 

   

    

