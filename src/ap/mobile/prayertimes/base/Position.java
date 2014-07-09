package ap.mobile.prayertimes.base;

import android.location.Location;

public class Position {

	private double longitude;
	private double latitude;
	private double altitude;
	
	public Position(double longitude, double latitude, double altitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
	}
	
	public Position(Location location) {
		this.longitude = location.getLongitude();
		this.latitude = location.getLatitude();
		this.altitude = location.getAltitude();
	}
	
	public Position(Double latitude, Double longitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = 0;
	}

	public double getLongitude() {
		return this.longitude;
	}
	
	public double getLatitude() {
		return this.latitude;
	}
	
	public double getAltitude() {
		return this.altitude;
	}
	
}

