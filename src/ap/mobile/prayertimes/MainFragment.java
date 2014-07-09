package ap.mobile.prayertimes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import ap.mobile.prayertimes.adapters.PrayerTimesAdapter;
import ap.mobile.prayertimes.base.Prayer;
import ap.mobile.prayertimes.base.UserLocation;
import ap.mobile.prayertimes.interfaces.CalculatePrayerTimesInterface;
import ap.mobile.prayertimes.interfaces.LocationInterface;
import ap.mobile.prayertimes.tasks.CalculatePrayerTimesTask;
import ap.mobile.prayertimes.tasks.LocationTask;
import ap.mobile.prayertimes.utilities.DateHijri;
import ap.mobile.prayertimes.utilities.Filter;
import ap.mobile.prayertimes.utilities.GPSTracker;
import ap.mobile.prayertimes.utilities.Qibla;
import ap.mobile.prayertimes.views.Compass;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements CalculatePrayerTimesInterface, LocationInterface, SensorEventListener, OnClickListener {

	CalculatePrayerTimesTask calculatePrayerTimesTask;
	ListView prayerTimesListView;
	TextView cityName;
	TextView calendarGregorian;
	TextView calendarHijr;
	TextView upcomingPrayer;
	TextView upcomingTime;
	TextView clock;
	
	Calendar calendar = Calendar.getInstance(Locale.US);
	Compass compass;
	SensorManager sensorManager;
	
	private Sensor sensorMagneticField;
	private Sensor sensorAccelerometer;
	private float[] sensorMagneticValues = new float[3];
	private float[] sensorAccelerometerValues = new float[3];
	private float[] matrixR = new float[9];
	private float[] matrixI = new float[9];
	private float[] matrixValues = new float[3];
	
	private ArrayList<Prayer> prayerTimes;
	
	double qibla;
	double north;
	
	double latitude, longitude;
	
	SharedPreferences prefs;
	private int displayFormat;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		this.displayFormat = 
				Integer.parseInt(
						this.prefs.getString("timeFormatPreference", String.valueOf(Prayer.FORMAT_12)));
		this.calculatePrayerTimesTask = new CalculatePrayerTimesTask(this.prefs, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		this.prayerTimesListView = (ListView) rootView.findViewById(R.id.mainPrayerTimesList);
		this.cityName = (TextView) rootView.findViewById(R.id.mainCityNameText);
		this.calendarGregorian = (TextView) rootView.findViewById(R.id.mainGregorianDateText);
		this.calendarHijr = (TextView) rootView.findViewById(R.id.mainHijrDateText);
		this.compass = (Compass) rootView.findViewById(R.id.mainCompass);
		this.compass.setOnClickListener(this);
		this.upcomingPrayer = (TextView) rootView.findViewById(R.id.mainUpcomingPrayer);
		this.upcomingTime = (TextView) rootView.findViewById(R.id.mainUpcomingTimeLeft);
		this.clock = (TextView) rootView.findViewById(R.id.mainClock);
		this.sensorManager = (SensorManager) this.getActivity().getSystemService(MainActivity.SENSOR_SERVICE);
		this.sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		this.sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		this.cityName.setOnClickListener(this);
        	
		return rootView;
	}

	private void calculatePrayerTimes() {
		
		this.displayFormat = 
				Integer.parseInt(
						this.prefs.getString("timeFormatPreference", String.valueOf(Prayer.FORMAT_12)));
		
		this.latitude = Double.valueOf(this.prefs.getString("latitude", "0"));
		this.longitude = Double.valueOf(this.prefs.getString("longitude", "0"));
		
		if(this.latitude == 0 && this.longitude == 0) {
		
			GPSTracker gpsTracker = new GPSTracker(getActivity());
			if(gpsTracker.canGetLocation()) {
				this.latitude = gpsTracker.getLatitude(); //-7.952280;
		        this.longitude = gpsTracker.getLongitude(); //112.608851;
			} else {
				gpsTracker.showSettingsAlert();
			}
			
		}
        
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
        SimpleDateFormat sdfTz = new SimpleDateFormat("Z", Locale.getDefault());
        
        double timezone = Math.floor(Double.parseDouble(sdfTz.format(calendar.getTime()))/100);
        
        this.calendarGregorian.setText(sdf.format(calendar.getTime()));
        this.calendarHijr.setText(DateHijri.convert(calendar));
        
        LocationTask locationTask = new LocationTask(getActivity(), latitude, longitude, this);
        locationTask.execute();
        this.cityName.setText("Loading...");
        
        if(this.prefs == null)
        	this.prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		UserLocation userLocation = new UserLocation(latitude, longitude, timezone);
		this.calculatePrayerTimesTask = new CalculatePrayerTimesTask(this.prefs, this);
		this.calculatePrayerTimesTask.execute(userLocation);
		
		try {
			this.qibla = Qibla.calculate(latitude, longitude);
			Log.d("prayer", "Lat: " + latitude + ", Lng: " + longitude + ", Qibla: " + qibla);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@Override
	public void onResume() {
		this.calculatePrayerTimes();
		this.sensorManager.registerListener(this, sensorMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
		this.sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		if(this.prayerTimes != null && this.prayerTimes.size() > 0)
		this.handler.post(upcomingPrayerRunnable);
		super.onResume();
	}
	 
	 @Override
	public void onPause() {
		this.sensorManager.unregisterListener(this, sensorMagneticField);
		this.sensorManager.unregisterListener(this, sensorAccelerometer);
		this.handler.removeCallbacks(upcomingPrayerRunnable);
		super.onPause();
	}

	@Override
	public void onCalculateComplete(ArrayList<Prayer> prayerTimes) {
		this.prayerTimes = prayerTimes;
		PrayerTimesAdapter adapter = new PrayerTimesAdapter(getActivity(), prayerTimes, calendar);
		this.prayerTimesListView.setAdapter(adapter);
		this.handler.post(upcomingPrayerRunnable);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// nothing...
	}
	
	Handler handler = new Handler();

	private Runnable upcomingPrayerRunnable = new Runnable() {		
		@Override
		public void run() {
			calendar = Calendar.getInstance(Locale.getDefault());
			int hours = calendar.get(Calendar.HOUR_OF_DAY);
			int hour = calendar.get(Calendar.HOUR);
			int minutes = calendar.get(Calendar.MINUTE);
			double time = hours + minutes/60d;
			Prayer nextPrayer = new Prayer();
			
			if(prayerTimes != null && prayerTimes.size() > 0) {
				int position = 0;
				for(Prayer p:prayerTimes) {
					if(time < p.getTime()) {
						if(position == 4)
						{
							nextPrayer = prayerTimes.get(5);
							break;
						}
						nextPrayer = p;
						break;
					}
					position++;
				}
			}
			
			double timeLeft = 0;
			if(nextPrayer.getTime() == 0) {
				nextPrayer = prayerTimes.get(0);
				timeLeft = nextPrayer.getTime() + (24-time);
			} else {
				timeLeft = nextPrayer.getTime() - time;
			}
			upcomingPrayer.setText(nextPrayer.getName());
			upcomingTime.setText(Prayer.friendlyTime(timeLeft));
			
			try {
				hour = (hour==0?12:hour);
				if(clock !=null) {
					String clockText = "";
					switch(displayFormat) {
						case Prayer.FORMAT_12:
						case Prayer.FORMAT_12NS:
							clockText += String.format("%d:%02d", hour, minutes);
							if(displayFormat == Prayer.FORMAT_12)
								clockText += " " + (calendar.get(Calendar.AM_PM) == 0?"AM":"PM");
							break;
						case Prayer.FORMAT_24:
							clockText += String.format("%02d:%02d", hours, minutes);
							break;
					}
					
					clock.setText(clockText);
					
				}
			} catch(Exception ex) {}
			
			handler.postDelayed(this, 500);
		}
	};
	
	
	//private double millis = 0;
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		//if( System.currentTimeMillis() - this.millis < 100)
		//	return;
		//else this.millis = System.currentTimeMillis();
		
		switch(event.sensor.getType()){
		case Sensor.TYPE_MAGNETIC_FIELD:
			this.sensorMagneticValues = Filter.lowPass(event.values.clone(), this.sensorMagneticValues);
			break;
		case Sensor.TYPE_ACCELEROMETER:
			this.sensorAccelerometerValues = Filter.lowPass(event.values.clone(), this.sensorAccelerometerValues);
			break;
		}
		
		boolean success = SensorManager.getRotationMatrix(
	       this.matrixR,
	       this.matrixI,
	       this.sensorAccelerometerValues,
	       this.sensorMagneticValues);
			   
		if(success){
		   SensorManager.getOrientation(matrixR, matrixValues);
		   this.north = Math.toDegrees(matrixValues[0]);
		   this.compass.update(this.qibla, this.north);
		}
	}

	@Override
	public void onLocationLoaded(String location) {
		try {
			if(location.trim().equals("")) 
				location = "Unknown Location";
		} catch(Exception ex) {
			location = "Unknown Location";
		}
		this.cityName.setText(location);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.mainCompass:
			QiblaDirectionFragment qiblaDirectionFragment = new QiblaDirectionFragment();
			qiblaDirectionFragment.setQibla(this.qibla);
			qiblaDirectionFragment.show(getFragmentManager(), "qiblaFragment");
			break;
		case R.id.mainCityNameText:
			Intent i = new Intent(getActivity(), MapsActivity.class);
			this.startActivity(i);
			break;
		}
	}
	
}