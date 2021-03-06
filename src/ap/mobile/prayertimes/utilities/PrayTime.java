//--------------------- Copyright Block ----------------------
/* 

PrayTime.java: Prayer Times Calculator (ver 1.0)
Copyright (C) 2007-2010 PrayTimes.org

Modified Java Code for Android by: Aryo Pinandito
Java Code By: Hussain Ali Khan
Original JS Code By: Hamid Zarrabi-Zadeh

License: GNU LGPL v3.0

TERMS OF USE:
	Permission is granted to use this code, with or 
	without modification, in any website or application 
	provided that credit is given to the original work 
	with a link back to PrayTimes.org.

This program is distributed in the hope that it will 
be useful, but WITHOUT ANY WARRANTY. 

PLEASE DO NOT REMOVE THIS COPYRIGHT BLOCK.

*/

package ap.mobile.prayertimes.utilities;


import java.util.ArrayList;
import java.util.Calendar;

import android.util.SparseArray;
import ap.mobile.prayertimes.base.Prayer;

public class PrayTime {

    // ---------------------- Global Variables --------------------
    private int calcMethod; // caculation method
    private int asrJuristic; // Juristic method for Asr
    private int dhuhrMinutes; // minutes after mid-day for Dhuhr
    private int adjustHighLats; // adjusting method for higher latitudes
    private int timeFormat; // time format
    
    private double lat; // latitude
    private double lng; // longitude
    private double timeZone; // time-zone
    private double JDate; // Julian date

    // ------------------------------------------------------------
    // Calculation Methods
    public static final int Jafari = 0; // Ithna Ashari
    public static final int Karachi = 1; // University of Islamic Sciences, Karachi
    public static final int ISNA = 2; // Islamic Society of North America (ISNA)
    public static final int MWL = 3; // Muslim World League (MWL)
    public static final int Makkah = 4; // Umm al-Qura, Makkah
    public static final int Egypt = 5; // Egyptian General Authority of Survey
    public static final int Tehran = 6; // Institute of Geophysics, University of Tehran
    public static final int Custom = 7; // Custom Setting
    
    // Juristic Methods
    public static final int Shafii = 0; // Shafii (standard)
    public static final int Hanafi = 1; // Hanafi
    
    // Adjusting Methods for Higher Latitudes
    public static final int None = 0; // No adjustment
    public static final int MidNight = 1; // middle of night
    public static final int OneSeventh = 2; // 1/7th of night
    public static final int AngleBased = 3; // angle/60th of night
    
    // Time Formats
    public static final int Time24 = 0; // 24-hour format
    public static final int Time12 = 1; // 12-hour format
    public static final int Time12NS = 2; // 12-hour format with no suffix
    public static final int Floating = 3; // floating point number
    
    // Time Names
    private ArrayList<String> timeNames;

    // --------------------- Technical Settings --------------------
    private int numIterations = 1; // number of iterations needed to compute times
    
    // ------------------- Calc Method Parameters --------------------
    private SparseArray<double[]> methodParams;

    /*
     * this.methodParams[methodNum] = new Array(fa, ms, mv, is, iv);
     *
     * fa : fajr angle ms : maghrib selector (0 = angle; 1 = minutes after
     * sunset) mv : maghrib parameter value (in angle or minutes) is : isha
     * selector (0 = angle; 1 = minutes after maghrib) iv : isha parameter value
     * (in angle or minutes)
     */
    //private double[] prayerTimesCurrent;

    // Tuning offsets {fajr, sunrise, dhuhr, asr, sunset, maghrib, isha}
    private int[] offsets = {0,0,0,0,0,0,0};

    private ArrayList<Prayer> prayerList = new ArrayList<Prayer>();
    
    public PrayTime(int calculationMethod, int asrJuristic, int timeFormat, int adjustHighLats) {
    	this.calcMethod = calculationMethod;
    	this.asrJuristic = asrJuristic;
    	this.timeFormat = timeFormat;
    	this.adjustHighLats = adjustHighLats;
    	this.dhuhrMinutes = 0;
    	init();
    }
    
	public PrayTime() {
        // Initialize vars
		this.calcMethod = PrayTime.Egypt;
		this.asrJuristic = PrayTime.Shafii;
		this.dhuhrMinutes = 0;
		this.adjustHighLats = PrayTime.MidNight;
		this.timeFormat = PrayTime.Time12;
        
        init();
    }

