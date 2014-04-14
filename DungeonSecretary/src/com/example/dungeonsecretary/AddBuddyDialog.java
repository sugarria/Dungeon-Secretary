package com.example.dungeonsecretary;

import java.util.ArrayList;
import java.util.List;

import com.example.dungeonsecretary.cloud.CloudOperations;
import com.example.dungeonsecretary.interfaces.DialogListener;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddBuddyDialog extends DialogFragment implements OnClickListener {
    private EditText mEditUser;
    private String userEmail;
	private List<DialogListener> listeners;
	long charId;
	
	public AddBuddyDialog() {
		listeners = new ArrayList<DialogListener>();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_add_buddy, container);
		mEditUser = (EditText) view.findViewById(R.id.txt_add_buddy_email);
       
		getDialog().setTitle("Enter a user's email address to add yourself to their friends list.");
		
		Button btn_add= (Button) view.findViewById(R.id.btn_add_buddy_add);
	   	Button btn_done= (Button) view.findViewById(R.id.btn_add_buddy_done);
	   	btn_add.setOnClickListener(this);
	   	btn_done.setOnClickListener(this);

        return view;
    }
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_add_buddy_add:
	    	{
	    		userEmail = mEditUser.getText().toString();
	    		//search for user in cloud by email
	    		// if user exists: add buddy and toast success
	    		// else toast failure
	    		boolean success = CloudOperations.addCurrentUserAsBuddy(getActivity().getApplicationContext(), userEmail);
    			Context context = getActivity().getApplicationContext();
    			int duration = Toast.LENGTH_SHORT;
	    		if (success) {
	    			CharSequence text = "Successfully Added";

	    			Toast toast = Toast.makeText(context, text, duration);
	    			toast.show();
	    		} else {
	    			CharSequence text = "User Not Found";

	    			Toast toast = Toast.makeText(context, text, duration);
	    			toast.show();
	    		}
	    		
	    		callDialogListeners();
	    		
	    		break;
			}
			case R.id.btn_add_buddy_done:
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
			listeners.get(i).onDialogFinish(R.id.dialog_add_buddy);
		}
	}
	
	public void addDialogListener(DialogListener dl)
	{
		listeners.add(dl);
	}
}
