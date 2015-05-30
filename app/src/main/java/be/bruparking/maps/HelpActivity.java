package be.bruparking.maps;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_help);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);
		MenuItem settings = menu.findItem(R.id.action_settings);
		settings.setVisible(false);
		// Hide icon on this activity. Disabled for now. This is saved for later
		//getActionBar().setIcon(android.R.color.transparent);
		return true;
	}


}
