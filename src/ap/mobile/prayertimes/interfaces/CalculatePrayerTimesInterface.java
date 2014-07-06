package ap.mobile.prayertimes.interfaces;

import java.util.ArrayList;

import ap.mobile.prayertimes.base.PrayerTime;

public interface CalculatePrayerTimesInterface {
	public void onCalculateComplete(ArrayList<PrayerTime> prayerTimes);
}
