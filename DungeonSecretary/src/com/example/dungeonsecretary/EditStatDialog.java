package com.example.dungeonsecretary;


import java.util.ArrayList;
import java.util.List;

import com.example.dungeonsecretary.adapter.StatListAdapter;
import com.example.dungeonsecretary.model.StatData;
import com.example.dungeonsecretary.sql.DungeonDataSource;
import com.example.dungeonsecretary.StatListPageActivity;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class EditStatDialog extends DialogFragment implements OnClickListener{
	private EditText mEditText;
	String statName;
	DungeonDataSource dbData;
	long charId;
	
	
	//private EditText input;
	
	 public EditStatDialog() {
	        // Empty constructor required for DialogFragment
	    }
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_edit_stat, container);
       mEditText = (EditText) view.findViewById(R.id.txt_your_name);
       
       getDialog().setTitle("Enter Stat Name");
       dbData = DungeonDataSource.getInstance(getActivity().getApplicationContext());
       
       Button btn_save= (Button) view.findViewById(R.id.btn_save);
       Button btn_cancel= (Button) view.findViewById(R.id.btn_cancel);
       btn_save.setOnClickListener(this);
       btn_cancel.setOnClickListener(this);
       //input = new EditText(getActivity());

        return view;
    }
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_save:
    	{
    		statName = mEditText.getText().toString();
    		Bundle bundle = this.getArguments();
    		charId = bundle.getLong("charId");
    		StatData newStat = new StatData();	
    		newStat.setName(statName);
    		newStat.setCharacterId(charId);
    		newStat.setType("Text");
    		newStat.setValue("20");
    		
    		dbData.insertStat(newStat);
    		dismiss();
    		
    		break;
		}
		case R.id.btn_cancel:
		{
			//do nothing
			break;
		}
	
		
	}
		
	}
	   
	   
	} 

   

    

