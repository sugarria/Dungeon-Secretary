package com.example.dungeonsecretary;

import com.example.dungeonsecretary.model.CharacterData;
import com.example.dungeonsecretary.sql.DungeonDataSource;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
 
public class HomeFragment extends Fragment implements OnClickListener{
     
	private Button btnAddChar;
	private DungeonDataSource dbData;
	
    public HomeFragment(){}
         
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
          
        btnAddChar = (Button)rootView.findViewById(R.id.btn_add_character);
        btnAddChar.setOnClickListener(this);
        
        dbData = DungeonDataSource.getInstance(getActivity().getApplicationContext());
        
        return rootView;
    }
    
    public void onClick(View v)
    {
    	switch(v.getId()){
    		case R.id.btn_add_character:
	    	{
	    		CharacterData cha = new CharacterData();
	    		int num = dbData.getAllCharacters().size();
	    		cha.setName("Added Character" + ++num);
	    		long ownerId = dbData.getUser("'shawn@gmail.com'").getId();
	    		cha.setOwnerId(ownerId);
	    		cha.setSystem("Pokemon Tabletop Adventures");
	    		cha.setPublic(false);
	    		cha.setShared(false);
	    		
	    		dbData.insertCharacter(cha);
	    		break;
    		}
    	
    		
    	}
    	
    }
}