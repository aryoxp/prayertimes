package ap.mobile.prayertimes.tasks;

import java.util.ArrayList;
import java.util.Calendar;
import android.os.AsyncTask;
import ap.mobile.prayertimes.base.Prayer;
import ap.mobile.prayertimes.base.UserLocation;
import ap.mobile.prayertimes.interfaces.CalculatePrayerTimesInterface;
import ap.mobile.prayertimes.utilities.PrayTime;

public class CalculatePrayerTimesTask extends AsyncTask<UserLocation, Void, ArrayList<Prayer>> {

	CalculatePrayerTimesInterface mCallback;
	
	public CalculatePrayerTimesTask(CalculatePrayerTimesInterface mCallback) {
		this.mCallback = mCallback;
	}
	
	@Override
	protected ArrayList<Prayer> doInBackground(UserLocation... params) {
		
		PrayTime prayers = new PrayTime(PrayTime.Egypt, 
				PrayTime.Shafii, 
				PrayTime.Time12, 
				PrayTime.AngleBased);
        
        Calendar cal = Calendar.getInstance();
        
        UserLocation userLocation = params[0];
        double latitude = userLocation.latitude;
        double longitude = userLocation.longitude;
        double timezone = userLocation.timezone;
        
        ArrayList<Prayer> prayerTimesList = prayers.getPrayerList(cal, latitude, longitude, timezone);
        /*
        ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal, latitude, longitude, timezone);
        ArrayList<String> prayerNames = prayers.getTimeNames();
        ArrayList<PrayerTime> prayerTimesList = new ArrayList<PrayerTime>();
        for (int i = 0; i < prayerTimes.size(); i++) {
        	Log.d("prayer", prayerNames.get(i) + " - " + prayerTimes.get(i));
        	prayerTimesList.add(new PrayerTime(prayerNames.get(i), prayerTimes.get(i)));
        }
        */
        return prayerTimesList;
        
	}
	
	@Override
	protected void onPostExecute(ArrayList<Prayer> result) {
		this.mCallback.onCalculateComplete(result);
	}

}
