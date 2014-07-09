package ap.mobile.prayertimes;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MethodPreferenceActivity extends FragmentActivity {

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
