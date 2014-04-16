package com.example.dungeonsecretary.test;

import android.test.ActivityUnitTestCase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import com.example.dungeonsecretary.model.CharacterData;
import com.example.dungeonsecretary.model.UserData;
import com.example.dungeonsecretary.sql.DungeonDataSource;

public class DatabaseTest extends AndroidTestCase{
	DungeonDataSource dbData;
	RenamingDelegatingContext context;
	
	@Override
	public void setUp(){
		context	= new RenamingDelegatingContext(getContext(), "test_");
		
		dbData = DungeonDataSource.TESTgetInstance(context, "testDB.db");
		Log.d("UnitTesting", "set up");
	}
	
	private UserData AddTestUser(int num)
	{
		UserData testUser = new UserData();
		testUser.setGoogleAccount("test"+num+"@gmail.com");
		testUser.setUserName("Test McExample the "+num);
		boolean inserted = dbData.insertUser(testUser);
		if(inserted){
			return testUser;
		} else
		{
			return null;
		}
	}
	
	private CharacterData AddTestCharacter(UserData user, int num)
	{
		CharacterData testChar = new CharacterData();
		testChar.setName("Character"+num);
		testChar.setOwnerId(user.getId());
		testChar.setSystem("Game of Tests");
		testChar.setPublic(false);
		testChar.setShared(true);
		boolean inserted = dbData.insertCharacter(testChar);
		if(inserted){
			return testChar;
		} else
		{
			return null;
		}
	}
	
	private void checkUser(UserData input, UserData output)
	{
		assertEquals("User GAccount: ", input.getGoogleAccount(), output.getGoogleAccount());
		assertEquals("User UserName: ", input.getUserName(), output.getUserName());
		assertEquals("User Id:", input.getId(), output.getId());
	}
	
	private void checkCharacter(CharacterData input, CharacterData output)
	{
		assertEquals("Character Id:", input.getId(), output.getId());
		assertEquals("Character Name:", input.getName(), output.getName());
		assertEquals("Character OwnerId:", input.getOwnerId(), output.getOwnerId());
		assertEquals("Character System:", input.getSystem(), output.getSystem());
		assertEquals("Character Public:", input.getPublic(), output.getPublic());
		assertEquals("Character Shared:", input.getShared(), output.getShared());
		
	}
	
	public void testAddUser(){
		UserData testUser = AddTestUser(0);
		
		UserData foundUser = dbData.getUser(testUser.getId());
		checkUser(testUser, foundUser);
	}
	
	
	public void testAddMultipleUsers(){
		UserData testUser0 = AddTestUser(0);
		UserData testUser1 = AddTestUser(1);
		UserData testUser2 = AddTestUser(2);
		UserData testUser3 = AddTestUser(3);
		
		checkUser(testUser0, dbData.getUser(testUser0.getGoogleAccount()));
		checkUser(testUser1, dbData.getUser(testUser1.getGoogleAccount()));
		checkUser(testUser2, dbData.getUser(testUser2.getGoogleAccount()));
		checkUser(testUser3, dbData.getUser(testUser3.getGoogleAccount()));
		
	}
	
	public void testGetAllUsers(){
		UserData testUser0 = AddTestUser(0);
		UserData testUser1 = AddTestUser(1);
		UserData testUser2 = AddTestUser(2);
		UserData testUser3 = AddTestUser(3);
		//I'd do more in depth testing than just checking the number, but I'm kinda short on time...
		assertEquals(dbData.getAllUsers().size(), 4);
	}
	
	public void testUpdateUser()
	{
		UserData testUser0 = AddTestUser(0);
		UserData testUser1 = AddTestUser(1);
		
		testUser0.setUserName("New name");
		dbData.updateUser(testUser0);
		//check that testUser0 has changed and that testUser1 has not
		checkUser(testUser0, dbData.getUser(testUser0.getId()));
		checkUser(testUser1, dbData.getUser(testUser1.getId()));
	}
	
