package ap.mobile.prayertimes.utilities;

public class PrayTimeMathHelper {
	// ---------------------- Trigonometric Functions -----------------------
    // range reduce angle in degrees.
    public static double fixangle(double a) {

        a = a - (360 * (Math.floor(a / 360.0)));

        a = a < 0 ? (a + 360) : a;

        return a;
    }

    // range reduce hours to 0..23
    public static double fixhour(double a) {
        a = a - 24.0 * Math.floor(a / 24.0);
        a = a < 0 ? (a + 24) : a;
        return a;
    }

    // radian to degree
    public static double radiansToDegrees(double alpha) {
        return ((alpha * 180.0) / Math.PI);
    }

    // deree to radian
    public static double DegreesToRadians(double alpha) {
        return ((alpha * Math.PI) / 180.0);
    }

    // degree sin
    public static double dsin(double d) {
        return (Math.sin(DegreesToRadians(d)));
    }

    // degree cos
    public static double dcos(double d) {
        return (Math.cos(DegreesToRadians(d)));
    }

    // degree tan
    public static double dtan(double d) {
        return (Math.tan(DegreesToRadians(d)));
    }

    // degree arcsin
    public static double darcsin(double x) {
        double val = Math.asin(x);
        return radiansToDegrees(val);
    }

    // degree arccos
    public static double darccos(double x) {
        double val = Math.acos(x);
        return radiansToDegrees(val);
    }

    // degree arctan
    public static double darctan(double x) {
        double val = Math.atan(x);
        return radiansToDegrees(val);
    }

    // degree arctan2
    public static double darctan2(double y, double x) {
        double val = Math.atan2(y, x);
        return radiansToDegrees(val);
    }

    // degree arccot
    public static double darccot(double x) {
        double val = Math.atan2(1.0, x);
        return radiansToDegrees(val);
    }
}
