package ap.mobile.prayertimes.utilities;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

public class LocationHelper {
	public static String getLocation(Context context, double lat, double lng) {
		try {
			Geocoder gcd = new Geocoder(context, Locale.getDefault());
			List<Address> addresses;
			addresses = gcd.getFromLocation(lat, lng, 1);
			if (addresses.size() > 0) {
				if(addresses.get(0).getLocality() != null) 
					return addresses.get(0).getLocality();
				return addresses.get(0).getFeatureName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
