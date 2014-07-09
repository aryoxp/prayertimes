package ap.mobile.prayertimes;

import android.app.DialogFragment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import ap.mobile.prayertimes.views.Compass;

public class LocationFragment extends DialogFragment implements SensorEventListener {
	
	View layout;
	View okButton;
	TextView dialogTextView;
	
	Compass compass;
	SensorManager sensorManager;
	
	private Sensor sensorMagneticField;
	private Sensor sensorAccelerometer;
	private float[] sensorMagneticValues = new float[3];
	private float[] sensorAccelerometerValues = new float[3];
	private float[] matrixR = new float[9];
	private float[] matrixI = new float[9];
	private float[] matrixValues = new float[3];
	
	private double qibla;
	private double north;
	private double millis = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
	}
	
	public void setQibla(double qibla) {
		this.qibla = qibla;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		this.layout = inflater.inflate(R.layout.fragment_qibla, container);
		this.compass = (Compass) this.layout.findViewById(R.id.qiblaCompass);
		this.sensorManager = (SensorManager) this.getActivity().getSystemService(MainActivity.SENSOR_SERVICE);
		this.sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		this.sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		return this.layout;
	}
		
	@Override
	public void onStart() {
		super.onStart();
		LayoutParams lp = getDialog().getWindow().getAttributes();
		DisplayMetrics dm = getResources().getDisplayMetrics();
		lp.width = (int) (dm.widthPixels * 0.9);
		getDialog().getWindow().setAttributes(lp);		
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
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

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
		   this.north = Math.toDegrees(matrixValues[0]);
		   this.compass.update(this.qibla, this.north);
		}
	}

}