	private void init() {
		// Time Names
        timeNames = new ArrayList<String>();
        timeNames.add("Subuh");
        timeNames.add("Sunrise");
        timeNames.add("Dzuhur");
        timeNames.add("Ashar");
        timeNames.add("Sunset");
        timeNames.add("Maghrib");
        timeNames.add("Isya");

        // ------------------- Calc Method Parameters --------------------
        // Tuning offsets {fajr, sunrise, dhuhr, asr, sunset, maghrib, isha}
        offsets = new int[] { 0,0,0,0,0,0,0 };

        /*
         *
         * fa : fajr angle ms : maghrib selector (0 = angle; 1 = minutes after
         * sunset) mv : maghrib parameter value (in angle or minutes) is : isha
         * selector (0 = angle; 1 = minutes after maghrib) iv : isha parameter
         * value (in angle or minutes)
         */
        methodParams = new SparseArray<double[]>();
        methodParams.put(PrayTime.Jafari, 	new double[]{16,0,4,0,14});
        methodParams.put(PrayTime.Karachi, 	new double[]{18,1,0,0,18});
        methodParams.put(PrayTime.ISNA, 	new double[]{15,1,0,0,15});
        methodParams.put(PrayTime.MWL, 		new double[]{18,1,0,0,17});
        methodParams.put(PrayTime.Makkah, 	new double[]{18.5,1,0,1,90});
        methodParams.put(PrayTime.Egypt, 	new double[]{19.5,1,0,0,17.5});
        methodParams.put(PrayTime.Tehran, 	new double[]{17.7,0,4.5,0,14});
        methodParams.put(PrayTime.Custom, 	new double[]{18,1,0,0,17});
	}

    

    // ---------------------- Julian Date Functions -----------------------
    // convert Gregorian date to Julian day
	// Ref: Astronomical Algorithms by Jean Meeus
    private double julian(int year, int month, int day) {
        if (month <= 2) {
            year -= 1;
            month += 12;
        }
        double A = Math.floor(year / 100.0);
        double B = 2 - A + Math.floor(A / 4.0);
        double JD = Math.floor(365.25 * (year + 4716))
                + Math.floor(30.6001 * (month + 1)) + day + B - 1524.5;
        return JD;
    }

    // convert a calendar date to julian date (second method)
    /*
    private double calcJD(int year, int month, int day) {
        double J1970 = 2440588.0;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, day); //year is as expected, month is zero based, date is as expected
        Date date = cal.getTime();
        double ms = date.getTime(); // # of milliseconds since midnight Jan 1, 1970
        double days = Math.floor(ms / (1000.0 * 60.0 * 60.0 * 24.0));
        return J1970 + days - 0.5;
    }
	*/
    
    // ---------------------- Calculation Functions -----------------------
    // References:
    // http://www.ummah.net/astronomy/saltime
    // http://aa.usno.navy.mil/faq/docs/SunApprox.html
    // compute declination angle of sun and equation of time
    private double[] sunPosition(double jd) {

        double D = jd - 2451545;
        double g = PrayTimeMathHelper.fixangle(357.529 + 0.98560028 * D);
        double q = PrayTimeMathHelper.fixangle(280.459 + 0.98564736 * D);
        double L = PrayTimeMathHelper.fixangle(q + (1.915 * PrayTimeMathHelper.dsin(g)) + (0.020 * PrayTimeMathHelper.dsin(2 * g)));
        // double R = 1.00014 - 0.01671* dcos(g) - 0.00014* dcos(2*g);
        double e = 23.439 - (0.00000036 * D);
        double d = PrayTimeMathHelper.darcsin(PrayTimeMathHelper.dsin(e) * PrayTimeMathHelper.dsin(L));
        double RA = (PrayTimeMathHelper.darctan2((PrayTimeMathHelper.dcos(e) * PrayTimeMathHelper.dsin(L)), (PrayTimeMathHelper.dcos(L))))/ 15.0;
        RA = PrayTimeMathHelper.fixhour(RA);
        double EqT = q/15.0 - RA;
        double[] sPosition = new double[2];
        sPosition[0] = d;
        sPosition[1] = EqT;

        return sPosition;
    }

    // compute equation of time
    private double equationOfTime(double jd) {
        double eq = sunPosition(jd)[1];
        return eq;
    }

    // compute declination angle of sun
    private double sunDeclination(double jd) {
        double d = sunPosition(jd)[0];
        return d;
    }

    // compute mid-day (Dhuhr, Zawal) time
    private double computeMidDay(double t) {
        double T = equationOfTime(this.getJDate() + t);
        double Z = PrayTimeMathHelper.fixhour(12 - T);
        return Z;
    }

    // compute time for a given angle G
    private double computeTime(double G, double t) {

        double D = sunDeclination(this.getJDate() + t);
        double Z = computeMidDay(t);
        double Beg = -PrayTimeMathHelper.dsin(G) - PrayTimeMathHelper.dsin(D) * PrayTimeMathHelper.dsin(this.getLat());
        double Mid = PrayTimeMathHelper.dcos(D) * PrayTimeMathHelper.dcos(this.getLat());
        double V = PrayTimeMathHelper.darccos(Beg/Mid)/15.0;
        return Z + (G > 90 ? -V : V);
    }

