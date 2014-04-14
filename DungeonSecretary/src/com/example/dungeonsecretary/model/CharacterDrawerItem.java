package com.example.dungeonsecretary.model;

import com.parse.ParseObject;

public class CharacterDrawerItem {
    
    private String name;
    private String owner;
    private String system;
    private String ownerEmail;
    public CharacterDrawerItem(){}
 
    public CharacterDrawerItem(CharacterData character, String ownerName){
        this.name = character.getName();
        this.owner = ownerName;
        this.system = character.getSystem();
    }
    
    public CharacterDrawerItem(CharacterData character, String ownerName, String ownerEmail) {
    	this.name = character.getName();
    	this.owner = ownerName;
    	this.system = character.getSystem();
    	this.ownerEmail = ownerEmail;
    }
     
    public String getName(){
        return this.name;
    }
          
    public String getOwner(){
        return this.owner;
    }
    
    public String getSystem(){
    	return this.system;
    }
     
    public void setName(String name){
    	this.name = name;
    }  
     
    public void setOwner(String owner){
        this.owner = owner;
    }
    
    public void setSystem(String system){
    	this.system = system;
    }

    public String getOwnerEmail() {
    	return ownerEmail;
    }
}