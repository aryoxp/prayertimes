package ap.mobile.prayertimes.interfaces;

import java.util.ArrayList;

import ap.mobile.prayertimes.base.Prayer;

public interface CalculatePrayerTimesInterface {
	public void onCalculateComplete(ArrayList<Prayer> prayerTimes);
}
