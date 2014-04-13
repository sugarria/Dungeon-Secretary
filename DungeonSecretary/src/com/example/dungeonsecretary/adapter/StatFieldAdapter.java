package com.example.dungeonsecretary.adapter;

import com.example.dungeonsecretary.R;
import com.example.dungeonsecretary.model.StatData;
import com.example.dungeonsecretary.model.SheetFieldData;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
 
import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
 
public class StatFieldAdapter extends BaseAdapter {
     
    private Context context;
    private Map<Long, String> values;
    
    private int screenHeight;
    private int screenWidth;
    private int numRows;
    private int numColumns;
    
    private ArrayList<SheetFieldData> StatFieldList;
     
    public StatFieldAdapter(Context context){
        this.context = context;
        values = new HashMap<Long, String>();
    }
 
    @Override
    public int getCount() {
        return values.size();        	
    }
 
    @Override
    public Object getItem(int position) {       
        return StatFieldList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView text;
    	if (convertView == null) {
    		text = new TextView(context);
    		text.setLayoutParams(new GridView.LayoutParams(screenWidth/numColumns, screenHeight/numRows));
    		text.setBackgroundColor(color.holo_orange_dark);
    		text.setPadding(8,  8,  8,  8);
        } else
        {
        	text = (TextView) convertView;
        }
    	
    	long key = (long) position;
    	if(values.containsKey(key))
    	{
    		text.setText(values.get(key));
    	} else
    	{
    		text.setText("empty");
    	}
    	
    	return text;
    }
    
    public void setValue(long position, String value)
    {
    	values.put(position, value);
    }
    
    public void setScreenSize(int height, int width)
    {
    	screenHeight = height;
    	screenWidth = width;
    }
    
    public void setGridSize(int numRows, int numColumns)
    {
    	this.numRows = numRows;
    	this.numColumns = numColumns;
    }
 
}