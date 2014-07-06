package ap.mobile.prayertimes.tasks;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import ap.mobile.prayertimes.base.Prayer;
import ap.mobile.prayertimes.base.UserLocation;
import ap.mobile.prayertimes.interfaces.CalculatePrayerTimesInterface;
import ap.mobile.prayertimes.utilities.PrayTime;

public class CalculatePrayerTimesTask extends AsyncTask<UserLocation, Void, ArrayList<Prayer>> {

	private CalculatePrayerTimesInterface mCallback;
	private SharedPreferences prefs;
	
	public CalculatePrayerTimesTask(SharedPreferences prefs, CalculatePrayerTimesInterface mCallback) {
		this.mCallback = mCallback;
		this.prefs = prefs;
	}
	
	@Override
	protected ArrayList<Prayer> doInBackground(UserLocation... params) {
				
		PrayTime prayTime = new PrayTime(Integer.valueOf(this.prefs.getString("calcMethodPreference", "0")), 
				Integer.valueOf(this.prefs.getString("asrJuristicPreference", "0")), 
				Integer.valueOf(this.prefs.getString("timeFormatPreference", "0")), 
				Integer.valueOf(this.prefs.getString("adjustHighLatsPreference", "0")));
        
        Calendar cal = Calendar.getInstance();
        
        UserLocation userLocation = params[0];
        double latitude = userLocation.latitude;
        double longitude = userLocation.longitude;
        double timezone = userLocation.timezone;
        
        ArrayList<Prayer> prayerTimesList = prayTime.getPrayerList(cal, latitude, longitude, timezone);        
        return prayerTimesList;
        
	}
	
	@Override
	protected void onPostExecute(ArrayList<Prayer> result) {
		this.mCallback.onCalculateComplete(result);
	}

}