    // compute the time of Asr
    // Shafii: step=1, Hanafi: step=2
    private double computeAsr(double step, double t) {
        double D = sunDeclination(this.getJDate() + t);
        double G = -PrayTimeMathHelper.darccot(step + PrayTimeMathHelper.dtan(Math.abs(this.getLat() - D)));
        return computeTime(G, t);
    }

    // ---------------------- Misc Functions -----------------------
    // compute the difference between two times
    private double timeDiff(double time1, double time2) {
        return PrayTimeMathHelper.fixhour(time2 - time1);
    }

    // -------------------- Interface Functions --------------------
    
    public ArrayList<Prayer> getPrayerList(Calendar date, double latitude,
            double longitude, double tZone) {

        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DATE);

        this.lat = latitude;
        this.lng = longitude;
        this.timeZone = tZone;
        this.JDate = julian(year, month, day);
        double lonDiff = longitude / (15.0 * 24.0);
        this.JDate = this.JDate - lonDiff;
        
        double[] times = {5, 6, 12, 13, 18, 18, 18}; // default times

        for (int i = 1; i <= this.numIterations; i++) {
            times = computeTimes(times);
        }
        
        times = adjustTimes(times);
        times = tuneTimes(times);
        
        this.prayerList = new ArrayList<Prayer>();
    	
    	for(int x=0; x<times.length; x++) {
    		Prayer p = new Prayer(this.timeNames.get(x), times[x]);
    		if(x == 0)
    			p.setId(Prayer.SUBUH);
    		else if(x == 2) p.setId(Prayer.DZUHUR);
    		else if(x == 3) p.setId(Prayer.ASHAR);
    		else if(x == 5) p.setId(Prayer.MAGHRIB);
    		else if(x == 6) p.setId(Prayer.ISYA);
    		this.prayerList.add(p);
    	}
        
