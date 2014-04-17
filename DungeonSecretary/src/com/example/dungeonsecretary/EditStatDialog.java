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
import android.text.InputType;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.view.inputmethod.EditorInfo;


public class EditStatDialog extends DialogFragment implements OnClickListener {
	private TextView nameTextView;
	private TextView typeTextView;
	private EditText value1EditText;
	private EditText value2EditText;
	private EditText finalValueEditText;
	private Spinner operation;
	
	private LinearLayout editStatLayout;
	private TextView textResult;
	private int childIndex=0;
	
	String statName;
	String statType;
	String statValue1;
	String statValue2;
	String operationResult;
	String statValue;
	String statEquation="";
	String value1FromEq;
    String operationFromEq;
    String value2FromEq;
	
	StatData editStat;
	
	DungeonDataSource dbData;
	long charId;
	String statNameToEdit;
	int value;

	private List<DialogListener> listeners;
	
	public EditStatDialog() {
	       listeners = new ArrayList<DialogListener>();
    }
	
	public interface EditNameDialogListener {
        void onFinishEditDialog();
    }
	 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_edit_stat, container);
       
       dbData = DungeonDataSource.getInstance(getActivity().getApplicationContext());
       
       Bundle bundle = this.getArguments();
       charId = bundle.getLong("charId");
       statNameToEdit = bundle.getString("statNameToEdit");
       
      nameTextView = (TextView)view.findViewById(R.id.edit_txt_your_name);
      typeTextView = (TextView)view.findViewById(R.id.edit_txt_your_type);
      editStatLayout = (LinearLayout)view.findViewById(R.id.edit_stat);
      textResult = (TextView)view.findViewById(R.id.text_result);
       
   	  editStat = dbData.getStat(charId, statNameToEdit); 
   	  
   	  nameTextView.setText(editStat.getName());
   	  typeTextView.setText("Number");
   	  
   	  EquationAlgorithm algor = new EquationAlgorithm(getActivity().getApplicationContext());
   	  List<String> views = algor.tokenizer(editStat.getValue());
   	  for(int i=0; i <views.size() ; i++){
   		  if(isNumeric(views.get(i))){
   			  EditText editText = new EditText(view.getContext());
   			  editText.setInputType(InputType.TYPE_CLASS_NUMBER);
   			  editText.setId(3);
   			  editText.setText(views.get(i));
   			  editStatLayout.addView(editText, childIndex);   
   			  
   		  }else if(views.get(i).equals("+") || views.get(i).equals("-") || 
   				views.get(i).equals("x") || views.get(i).equals("/")){
   			  
   			  Spinner spinner = new Spinner(view.getContext());
   			  spinner.setId(2);
   			  List<String> SpinnerOperation =  new ArrayList<String>();
   			  SpinnerOperation.add("+");
  			  SpinnerOperation.add("-");
  			  SpinnerOperation.add("x");
  			  SpinnerOperation.add("/");
  			 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, SpinnerOperation);
		       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		       spinner.setAdapter(adapter);
  			  
   			  if(views.get(i).equals("+")){
   				spinner.setSelection(0);
   			  }else if(views.get(i).equals("-")){
   				spinner.setSelection(1);  				  
   			  }else if(views.get(i).equals("x")){
   				spinner.setSelection(2);
   				  
   			  }else if(views.get(i).equals("/")){
   				spinner.setSelection(3);
   				  
   			  }
   			 
 			  editStatLayout.addView(spinner, childIndex);  
   			  
   		  }else{//a stat
   			Spinner mSpinnerOtherStat = new Spinner(view.getContext());
			mSpinnerOtherStat.setId(1);
			int indexOfSelection=0;
			List<StatData> allStats = dbData.getAllStatsForCharacter(charId);
			
			List<String> mSpinnerOtherStatArray =  new ArrayList<String>();
			for(int j=0; j < allStats.size(); j++){
				if(allStats.get(j).getType().equals("Number") && !(allStats.get(j).getName().equals(statNameToEdit))){//should be equal number
					mSpinnerOtherStatArray.add(allStats.get(j).getName());
					if(views.get(i).equals(allStats.get(j).getName())){
						indexOfSelection = j;
					}
				}
			}
		     
		       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, mSpinnerOtherStatArray);
		       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		       mSpinnerOtherStat.setAdapter(adapter);
		       mSpinnerOtherStat.setSelection(indexOfSelection);
		       editStatLayout.addView(mSpinnerOtherStat, childIndex);
   		  }
   		childIndex++;
   	  }
   	  
   	  textResult.setText(Integer.toString(algor.getValue(editStat.getValue(), charId)));
   	  statName = editStat.getName();
   	  
       Button btn_cancel = (Button) view.findViewById(R.id.btn_edit_cancel);
       Button btn_save= (Button) view.findViewById(R.id.btn_edit_save);
    
       getDialog().setTitle("Edit Stat");
               
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
    		statNameToEdit = bundle.getString("statNameToEdit");
    		
    		for(int i =0; i<childIndex; i++){
    			if(editStatLayout.getChildAt(i).getId() == 1){
    				Spinner spinner1 =(Spinner) editStatLayout.getChildAt(i);
    				String nameofStat = spinner1.getSelectedItem().toString();
    				statEquation= statEquation + nameofStat +"|";
    			}
    			else if(editStatLayout.getChildAt(i).getId() == 3){
    				EditText edittext3 = (EditText) editStatLayout.getChildAt(i);
    				String editTextResult="";
    				if (edittext3.getText().toString().equals("")){
    					editTextResult="0";
    					statEquation= statEquation + editTextResult+"|";
    				}
    				else{
    					statEquation= statEquation + edittext3.getText().toString()+"|";
    				}
    				
    				
    			}
    			else if(editStatLayout.getChildAt(i).getId() == 2){
    				Spinner spinner2 =(Spinner) editStatLayout.getChildAt(i);
    				String opName = spinner2.getSelectedItem().toString();//will return the operation
    				statEquation= statEquation + opName +"|";
    			}
    		}
    		
    		
    		EquationAlgorithm eqAlgorithm = new EquationAlgorithm(this.getActivity().getApplicationContext());
    		int showResult = eqAlgorithm.getValue(statEquation, charId);
    		textResult.setText(Integer.toString(showResult));
    	  		
    		editStat = dbData.getStat(charId, statNameToEdit); 
    		editStat.setValue(statEquation);
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
			listeners.get(i).onDialogFinish(R.id.dialog_edit_stat);
		}
	}
	
	public void addDialogListener(DialogListener dl)
	{
		listeners.add(dl);
	}
} 

   

    

