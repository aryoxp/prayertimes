package ap.mobile.prayertimes.tasks;

import android.content.Context;
import android.os.AsyncTask;
import ap.mobile.prayertimes.interfaces.LocationInterface;
import ap.mobile.prayertimes.utilities.LocationHelper;

public class LocationTask extends AsyncTask<Void, Void, String> {

	private Context context;
	private double longitude;
	private double latitude;
	private LocationInterface locationInterface;
	
	public LocationTask(Context context, double latitude, double longitude, LocationInterface locationInterface) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.context = context;
		this.locationInterface = locationInterface;
	}
	
	@Override
	protected String doInBackground(Void... params) {
		return LocationHelper.getLocation(this.context, this.latitude, this.longitude);
	}
	
	@Override
	protected void onPostExecute(String result) {
		this.locationInterface.onLocationLoaded(result);
	}

}
