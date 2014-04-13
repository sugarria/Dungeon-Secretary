package com.example.dungeonsecretary.model;

public class SheetFieldData {
	private long charId;
	private long statId;
	private long index;
	
	private String label;
	
	public long getCharId()
	{
		return charId;
	}
	public void setCharId(long id)
	{
		this.charId = id;
	}
	
	public long getStatId()
	{
		return statId;
	}
	public void setStatId(long id)
	{
		this.statId = id;
	}
	
	public long getIndex()
	{
		return index;
	}
	public void setIndex(long index)
	{
		this.index = index;
	}
			
	public String getLabel()
	{
		return label;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
}
