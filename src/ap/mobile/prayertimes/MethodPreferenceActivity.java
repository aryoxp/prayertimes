package ap.mobile.prayertimes;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class MethodPreferenceActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new MethodPreferenceFragment()).addToBackStack(null).commit();
		}
	}
	
}
