package com.example.dungeonsecretary;


import java.util.ArrayList;
import java.util.List;

import com.example.dungeonsecretary.adapter.StatListAdapter;
import com.example.dungeonsecretary.interfaces.DialogListener;
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


public class AddStatDialog extends DialogFragment implements OnClickListener, OnItemSelectedListener {
	private EditText mEditTextStatName;
	private EditText mEditTextStatType;
	private EditText mEditTextStatValue1;
	private EditText mEditTextStatValue2;
	private EditText mEditTextStatValue;
	private Spinner operation;
	String statName;
	String statType;
	String statValue1;
	String statValue2;
	String operationResult;
	String statValue;
	String statEquation;
	
	DungeonDataSource dbData;
	long charId;
	int value;

	private List<DialogListener> listeners;
	
	public AddStatDialog() {
	       listeners = new ArrayList<DialogListener>();
    }
	
	public interface EditNameDialogListener {
        void onFinishEditDialog();
    }
	 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_edit_stat, container);
       
       mEditTextStatName = (EditText) view.findViewById(R.id.txt_your_name);
       mEditTextStatType = (EditText) view.findViewById(R.id.txt_your_type);
       mEditTextStatValue1 = (EditText) view.findViewById(R.id.txt_your_value1);
       mEditTextStatValue2 = (EditText) view.findViewById(R.id.txt_your_value2);
       mEditTextStatValue = (EditText) view.findViewById(R.id.txt_your_value);
       operation = (Spinner) view.findViewById(R.id.txt_your_operation); 
       Button btn_save= (Button) view.findViewById(R.id.btn_save);
       Button btn_cancel= (Button) view.findViewById(R.id.btn_cancel);
    
       getDialog().setTitle("Enter Stat Name");
       dbData = DungeonDataSource.getInstance(getActivity().getApplicationContext());     
       
       operation.setOnItemSelectedListener(this);      
       btn_save.setOnClickListener(this);
       btn_cancel.setOnClickListener(this);
       
       //mEditTextStatValue1.addTextChangedListener(updateStatValue);
       //mEditTextStatValue1.addTextChangedListener(updateStatValue);

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
    		statType = mEditTextStatType.getText().toString();
    		statValue1 = mEditTextStatValue1.getText().toString();
    		statValue2 = mEditTextStatValue2.getText().toString();
    		StatData statValueFromOther;
    		StatData statValueFromOther2;
    		if(!isNumeric(statValue1)){
    			statValueFromOther = dbData.getStat(charId, statValue1);  			
    			statValue1 = changeToFinalValue(statValueFromOther.getValue());
    		}
    		if(!isNumeric(statValue2)){
    			statValueFromOther2 = dbData.getStat(charId, statValue2);
    			statValue2 = changeToFinalValue(statValueFromOther2.getValue());
    		}
    		
    		
    		if(operationResult.equals("+")){
    			value = Integer.parseInt(statValue1) + Integer.parseInt(statValue2);
    			statEquation = statValue1 + "|" + "+" + "|" + statValue2;
    		}
    		else if(operationResult.equals("-")){
    			value = Integer.parseInt(statValue1) - Integer.parseInt(statValue2);
    			statEquation = statValue1 + "|" + "-" + "|" + statValue2;
    			
    		} else if(operationResult.equals("x")){
    			value = Integer.parseInt(statValue1) * Integer.parseInt(statValue2);
    			statEquation = statValue1 + "|" + "*" + "|" + statValue2;
    			
    		}else if(operationResult.equals("/")){
    			value = Integer.parseInt(statValue1) / Integer.parseInt(statValue2);
    			statEquation = statValue1 + "|" + "/" + "|" + statValue2;
    			
    		}
    		mEditTextStatValue.setText(Integer.toString(value));
    	
    		statValue = Integer.toString(value);
    	
    		StatData newStat = new StatData();	
    		newStat.setName(statName);
    		newStat.setCharacterId(charId);
    		newStat.setType(statType);
    		newStat.setValue(statEquation);
    		dbData.insertStat(newStat);

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
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		operationResult = parent.getItemAtPosition(pos).toString();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private TextWatcher updateStatValue = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			
			statValue1 = mEditTextStatValue1.getText().toString();
    		statValue2 = mEditTextStatValue2.getText().toString();
    		if(operationResult.equals("+")){
    			value = Integer.parseInt(statValue1) ;
    		}
    		else if(operationResult.equals("-")){
    			value = Integer.parseInt(statValue1) ;
    			
    		} else if(operationResult.equals("x")){
    			value = Integer.parseInt(statValue1) ;
    			
    		}else if(operationResult.equals("/")){
    			value = Integer.parseInt(statValue1);
    			
    		}
    		mEditTextStatValue.setText(Integer.toString(value));
			
		}
		
	};
	
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	public String changeToFinalValue(String equation){
		 if (equation.contains("|")) {
	        	String[] values= equation.split("\\|");
	            String value1 = values[0];
	            String operation = values[1];
	            String value2 = values[2];
	            
	            int finalValue=0;
	            
	            if(operation.equals("+")){
	            	finalValue = Integer.parseInt(value1) + Integer.parseInt(value2);
	            }
	            else if(operation.equals("-")){
	            	finalValue = Integer.parseInt(value1) - Integer.parseInt(value2);
	            }
	            else if(operation.equals("x")){
	            	finalValue = Integer.parseInt(value1) * Integer.parseInt(value2);
	            }
	            else if(operation.equals("/")){
	            	finalValue = Integer.parseInt(value1) / Integer.parseInt(value2);
	            }
	           
	            return Integer.toString(finalValue);
	           
	        } else {
	        	return equation;
	        	
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

   

    

