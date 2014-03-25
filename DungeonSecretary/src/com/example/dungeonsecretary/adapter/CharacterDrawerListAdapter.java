package com.example.dungeonsecretary.adapter;

import com.example.dungeonsecretary.R;
import com.example.dungeonsecretary.model.CharacterDrawerItem;
import com.example.dungeonsecretary.model.NavDrawerItem;
 
import java.util.ArrayList;
 
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class CharacterDrawerListAdapter extends BaseAdapter {
     
    private Context context;
    private ArrayList<CharacterDrawerItem> charDrawerItems;
     
    public CharacterDrawerListAdapter(Context context, ArrayList<CharacterDrawerItem> charDrawerItems){
        this.context = context;
        this.charDrawerItems = charDrawerItems;
    }
 
    @Override
    public int getCount() {
        return charDrawerItems.size();
    }
 
    @Override
    public Object getItem(int position) {       
        return charDrawerItems.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.character_list_item, null);
        }
        TextView charName = (TextView) convertView.findViewById(R.id.character_name);
        TextView ownerName = (TextView) convertView.findViewById(R.id.owner_name);
        TextView system = (TextView) convertView.findViewById(R.id.system);
          
        charName.setText(charDrawerItems.get(position).getName());
        ownerName.setText(charDrawerItems.get(position).getOwner());
        system.setText(charDrawerItems.get(position).getSystem());
                  
        return convertView;
    }
 
}