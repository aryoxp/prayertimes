package ap.mobile.prayertimes;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			MainFragment mainFragment = new MainFragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, mainFragment).commit();
		}
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		switch(item.getItemId()) {
		case R.id.action_settings:
			
			Intent i = new Intent(getApplicationContext(), MethodPreferenceActivity.class);
			this.startActivity(i);
			
			return true;
		case R.id.action_about:
			AboutFragment aboutFragment = new AboutFragment();
			aboutFragment.show(getSupportFragmentManager(), "aboutFragment");
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
