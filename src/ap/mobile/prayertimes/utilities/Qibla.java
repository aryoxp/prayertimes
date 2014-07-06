package ap.mobile.prayertimes.utilities;

public class Qibla {
	public static double calculate(double lat, double lon) throws Exception {
		
		// Source: http://www.geomete.com/abdali/
		// http://www.islamicsoftware.org/qibla/qibla.html
		
		if (lat > 90 || lat<-90) {
			throw new Exception("Latitude must be between -90 and 90 degrees.");
		}
		if (lon > 180 || lon<-180) {
			throw new Exception("Longitude must be between -180 and 180 degrees");
		}
		if (Math.abs(lat-21.4)<Math.abs(0.0-0.01) && Math.abs(lon-39.8)<Math.abs(0.0-0.01)) 
			throw new Exception("The current location is in Mecca.");	//Mecca
		double phiK = 21.4*Math.PI/180.0;
		double lambdaK = 39.8*Math.PI/180.0;
		double phi = lat*Math.PI/180.0;
		double lambda = lon*Math.PI/180.0;
		double psi = 180.0/Math.PI*Math.atan2(Math.sin(lambdaK-lambda),
				Math.cos(phi)*Math.tan(phiK)-Math.sin(phi)*Math.cos(lambdaK-lambda));
		return psi;
	}
}
