package ap.mobile.prayertimes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class MethodPreferenceFragment extends PreferenceFragment 
	implements OnSharedPreferenceChangeListener, OnPreferenceClickListener {
	
	SharedPreferences sharedPreferences;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		//this.getActivity().setTheme(R.style.preferenceTextStyle);
        super.onCreate(savedInstanceState);
        
        PreferenceManager.getDefaultSharedPreferences(
        		getActivity()).registerOnSharedPreferenceChangeListener(this);
        this.sharedPreferences = 
				PreferenceManager.getDefaultSharedPreferences(getActivity());
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
    }
	
	@Override
	public void onResume() {
		String key = "locationModePreference";
		Preference locationPreference = this.findPreference("locationPreference");
		if(locationPreference != null)
			locationPreference.setOnPreferenceClickListener(this);
		if(this.sharedPreferences.getString(key, "").equals("0")) {
			locationPreference.setEnabled(false);
		}
		super.onResume();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(key.equals("locationModePreference")) {
			String value = sharedPreferences.getString(key, "");
			if(value.equals("0")) {
				this.findPreference("locationPreference").setEnabled(false);
			} else this.findPreference("locationPreference").setEnabled(true);
		}
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(preference.getKey().equals("locationPreference")) {
			if(this.sharedPreferences.getString("locationModePreference", "").equals("0")) {
				Toast.makeText(getActivity(), 
						"You have to set Location Mode to Manual in Settings", 
						Toast.LENGTH_SHORT).show();
				return false;
			}
			Intent i = new Intent(getActivity(), MapsActivity.class);
			this.getActivity().startActivity(i);
		}
		return false;
	}
	
}
