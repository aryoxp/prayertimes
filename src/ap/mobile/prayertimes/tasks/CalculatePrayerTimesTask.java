package ap.mobile.prayertimes.tasks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.os.AsyncTask;
import android.util.Log;
import ap.mobile.prayertimes.base.PrayerTime;
import ap.mobile.prayertimes.base.UserLocation;
import ap.mobile.prayertimes.interfaces.CalculatePrayerTimesInterface;
import ap.mobile.prayertimes.utilities.PrayTime;

public class CalculatePrayerTimesTask extends AsyncTask<UserLocation, Void, ArrayList<PrayerTime>> {

	CalculatePrayerTimesInterface mCallback;
	
	public CalculatePrayerTimesTask(CalculatePrayerTimesInterface mCallback) {
		this.mCallback = mCallback;
	}
	
	@Override
	protected ArrayList<PrayerTime> doInBackground(UserLocation... params) {
		
		PrayTime prayers = new PrayTime();
		
		prayers.setTimeFormat(PrayTime.Time12);
        prayers.setCalcMethod(PrayTime.Egypt);
        prayers.setAsrJuristic(PrayTime.Shafii);
        prayers.setAdjustHighLats(PrayTime.AngleBased);
        
        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        
        UserLocation userLocation = params[0];
        double latitude = userLocation.latitude;
        double longitude = userLocation.longitude;
        double timezone = userLocation.timezone;
        
        ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal, latitude, longitude, timezone);
        ArrayList<String> prayerNames = prayers.getTimeNames();
        ArrayList<PrayerTime> prayerTimesList = new ArrayList<PrayerTime>();
        for (int i = 0; i < prayerTimes.size(); i++) {
        	Log.d("prayer", prayerNames.get(i) + " - " + prayerTimes.get(i));
        	prayerTimesList.add(new PrayerTime(prayerNames.get(i), prayerTimes.get(i)));
        }
        
        return prayerTimesList;
        
	}
	
	@Override
	protected void onPostExecute(ArrayList<PrayerTime> result) {
		this.mCallback.onCalculateComplete(result);
	}

}
