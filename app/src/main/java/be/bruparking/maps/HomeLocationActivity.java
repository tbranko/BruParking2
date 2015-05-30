package be.bruparking.maps;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class HomeLocationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_home_location);
		
		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		TextView TextView_home_address = (TextView) findViewById(R.id.TextView_home_address);
		String text = sharedPref.getString(getString(R.string.pref_home_Text), "N/A in pref") + "\n";
		text += sharedPref.getString(getString(R.string.pref_home_lat), "LAT") + "\n";
		text += sharedPref.getString(getString(R.string.pref_home_lng), "LNG");
		TextView_home_address.setText(text);
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
