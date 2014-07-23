package ap.mobile.prayertimes;

import java.util.ArrayList;
import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import ap.mobile.prayertimes.base.Prayer;
import ap.mobile.prayertimes.base.UserLocation;
import ap.mobile.prayertimes.interfaces.CalculatePrayerTimesInterface;
import ap.mobile.prayertimes.tasks.CalculatePrayerTimesTask;

public class MethodPreferenceFragment extends PreferenceFragment 
	implements OnSharedPreferenceChangeListener, OnPreferenceClickListener, CalculatePrayerTimesInterface {
	
	private double latitude;
	private double longitude;
	private SharedPreferences prefs;
	private Integer timezone;
	private CalculatePrayerTimesTask calculatePrayerTimesTask;
	private ArrayList<Intent> alarmIntent;
	private boolean reminderEnabled;
	private Context context;


	@Override
    public void onCreate(Bundle savedInstanceState) {
		//this.getActivity().setTheme(R.style.preferenceTextStyle);
        super.onCreate(savedInstanceState);
        this.context = (Context) getActivity();
        PreferenceManager.getDefaultSharedPreferences(
        		getActivity()).registerOnSharedPreferenceChangeListener(this);
        this.prefs = 
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
		if(this.prefs.getString(key, "").equals("0")) {
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
			return;
		}
		
		if(key.equals("reminderEnabledPreference")) {
			this.calculatePrayerTimes();
		}
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(preference.getKey().equals("locationPreference")) {
			if(this.prefs.getString("locationModePreference", "").equals("0")) {
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
	
	private void calculatePrayerTimes() {
		
		if(this.prefs == null)
        	this.prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		this.latitude = Double.valueOf(this.prefs.getString("latitude", "0"));
		this.longitude = Double.valueOf(this.prefs.getString("longitude", "0"));        
        this.timezone = Integer.valueOf(this.prefs.getString("timezonePreference", "0"));                        
		UserLocation userLocation = new UserLocation(this.latitude, this.longitude, this.timezone);
		this.calculatePrayerTimesTask = new CalculatePrayerTimesTask(this.prefs, this);
		this.calculatePrayerTimesTask.execute(userLocation);
	}

	@Override
	public void onCalculateComplete(ArrayList<Prayer> prayerTimes) {
		this.alarmIntent = new ArrayList<Intent>();
		//int i=0;
		for(Prayer p : prayerTimes) {
			if(!p.getAlarmable()) continue;
			
			Intent intent = new Intent(this.context.getApplicationContext(), ReminderReceiver.class);
			intent.putExtra("PrayerId", p.getId());
			intent.putExtra("PrayerName", p.getName());
			intent.putExtra("PrayerTime", p.getTimeString());
			intent.putExtra("PrayerHour", p.getHours());
			intent.putExtra("PrayerMinute", p.getMinutes());
			
			this.alarmIntent.add(intent);
			
			//boolean alarmUp = (PendingIntent.getBroadcast(getActivity().getApplicationContext(), 
			//		p.getId(), intent, PendingIntent.FLAG_NO_CREATE) != null);
			
			AlarmManager alarmManager = (AlarmManager) this.getActivity()
			        .getSystemService(Context.ALARM_SERVICE);
			this.reminderEnabled = this.prefs.getBoolean("reminderEnabledPreference", false);
			if(this.reminderEnabled) {				
				PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getActivity().getApplicationContext(), 
						p.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
				Calendar cal = p.getAlarmCalendar();
				Log.d(this.getActivity().getPackageName(), 
						"Alarm set for " + p.getName() + " on " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) );
				alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, p.getAlarmCalendar().getTimeInMillis(),
				        AlarmManager.INTERVAL_DAY, pendingIntent);
				
			} else {
				int i = 0;
				for(Intent in : this.alarmIntent) {
					PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getActivity().getApplicationContext(), 
							p.getId(), in, PendingIntent.FLAG_NO_CREATE);
					alarmManager.cancel(pendingIntent);
					Log.d("alarm clear", p.getName());
					this.alarmIntent.remove(i);
				}
			}
		}
		if(this.reminderEnabled)
			Toast.makeText(getActivity(), "Prayer times reminder activated", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(getActivity(), "Prayer times reminder inactive", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCalculateStart() {}
	
}
