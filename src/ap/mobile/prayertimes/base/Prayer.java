package ap.mobile.prayertimes.base;

import ap.mobile.prayertimes.utilities.PrayTimeMathHelper;

public class Prayer {
	
	public static final int FORMAT_24 = 0;
	public static final int FORMAT_12 = 1;
	public static final int FORMAT_12NS = 2;
	public static final int FORMAT_FLOATING = 3;
	
	private int hours;
	private int minutes;
	private double time;
	private String name;
	
	public Prayer() {
		
	}
	
	public Prayer(String name, double time) {
		this.name = name;
		this.time = time;
		time = PrayTimeMathHelper.fixhour(time + 0.5 / 60.0); // add 0.5 minutes to round
        this.hours = (int) Math.floor(time);
        this.minutes = (int) Math.floor((time - this.hours) * 60.0);
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getHours() {
		return this.hours;
	}
	
	public int getMinutes() {
		return this.minutes;
	}
	
	public double getTime() {
		return this.time;
	}
	
	public String toString(int format) {
		switch(format) {
		case Prayer.FORMAT_24:
			return this.floatToTime24();
		case Prayer.FORMAT_12:
			return this.floatToTime12(false);
		case Prayer.FORMAT_12NS:
			return this.floatToTime12NS();
		case Prayer.FORMAT_FLOATING:
			return String.valueOf(this.time);
		}
		return null;
	}
	
	// convert double hours to 24h format
    private String floatToTime24() {

        String result = "";

        if ((hours >= 0 && hours <= 9) && (minutes >= 0 && minutes <= 9)) {
            result = "0" + hours + ":0" + Math.round(minutes);
        } else if ((hours >= 0 && hours <= 9)) {
            result = "0" + hours + ":" + Math.round(minutes);
        } else if ((minutes >= 0 && minutes <= 9)) {
            result = hours + ":0" + Math.round(minutes);
        } else {
            result = hours + ":" + Math.round(minutes);
        }
        return result;
    }
    
    // convert double hours to 12h format
    private String floatToTime12(boolean noSuffix) {

        String suffix, result;
        if (hours >= 12) suffix = "pm";
        else suffix = "am";

        hours = ((((hours + 12) - 1) % 12) + 1);

        if (noSuffix == false) {
            if ((hours >= 0 && hours <= 9) && (minutes >= 0 && minutes <= 9)) {
                result = "0" + hours + ":0" + Math.round(minutes) + " "
                        + suffix;
            } else if ((hours >= 0 && hours <= 9)) {
                result = "0" + hours + ":" + Math.round(minutes) + " " + suffix;
            } else if ((minutes >= 0 && minutes <= 9)) {
                result = hours + ":0" + Math.round(minutes) + " " + suffix;
            } else {
                result = hours + ":" + Math.round(minutes) + " " + suffix;
            }

        } else {
            if ((hours >= 0 && hours <= 9) && (minutes >= 0 && minutes <= 9)) {
                result = "0" + hours + ":0" + Math.round(minutes);
            } else if ((hours >= 0 && hours <= 9)) {
                result = "0" + hours + ":" + Math.round(minutes);
            } else if ((minutes >= 0 && minutes <= 9)) {
                result = hours + ":0" + Math.round(minutes);
            } else {
                result = hours + ":" + Math.round(minutes);
            }
        }
        return result;

    }

    // convert double hours to 12h format with no suffix
    private String floatToTime12NS() {
        return floatToTime12(true);
    }
    
    public static String friendlyTime(double time) {
    	int hours = (int) Math.floor(time);
    	int minutes = (int) Math.ceil((time-hours) * 60);
    	String timeLeft = "";
    	if(hours > 0)
    		timeLeft = hours + " hour" + (hours>1?"s ":" ");
    	timeLeft += minutes + " minute" + (minutes>1?"s":"");
    	return timeLeft;
    }
}
