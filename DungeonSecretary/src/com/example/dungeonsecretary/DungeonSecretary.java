package com.example.dungeonsecretary;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class DungeonSecretary extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dungeon_secretary);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dungeon_secretary, menu);
		return true;
	}

}
