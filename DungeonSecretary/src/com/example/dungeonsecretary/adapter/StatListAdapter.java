package com.example.dungeonsecretary.adapter;

import com.example.dungeonsecretary.R;
import com.example.dungeonsecretary.model.StatData;
 
import java.util.ArrayList;
 
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
public class StatListAdapter extends BaseAdapter {
     
    private Context context;
    private ArrayList<StatData> StatDataList;
    private int finalValue;
    String[] values;
    String value1;
    String operation;
    String value2;
     
    public StatListAdapter(Context context, ArrayList<StatData> StatDataList){
        this.context = context;
        this.StatDataList = StatDataList;
    }
 
    @Override
    public int getCount() {
        return StatDataList.size();
    }
 
    @Override
    public Object getItem(int position) {       
        return StatDataList.get(position);
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
            convertView = mInflater.inflate(R.layout.stat_entry, null);
        }
        
        TextView statId =(TextView) convertView.findViewById(R.id.statId);
        TextView statName =(TextView) convertView.findViewById(R.id.statName);
        TextView statValue = (TextView) convertView.findViewById(R.id.statValue);
                 
        statId.setText(String.valueOf(StatDataList.get(position).getId()));
        statName.setText(StatDataList.get(position).getName());
        
        //take the equation from the value
        //parse the equation to calculate the value
        if (StatDataList.get(position).getValue().contains("|")) {
        	values= StatDataList.get(position).getValue().split("\\|");
            value1 = values[0];
            operation = values[1];
            value2 = values[2];
            
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
           
            statValue.setText(Integer.toString(finalValue));
        } else {
        	statValue.setText(StatDataList.get(position).getValue());
        }
        
        return convertView;
    }
 
}