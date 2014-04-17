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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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


public class AddStatDialog extends DialogFragment implements OnClickListener {
	private EditText mEditTextStatName;
	private Spinner mSpinnerStatOption;
	private Button mButtonStatOption;
	private LinearLayout statAddLayout;
	private TextView mEditTextResult;

	//for adding new View
	private Spinner mSpinnerOtherStat;
	private Spinner mSpinnerOperation;
	private EditText mEditTextNumber;
	private int childIndex=0;
	View view;

	String statName;
	String statType;
	String statValue1;
	String statValue2;
	String operationResult;
	String statValue;
	String statEquation="";
	String statTypeResult;

	String optionResult;

	DungeonDataSource dbData;
	long charId;
	int value;

	private List<DialogListener> listeners;

	public AddStatDialog() {
	       listeners = new ArrayList<DialogListener>();
    }


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_add_stat, container);
       
       mEditTextStatName = (EditText) view.findViewById(R.id.txt_your_name);
       mSpinnerStatOption = (Spinner) view.findViewById(R.id.spinner_stat_option);
       mButtonStatOption = (Button) view.findViewById(R.id.add_option);
       statAddLayout = (LinearLayout) view.findViewById(R.id.add_stat);
       mEditTextResult = (TextView) view.findViewById(R.id.text_result);
      
       Button btn_save= (Button) view.findViewById(R.id.btn_save);
       Button btn_cancel= (Button) view.findViewById(R.id.btn_cancel);
    
       getDialog().setTitle("Enter Stat Name");
       dbData = DungeonDataSource.getInstance(getActivity().getApplicationContext());     
           
       mButtonStatOption.setOnClickListener(this);
       btn_save.setOnClickListener(this);
       btn_cancel.setOnClickListener(this);
       
       //add items in Spinner
       List<String> SpinnerArray =  new ArrayList<String>();
      
       SpinnerArray.add("Number");
       Bundle bundle = this.getArguments();
       charId = bundle.getLong("charId");
       int numberOfStatNumber=0;
       List<StatData> allStats = dbData.getAllStatsForCharacter(charId);
       for(int i=0; i < allStats.size(); i++){
			if(allStats.get(i).getType().equals("Number")){//should be equal number
				numberOfStatNumber++;
			}
		}
		if(numberOfStatNumber >0){
	   	   SpinnerArray.add("Other Stat");
	      }

       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, SpinnerArray);
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       mSpinnerStatOption.setAdapter(adapter);
       

        return view;
    }


	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_save:
	    	{
	    		Bundle bundle = this.getArguments();
	    		charId = bundle.getLong("charId");
	    		if(childIndex==0){
	    			this.dismiss();
	    		}
	    		else{
		    		for(int i =0; i<childIndex; i++){
		    			if(statAddLayout.getChildAt(i).getId() == 1){
		    				Spinner spinner1 =(Spinner) statAddLayout.getChildAt(i);
		    				String nameofStat = spinner1.getSelectedItem().toString();
		    				statEquation= statEquation + nameofStat +"|";
		    			}
		    			else if(statAddLayout.getChildAt(i).getId() == 3){
		    				EditText edittext3 = (EditText) statAddLayout.getChildAt(i);
		    				String editTextResult="";
		    				if (edittext3.getText().toString().equals("")){
		    					editTextResult="0";
		    					statEquation= statEquation + editTextResult+"|";
		    				}
		    				else{
		    					statEquation= statEquation + edittext3.getText().toString()+"|";
		    				}


		    			}
		    			else if(statAddLayout.getChildAt(i).getId() == 2){
		    				Spinner spinner2 =(Spinner) statAddLayout.getChildAt(i);
		    				String opName = spinner2.getSelectedItem().toString();//will return the operation
		    				statEquation= statEquation + opName +"|";
		    			}
		    		}


		    		EquationAlgorithm eqAlgorithm = new EquationAlgorithm(this.getActivity().getApplicationContext());
		    		int showResult = eqAlgorithm.getValue(statEquation, charId);
		    		mEditTextResult.setText(Integer.toString(showResult));

		    		statName = mEditTextStatName.getText().toString();
		    		StatData newStat = new StatData();	
		    		newStat.setName(statName);
		    		newStat.setCharacterId(charId);
		    		newStat.setType("Number");
		    		newStat.setValue(statEquation);
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
	    		}
	    		break;
			}
			case R.id.btn_cancel:
			{
				//do nothing
				this.dismiss();
				break;
			}	
			case R.id.add_option:
			{
				optionResult = mSpinnerStatOption.getSelectedItem().toString();
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); 
				if(optionResult.equals("Other Stat")){
					mSpinnerOtherStat = new Spinner(view.getContext());
					mSpinnerOtherStat.setLayoutParams(params);
					mSpinnerOtherStat.setId(1);

					Bundle bundle = this.getArguments();
		    		charId = bundle.getLong("charId");
					List<StatData> allStats = dbData.getAllStatsForCharacter(charId);

					List<String> mSpinnerOtherStatArray =  new ArrayList<String>();
					for(int i=0; i < allStats.size(); i++){
						if(allStats.get(i).getType().equals("Number")){//should be equal number
							mSpinnerOtherStatArray.add(allStats.get(i).getName());
						}
					}

				       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, mSpinnerOtherStatArray);
				       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				       mSpinnerOtherStat.setAdapter(adapter);
				
					statAddLayout.addView(mSpinnerOtherStat, childIndex);

					List<String> SpinnerArray =  new ArrayList<String>();
				       SpinnerArray.add("Operation");

				       ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, SpinnerArray);
				       adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				       mSpinnerStatOption.setAdapter(adapter2);
				  

				}
				else if(optionResult.equals("Operation")){
					mSpinnerOperation = new Spinner(this.getActivity());
					mSpinnerOperation.setLayoutParams(params);
					mSpinnerOperation.setId(2);

					List<String> mSpinnerOperationArray =  new ArrayList<String>();
					mSpinnerOperationArray.add("+");
					mSpinnerOperationArray.add("-");
					mSpinnerOperationArray.add("x");
					mSpinnerOperationArray.add("/");

				       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, mSpinnerOperationArray);
				       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				       mSpinnerOperation.setAdapter(adapter);

			

				    statAddLayout.addView(mSpinnerOperation, childIndex);
				    //reset the spinner item

				    List<String> SpinnerArray =  new ArrayList<String>();
				    SpinnerArray.add("Number");
				       Bundle bundle = this.getArguments();
				       charId = bundle.getLong("charId");
				       int numberOfStatNumber=0;
					   List<StatData> allStats = dbData.getAllStatsForCharacter(charId);
					   for(int i=0; i < allStats.size(); i++){
						if(allStats.get(i).getType().equals("Number")){//should be equal number
							numberOfStatNumber++;
							}
					   }
					   if(numberOfStatNumber >0){
						   	SpinnerArray.add("Other Stat");
					   }

				       ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, SpinnerArray);
				       adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				       mSpinnerStatOption.setAdapter(adapter2);
				//       mSpinnerStatOption.setOnItemSelectedListener(this);

				}
				else if(optionResult.equals("Number")){
					mEditTextNumber = new EditText(view.getContext());
					mEditTextNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
					mEditTextNumber.setId(3);
					statAddLayout.addView(mEditTextNumber, childIndex);
					//reset the spinner item

				    List<String> SpinnerArray =  new ArrayList<String>();
				       SpinnerArray.add("Operation");

				       ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, SpinnerArray);
				       adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				       mSpinnerStatOption.setAdapter(adapter2);
				

				}
				childIndex++;
				break;
			}	
		}

	}


	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
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

   

    
