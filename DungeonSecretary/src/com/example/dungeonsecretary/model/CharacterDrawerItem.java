package com.example.dungeonsecretary.model;

public class CharacterDrawerItem {
    
    private String name;
    private String owner;
    private String system;
    public CharacterDrawerItem(){}
 
    public CharacterDrawerItem(CharacterData character, String ownerName){
        this.name = character.getName();
        this.owner = ownerName;
        this.system = character.getSystem();
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

}