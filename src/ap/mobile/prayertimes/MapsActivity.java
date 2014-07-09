package ap.mobile.prayertimes;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MapsActivity extends FragmentActivity {
	
	//private View rootView;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.setContentView(R.layout.activity_maps);
		if (bundle == null) {
			this.getSupportFragmentManager().beginTransaction()
				.add(R.id.mapsLayoutContainer, new MapsFragment()).commit();
		}
	}
}
