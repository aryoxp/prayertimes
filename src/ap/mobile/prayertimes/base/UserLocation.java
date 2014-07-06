package ap.mobile.prayertimes.base;

public class UserLocation {
	
	public double longitude;
	public double latitude;
	public double timezone;
	
	public UserLocation(double latitude, double longitude) {
		this(latitude, longitude, 0);
	}
	
	public UserLocation(double latitude, double longitude, double timezone) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.timezone = timezone;
	}
}
