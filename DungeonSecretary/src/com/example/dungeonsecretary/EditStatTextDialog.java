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


public class EditStatTextDialog extends DialogFragment implements OnClickListener {
	private TextView nameTextView;
	private EditText editValueTextView;

	String statName;
	String statType;

	StatData editStat;
	
	DungeonDataSource dbData;
	long charId;
	String statNameToEdit;
	int value;

	private List<DialogListener> listeners;
	
	public EditStatTextDialog() {
	       listeners = new ArrayList<DialogListener>();
    }
	
	public interface EditNameDialogListener {
        void onFinishEditDialog();
    }
	 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_edit_stat_text, container);
       
       dbData = DungeonDataSource.getInstance(getActivity().getApplicationContext());
       
       Bundle bundle = this.getArguments();
       charId = bundle.getLong("charId");
       statNameToEdit = bundle.getString("statNameToEdit");
       
      nameTextView = (TextView)view.findViewById(R.id.edit_txt_your_name);
      editValueTextView = (EditText)view.findViewById(R.id.txt_your_stat_text);
     
   	  editStat = dbData.getStat(charId, statNameToEdit); 
   	  
   	  nameTextView.setText(editStat.getName());
   	  editValueTextView.setText(editStat.getValue());

   	  statName = editStat.getName();
   	  statType = editStat.getType();
     
      Button btn_cancel = (Button) view.findViewById(R.id.btn_edit_cancel);
      Button btn_save= (Button) view.findViewById(R.id.btn_edit_save);
    
      getDialog().setTitle("Enter Stat Name");
   
      btn_cancel.setOnClickListener(this);
      btn_save.setOnClickListener(this);

        return view;
    }
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_edit_save:
    	{
    		Bundle bundle = this.getArguments();
    		charId = bundle.getLong("charId");

    	   	editStat = dbData.getStat(charId, statNameToEdit); 
    	   	String editTextValue = editValueTextView.getText().toString();
    		editStat.setValue(editTextValue);
    		dbData.updateStat(editStat);

    		// update character to cloud every time a stat is updated
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
		case R.id.btn_edit_cancel:
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
			listeners.get(i).onDialogFinish(R.id.dialog_edit_stat);
		}
	}
	
	public void addDialogListener(DialogListener dl)
	{
		listeners.add(dl);
	}
} 

   

    

