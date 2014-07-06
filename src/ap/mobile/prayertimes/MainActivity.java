package ap.mobile.prayertimes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import ap.mobile.prayertimes.adapters.PrayerTimesAdapter;
import ap.mobile.prayertimes.base.PrayerTime;
import ap.mobile.prayertimes.base.UserLocation;
import ap.mobile.prayertimes.interfaces.CalculatePrayerTimesInterface;
import ap.mobile.prayertimes.tasks.CalculatePrayerTimesTask;
import ap.mobile.prayertimes.utilities.DateHijri;
import ap.mobile.prayertimes.utilities.LocationHelper;
import ap.mobile.prayertimes.utilities.Qibla;
import ap.mobile.prayertimes.views.Compass;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements CalculatePrayerTimesInterface, SensorEventListener {

		CalculatePrayerTimesTask calculatePrayerTimesTask;
		ListView prayerTimesListView;
		TextView cityName;
		TextView calendarGregorian;
		TextView calendarHijr;
		
		Compass compass;
		SensorManager sensorManager;
		private Sensor sensorMagneticField;
		private Sensor sensorAccelerometer;
		private float[] sensorMagneticValues = new float[3];
		private float[] sensorAccelerometerValues = new float[3];
		private float[] matrixR = new float[9];
		private float[] matrixI = new float[9];
		private float[] matrixValues = new float[3];
		
		double qibla;
		double north;
		
		public PlaceholderFragment() {
			this.calculatePrayerTimesTask = new CalculatePrayerTimesTask(this);
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
			
			sensorManager = (SensorManager) this.getActivity().getSystemService(SENSOR_SERVICE);
			sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			
	        double latitude = -7.952280;
	        double longitude = 112.608851;
	        double timezone = 7;
	        
	        Calendar calendar = Calendar.getInstance(Locale.US);
	        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
	        this.calendarGregorian.setText(sdf.format(calendar.getTime()));
	        this.calendarHijr.setText(DateHijri.convert(calendar));
	        
	        this.cityName.setText(LocationHelper.getLocation(getActivity(), latitude, longitude));
	        
			UserLocation userLocation = new UserLocation(latitude, longitude, timezone);
			this.calculatePrayerTimesTask.execute(userLocation);
			
			try {
				this.qibla = Qibla.calculate(latitude, longitude);
				Log.d("prayer qibla", String.valueOf(qibla));
			} catch (Exception e) {
				e.printStackTrace();
			}			
			return rootView;
		}
		
		@Override
		public void onResume() {
		  sensorManager.registerListener(this, sensorMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
		  sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		  super.onResume();
		}
		 
		 @Override
		public void onPause() {
		  sensorManager.unregisterListener(this, sensorMagneticField);
		  sensorManager.unregisterListener(this, sensorAccelerometer);
		  super.onPause();
		}

		@Override
		public void onCalculateComplete(ArrayList<PrayerTime> prayerTimes) {
			PrayerTimesAdapter adapter = new PrayerTimesAdapter(getActivity(), prayerTimes);
			this.prayerTimesListView.setAdapter(adapter);
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
		}

		
		private double millis = 0;
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			
			if( System.currentTimeMillis() - this.millis < 100)
				return;
			else this.millis = System.currentTimeMillis();
			
			switch(event.sensor.getType()){
			case Sensor.TYPE_MAGNETIC_FIELD:
				this.sensorMagneticValues = event.values.clone();
				break;
			case Sensor.TYPE_ACCELEROMETER:
				this.sensorAccelerometerValues = event.values.clone();
				break;
			}
			
			boolean success = SensorManager.getRotationMatrix(
		       this.matrixR,
		       this.matrixI,
		       this.sensorAccelerometerValues,
		       this.sensorMagneticValues);
				   
			if(success){
			   SensorManager.getOrientation(matrixR, matrixValues);
			    
			   //double azimuth = Math.toDegrees(matrixValues[0]);
			   //double pitch = Math.toDegrees(matrixValues[1]);
			   //double roll = Math.toDegrees(matrixValues[2]);
			    
			   //readingAzimuth.setText("Azimuth: " + String.valueOf(azimuth));
			   //readingPitch.setText("Pitch: " + String.valueOf(pitch));
			   //readingRoll.setText("Roll: " + String.valueOf(roll));
			   
			   this.north = Math.toDegrees(matrixValues[0]);
			   
			   this.compass.update(this.qibla, this.north);
			   //this.cityName.setText(String.valueOf(Math.toDegrees(matrixValues[0])));
			}
		}
	}
}
