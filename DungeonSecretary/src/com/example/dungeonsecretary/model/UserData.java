package com.example.dungeonsecretary.model;

public class UserData {
	private long id;
	private String google_account;
	private String user_name;
	
	public long getId()
	{
		return id;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}
	
	public String getGoogleAccount()
	{
		return google_account;
	}
	
	public void setGoogleAccount(String gAccount)
	{
		google_account = gAccount;
	}
	
	public String getUserName()
	{
		return user_name;
	}
	
	public void setUserName(String uName)
	{
		user_name = uName;
	}
}
