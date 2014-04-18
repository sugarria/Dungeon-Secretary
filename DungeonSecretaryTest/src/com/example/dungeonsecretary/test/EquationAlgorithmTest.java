package com.example.dungeonsecretary.test;

import android.test.ActivityUnitTestCase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import com.example.dungeonsecretary.EquationAlgorithm;
import com.example.dungeonsecretary.model.CharacterData;
import com.example.dungeonsecretary.model.StatData;
import com.example.dungeonsecretary.model.UserData;
import com.example.dungeonsecretary.sql.DungeonDataSource;

public class EquationAlgorithmTest extends AndroidTestCase{
	DungeonDataSource dbData;
	RenamingDelegatingContext context;
	CharacterData character;
	EquationAlgorithm algor;
	long statId;
	long statId2;
	long statId3;
	long statId4;
	long statId5;
	long statId6;
	long statId7;
	long statId8;
	long statId9;
	long statId10;
	long statId11;
	long statId12;
	
	@Override
	public void setUp(){
		context	= new RenamingDelegatingContext(getContext(), "test_");
		
		dbData = DungeonDataSource.TESTgetInstance(context, "testDB.db");
		//input character data first
		insertCharacter("Roxanne");
		character = dbData.getCharacter(1, "Roxanne");
		//call the equation algorithm constructor
		algor = new EquationAlgorithm(context,"testDB.db");
		
		statId = insertStat(character.getId(),"power", "Number","50|");
		statId2 = insertStat(character.getId(),"power1", "Number","5|+|5|");
		statId3 = insertStat(character.getId(),"power2", "Number","0|");
		statId4 = insertStat(character.getId(),"power3", "Number","power|+|power1|");
		statId5 = insertStat(character.getId(),"power4", "Number","power|-|power1|");
		statId6 = insertStat(character.getId(),"power5", "Number","power|x|power1|");
		statId7 = insertStat(character.getId(),"power6", "Number","power|/|power1|");
		statId8 = insertStat(character.getId(),"power7", "Number","power|+|power1|+|0|");
		statId9 = insertStat(character.getId(),"power8", "Number","power|-|power1|+|4|");
		statId10 = insertStat(character.getId(),"power9", "Number","power|x|power1|+|4|");
		statId11 = insertStat(character.getId(),"power10", "Number","power|/|power1|+|6|");
		statId12 = insertStat(character.getId(),"power11", "Number","50|/|5|+|7|");
		Log.d("UnitTesting", "set up");
	}
	
	public void insertCharacter(String characterName){
		CharacterData character = new CharacterData();
		character.setName(characterName);
		character.setOwnerId(1);
		character.setPublic(true);
		character.setShared(true);
		character.setSystem("D&D");
		dbData.insertCharacter(character);
	}
	
	public long insertStat(long characterId, String name, String type, String value){
		StatData stat = new StatData();
		stat.setCharacterId(characterId);
		stat.setName(name);
		stat.setType(type);
		stat.setValue(value);
		
		dbData.insertStat(stat);
		return stat.getId();
	}
	
	public void testGetValueRandomNumber(){		
		int result = algor.getValue(dbData.getStat(statId).getValue(), character.getId());
		int result2 = algor.getValue(dbData.getStat(statId2).getValue(), character.getId());
		int result3 = algor.getValue(dbData.getStat(statId3).getValue(), character.getId());
		int result4 = algor.getValue(dbData.getStat(statId4).getValue(), character.getId());
		int result5 = algor.getValue(dbData.getStat(statId5).getValue(), character.getId());
		int result6 = algor.getValue(dbData.getStat(statId6).getValue(), character.getId());
		int result7 = algor.getValue(dbData.getStat(statId7).getValue(), character.getId());
		int result8 = algor.getValue(dbData.getStat(statId8).getValue(), character.getId());
		int result9 = algor.getValue(dbData.getStat(statId9).getValue(), character.getId());
		int result10 = algor.getValue(dbData.getStat(statId10).getValue(), character.getId());
		int result11 = algor.getValue(dbData.getStat(statId11).getValue(), character.getId());
		int result12 = algor.getValue(dbData.getStat(statId12).getValue(), character.getId());
		
		assertEquals("Stat Equation is a 50: ", result, 50);
		assertEquals("Stat Equation is a 5|+|5|: ", result2, 10);
		assertEquals("Stat Equation is a 0: ", result3, 0);
		assertEquals("Stat Equation is a power|+|power1|: ", result4, 60);
		assertEquals("Stat Equation is a power|-|power1|: ", result5, 40);
		assertEquals("Stat Equation is a power|x|power1|: ", result6, 500);
		assertEquals("Stat Equation is a power|/|power1|: ", result7, 5);
		assertEquals("Stat Equation is a power|+|power1|+|0|: ", result8, 60);
		assertEquals("Stat Equation is a power|-|power1|+|4|: ", result9, 44);
		assertEquals("Stat Equation is a power|x|power1|+|4|: ", result10, 504);
		assertEquals("Stat Equation is a power|/|power1|+|6|: ", result11, 11);
		assertEquals("Stat Equation is a 50|/|5|+|7|: ", result12, 17);
		
	}
	
	
	@Override
	public void tearDown(){
		dbData.close();
		context.deleteDatabase("testDB.db");
		Log.d("UnitTesting", "tear down");
		
	}

}
