package com.example.dungeonsecretary.model;

import java.util.List;

public class StatData {
	private long id;
	private static long characterId;
	private String name;
	
	private String type;
	private String value;
	
	private List<StatData> children;
	
	public long getId()
	{
		return id;
	}
	public void setId(long id)
	{
		this.id = id;
	}
			
	public long getCharacterId()
	{
		return characterId;
	}
	public void setCharacterId(long characterId)
	{
		this.characterId = characterId;
	}
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	
	public String getValue()
	{
		return value;
	}
	public void setValue(String value)
	{
		this.value = value;
	}
}