        return this.prayerList;
    }

    // set custom values for calculation parameters
    private void setCustomParams(double[] params) {

        for (int i = 0; i < 5; i++) {
            if (params[i] == -1) {
                params[i] = methodParams.get(this.getCalcMethod())[i];
                methodParams.put(PrayTime.Custom, params);
            } else {
                methodParams.get(PrayTime.Custom)[i] = params[i];
            }
        }
        this.setCalcMethod(PrayTime.Custom);
    }

    // set the angle for calculating Fajr
    public void setFajrAngle(double angle) {
        double[] params = {angle, -1, -1, -1, -1};
        setCustomParams(params);
    }

    // set the angle for calculating Maghrib
    public void setMaghribAngle(double angle) {
        double[] params = {-1, 0, angle, -1, -1};
        setCustomParams(params);

    }

    // set the angle for calculating Isha
    public void setIshaAngle(double angle) {
        double[] params = {-1, -1, -1, 0, angle};
        setCustomParams(params);

    }

    // set the minutes after Sunset for calculating Maghrib
    public void setMaghribMinutes(double minutes) {
        double[] params = {-1, 1, minutes, -1, -1};
        setCustomParams(params);

    }

    // set the minutes after Maghrib for calculating Isha
    public void setIshaMinutes(double minutes) {
        double[] params = {-1, -1, -1, 1, minutes};
        setCustomParams(params);

    }

    

    

    // ---------------------- Compute Prayer Times -----------------------
    // compute prayer times at given julian date
    private double[] computeTimes(double[] times) {

        double[] t = dayPortion(times);

        double Fajr = this.computeTime(180 - methodParams.get(this.calcMethod)[0], t[0]);
        double Sunrise = this.computeTime(180 - 0.833, t[1]);
        double Dhuhr = this.computeMidDay(t[2]);
        double Asr = this.computeAsr(1 + this.asrJuristic, t[3]);
        double Sunset = this.computeTime(0.833, t[4]);
        double Maghrib = this.computeTime(methodParams.get(this.calcMethod)[2], t[5]);
        double Isha = this.computeTime(methodParams.get(this.calcMethod)[4], t[6]);

        double[] CTimes = {Fajr, Sunrise, Dhuhr, Asr, Sunset, Maghrib, Isha};
        
        return CTimes;

    }

    // adjust times in a prayer time array
    private double[] adjustTimes(double[] times) {
        for (int i = 0; i < times.length; i++) {
            times[i] += this.getTimeZone() - this.getLng() / 15;
        }
         
        times[2] += this.getDhuhrMinutes() / 60; // Dhuhr
        if (methodParams.get(this.getCalcMethod())[1] == 1) // Maghrib
        {
            times[5] = times[4] + methodParams.get(this.getCalcMethod())[2] / 60;
        }
        if (methodParams.get(this.getCalcMethod())[3] == 1) // Isha
        {
            times[6] = times[5] + methodParams.get(this.getCalcMethod())[4] / 60;
        }
        
        if (this.getAdjustHighLats() != PrayTime.None) {
            times = adjustHighLatTimes(times);
        }
        
        return times;
    }

    // adjust Fajr, Isha and Maghrib for locations in higher latitudes
    private double[] adjustHighLatTimes(double[] times) {
        double nightTime = timeDiff(times[4], times[1]); // sunset to sunrise
        
        // Adjust Fajr
        double FajrDiff = nightPortion(methodParams.get(this.getCalcMethod())[0]) * nightTime;
        
        if (Double.isNaN(times[0]) || timeDiff(times[0], times[1]) > FajrDiff) {
            times[0] = times[1] - FajrDiff;
        }

        // Adjust Isha
        double IshaAngle = (methodParams.get(this.getCalcMethod())[3] == 0) ? methodParams.get(this.getCalcMethod())[4] : 18;
        double IshaDiff = this.nightPortion(IshaAngle) * nightTime;
        if (Double.isNaN(times[6]) || this.timeDiff(times[4], times[6]) > IshaDiff) {
            times[6] = times[4] + IshaDiff;
        }

        // Adjust Maghrib
        double MaghribAngle = (methodParams.get(this.getCalcMethod())[1] == 0) ? methodParams.get(this.getCalcMethod())[2] : 4;
        double MaghribDiff = nightPortion(MaghribAngle) * nightTime;
        if (Double.isNaN(times[5]) || this.timeDiff(times[4], times[5]) > MaghribDiff) {
            times[5] = times[4] + MaghribDiff;
        }
        
        return times;
    }

    // the night portion used for adjusting times in higher latitudes
    private double nightPortion(double angle) {
       double calc = 0;

	if (adjustHighLats == AngleBased)
		calc = (angle)/60.0;
	else if (adjustHighLats == MidNight)
		calc = 0.5;
	else if (adjustHighLats == OneSeventh)
		calc = 0.14286;

	return calc;
    }

    // convert hours to day portions
    private double[] dayPortion(double[] times) {
        for (int i = 0; i < 7; i++) {
            times[i] /= 24;
        }
        return times;
    }

    // Tune timings for adjustments
    // Set time offsets
    public void tune(int[] offsetTimes) {

        for (int i = 0; i < offsetTimes.length; i++) { // offsetTimes length
            // should be 7 in order
            // of Fajr, Sunrise,
            // Dhuhr, Asr, Sunset,
            // Maghrib, Isha
            this.offsets[i] = offsetTimes[i];
        }
    }

    private double[] tuneTimes(double[] times) {
        for (int i = 0; i < times.length; i++) {
            times[i] = times[i] + this.offsets[i] / 60.0;
        }
        return times;
    }

    /**
     * @param args
     */
    /*
    public static void main(String[] args) {
    	// -7.952280, 112.608851
        double latitude = -37.823689;
        double longitude = 145.121597;
        double timezone = 10;
        // Test Prayer times here
        PrayTime prayers = new PrayTime();

        prayers.setTimeFormat(PrayTime.Time12);
        prayers.setCalcMethod(PrayTime.Jafari);
        prayers.setAsrJuristic(PrayTime.Shafii);
        prayers.setAdjustHighLats(PrayTime.AngleBased);
        
        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal, latitude, longitude, timezone);
        ArrayList<String> prayerNames = prayers.getTimeNames();

        for (int i = 0; i < prayerTimes.size(); i++) {
            System.out.println(prayerNames.get(i) + " - " + prayerTimes.get(i));
        }

    }
    */

    public int getCalcMethod() {
        return calcMethod;
    }

    public void setCalcMethod(int calcMethod) {
        this.calcMethod = calcMethod;
    }

    public int getAsrJuristic() {
        return asrJuristic;
    }

    public void setAsrJuristic(int asrJuristic) {
        this.asrJuristic = asrJuristic;
    }

    public int getDhuhrMinutes() {
        return dhuhrMinutes;
    }

    public void setDhuhrMinutes(int dhuhrMinutes) {
        this.dhuhrMinutes = dhuhrMinutes;
    }

    public int getAdjustHighLats() {
        return adjustHighLats;
    }

    public void setAdjustHighLats(int adjustHighLats) {
        this.adjustHighLats = adjustHighLats;
    }

    public int getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(int timeFormat) {
        this.timeFormat = timeFormat;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(double timeZone) {
        this.timeZone = timeZone;
    }

    public double getJDate() {
        return JDate;
    }

    public void setJDate(double jDate) {
        JDate = jDate;
    }
    
    public ArrayList<String> getTimeNames() {
        return timeNames;
    }
}