	public void testAddDuplicateUser(){
		//these will have the same user and account names
		UserData testUserA = AddTestUser(0);
		UserData testUserB = AddTestUser(0);
		
		assertNull("Failed to catch duplicated user", testUserB);
	}

	public void testAddCharacter(){
		UserData testUser = AddTestUser(0);
		CharacterData testChar = AddTestCharacter(testUser, 0);
		checkCharacter(testChar, dbData.getCharacter(testChar.getId()));
	}
	
	public void testAddMultiCharsToOneUser(){
		UserData testUser = AddTestUser(0);
		CharacterData testChar0 = AddTestCharacter(testUser, 0);
		CharacterData testChar1 = AddTestCharacter(testUser, 1);
		CharacterData testChar2 = AddTestCharacter(testUser, 2);
		checkCharacter(testChar0, dbData.getCharacter(testChar0.getId()));
		checkCharacter(testChar1, dbData.getCharacter(testChar1.getId()));
		checkCharacter(testChar2, dbData.getCharacter(testChar2.getId()));		
	}
	
	public void testAddSameNameCharToMultiUsers()
	{
		UserData testUser0 = AddTestUser(0);
		UserData testUser1 = AddTestUser(1);
		
		CharacterData testChar0 = AddTestCharacter(testUser0, 0);
		CharacterData testChar1 = AddTestCharacter(testUser1, 0);//Same number, so it will have the same name
		
		//the main sign of a bug here will be if it crashes, but might as well do asserts too
		assertNotNull("Failed to insert character with same name to different user", testChar1);
		
		checkCharacter(testChar0, dbData.getCharacter(testChar0.getId()));
		checkCharacter(testChar1, dbData.getCharacter(testChar1.getId()));
	}
	
	public void testGetAllChars()
	{
		UserData testUser = AddTestUser(0);
		UserData testUser1 = AddTestUser(1);
		CharacterData testChar0 = AddTestCharacter(testUser, 0);
		CharacterData testChar1 = AddTestCharacter(testUser, 1);
		CharacterData testChar2 = AddTestCharacter(testUser, 2);
		
		CharacterData testChar3 = AddTestCharacter(testUser1, 0);
		
		assertEquals(dbData.getAllCharacters().size(), 4);
	}
	
	public void testUpdateCharacter()
	{
		UserData testUser = AddTestUser(0);
		CharacterData testChar = AddTestCharacter(testUser, 0);
		CharacterData testChar1 = AddTestCharacter(testUser, 1);
		
		testChar.setName("new name");
		testChar.setSystem("new system");
		testChar.setShared(false);
		testChar.setShared(true);
		
		dbData.updateCharacter(testChar);
		checkCharacter(testChar, dbData.getCharacter(testChar.getId()));
		
	}
	
	public void testDuplicateCharacter()
	{
		UserData testUser = AddTestUser(0);
		CharacterData testChar = AddTestCharacter(testUser, 0);
		dbData.DuplicateCharacter(testChar, "Dupy", testUser.getId());
		CharacterData dup = new CharacterData();
		dup.setName("Dupy");
		dup.setOwnerId(testUser.getId());
		dup.setSystem(testChar.getSystem());
		//duplicated characters are set to private at first
		dup.setPublic(false);
		dup.setShared(false);
		//the id is set when you add it, so we just need to cheat and grab it, then make sure
		//everything else copied right		
		dup.setId(dbData.getCharacter(testUser.getId(), "Dupy").getId());
		checkCharacter(dup, dbData.getCharacter(dup.getId()));
	}
	
	@Override
	public void tearDown(){
		dbData.close();
		context.deleteDatabase("testDB.db");
		Log.d("UnitTesting", "tear down");
		
	}
	
	//Note: Could do tests for stats and sheet fields, but they would be pretty much the same and
	//we're running low on time. The interesting cases in that would mostly be making sure that 
	//creating duplicate characters also copies the relevant stats and fields, which we know to 
	//be working from manual testing. 
}
