package ap.mobile.prayertimes;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import ap.mobile.prayertimes.utilities.GPSTracker;

public class MapsFragment extends Fragment implements 
		OnMapLoadedCallback, OnClickListener, 
		OnMarkerClickListener, OnMarkerDragListener {
	
	private View rootView, mapsButtonSave, mapsButtonReset, mapsButtonGPS, mapsButtonsContainer;
	private SharedPreferences prefs;
	private Double longitude;
	private Double latitude;
	private GoogleMap maps;
	private MarkerOptions mOptions;
	private Marker marker;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		this.rootView = inflater.inflate(R.layout.fragment_maps, container, false);
		this.mapsButtonSave = this.rootView.findViewById(R.id.mapsButtonSave);
		this.mapsButtonReset = this.rootView.findViewById(R.id.mapsButtonReset);
		this.mapsButtonGPS = this.rootView.findViewById(R.id.mapsButtonGPS);
		this.mapsButtonsContainer = this.rootView.findViewById(R.id.mapButtonsContainer);
		this.mapsButtonSave.setOnClickListener(this);
		this.mapsButtonReset.setOnClickListener(this);
		this.mapsButtonGPS.setOnClickListener(this);
		
		this.prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		SupportMapFragment fm = (SupportMapFragment) this.getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
		this.maps = fm.getMap();
		this.maps.setMyLocationEnabled(true);
		this.maps.getUiSettings().setMyLocationButtonEnabled(true);
		this.maps.getUiSettings().setCompassEnabled(true);
		this.maps.setOnMapLoadedCallback(this);
		
		return this.rootView;
	}
	
	@Override
	public void onResume() {
		this.longitude = Double.valueOf(this.prefs.getString("longitude", "0"));
		this.latitude = Double.valueOf(this.prefs.getString("latitude", "0"));
		
		if(this.longitude == 0 && this.latitude == 0) {
			GPSTracker gpsTracker = new GPSTracker(getActivity());
			if(gpsTracker.canGetLocation()) {
				this.latitude = gpsTracker.getLatitude();
				this.longitude = gpsTracker.getLongitude();
			}
		}
		if(this.prefs.getString("locationModePreference", "0").equals("0"))
			this.mapsButtonsContainer.setVisibility(View.GONE);
		else this.mapsButtonsContainer.setVisibility(View.VISIBLE);
		super.onResume();
	}

	@Override
	public void onMapLoaded() {
		this.mOptions = new MarkerOptions();
		LatLng point = new LatLng(this.latitude, this.longitude);
		this.mOptions.position(point);
		this.mOptions.draggable(true);
		this.marker = this.maps.addMarker(mOptions);
		this.marker.setTitle("My location");
		this.updateMarkerSnippet(this.marker);
		this.maps.setOnMarkerDragListener(this);
		this.maps.setOnMarkerClickListener(this);
		this.maps.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.mapsButtonReset:
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		    builder.setMessage("Do you want to move the marker to your last current location?")
		       .setTitle("Reset Marker")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   LatLng point = new LatLng(latitude, longitude);
		        	   marker.setPosition(point);
		        	   updateMarkerSnippet(marker);
		        	   marker.showInfoWindow();
		        	   maps.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		    AlertDialog alert = builder.create();
		    alert.show();
			
			
			break;
		case R.id.mapsButtonGPS:
			
			GPSTracker gpsTracker = new GPSTracker(getActivity());
     	   	final LatLng GPSPoint = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
     	   
			AlertDialog.Builder builderGPS = new AlertDialog.Builder(this.getActivity());
		    builderGPS.setMessage("Do you want to move the marker to your current location based on GPS information?")
		       .setTitle("Set Marker by GPS Location")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   
		        	   marker.setPosition(GPSPoint);
		        	   updateMarkerSnippet(marker);
		        	   marker.showInfoWindow();
		        	   maps.animateCamera(CameraUpdateFactory.newLatLngZoom(GPSPoint, 15));
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		    AlertDialog alertGPS = builderGPS.create();
		    alertGPS.show();
			
			
			break;	
		case R.id.mapsButtonSave:
			
			AlertDialog.Builder saveBuilder = new AlertDialog.Builder(this.getActivity());
			saveBuilder.setMessage("Do you want to set the marker as your current location?")
		       .setTitle("Set Marker")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	    LatLng pointSave = marker.getPosition();
		   				prefs.edit().putString("longitude", String.valueOf(pointSave.longitude)).commit();
		   				prefs.edit().putString("latitude", String.valueOf(pointSave.latitude)).commit();
		   				getActivity().finish();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		    AlertDialog saveAlert = saveBuilder.create();
		    saveAlert.show();
		    
			
			break;
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		updateMarkerSnippet(marker);
		marker.showInfoWindow();
		return false;
	}

	private void updateMarkerSnippet(Marker marker) {
		LatLng markerPosition = marker.getPosition();
		marker.setSnippet(String.format("Lat: %.3f, Lng: %.3f", markerPosition.latitude, markerPosition.longitude));
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		if(marker.isInfoWindowShown())
			this.updateMarkerSnippet(marker);
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		
	}
}
