package com.example.dungeonsecretary.model;

import java.util.List;

/*
 * The representation of the data of a character to be used by the rest
 * of the program. Handles the interactions with the database
 */
public class CharacterData {
	private long id;
	private long ownerId;
	private String characterName;
	private String system;
	private boolean isPublic;
	private boolean isShared;
	
	//public List<StatData> stats;
	
	public long getId()
	{
		return id;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}
	
	public long getOwnerId()
	{
		return ownerId;
	}
	
	public void setOwnerId(long ownerId)
	{
		this.ownerId = ownerId;
	}
	/*
	public String getOwnerName()
	{
		return ownerName;
	}
	
	public void setOwnerName(String name)
	{
		ownerName = name;
	}
	*/
	public String getName()
	{
		return characterName;
	}
	
	public void setName(String name)
	{
		characterName = name;
	}
	
	public String getSystem()
	{
		return system;
	}
	
	public void setSystem(String system)
	{
		this.system = system;
	}
	
	public boolean getPublic()
	{
		return isPublic;
	}
	
	public void setPublic(boolean isPublic)
	{
		this.isPublic = isPublic;
	}
	
	
	public boolean getShared()
	{
		return isShared;
	}
	
	public void setShared(boolean isShared)
	{
		this.isShared = isShared;
	}
	
}
