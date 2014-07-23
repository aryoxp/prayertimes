package ap.mobile.prayertimes.base;

import java.util.Calendar;
import java.util.Locale;

import ap.mobile.prayertimes.utilities.PrayTimeMathHelper;

public class Prayer {
	
	public static final int FORMAT_24 = 0;
	public static final int FORMAT_12 = 1;
	public static final int FORMAT_12NS = 2;
	public static final int FORMAT_FLOATING = 3;
	
	public static final int SUBUH = 10;
	public static final int DZUHUR = 11;
	public static final int ASHAR = 12;
	public static final int MAGHRIB = 13;
	public static final int ISYA = 14;
	
	private int hours;
	private int hours24;
	private int minutes;
	private double time;
	private String name;
	private int id;
	
	private boolean alarmable = false;
	private boolean isNext = false;
	
	public Prayer() {
		
	}
	
	public Prayer(String name, double time) {
		this.name = name;
		this.time = time;
		time = PrayTimeMathHelper.fixhour(time + 0.5 / 60.0); // add 0.5 minutes to round
        this.hours = (int) Math.floor(time);
        this.hours24 = (int) Math.floor(time);
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
	
	
	public void setId(int id) {
		this.id = id;
		this.alarmable = true;
	}
	
	public int getId() {
		return this.id;
	}
	
	public boolean getAlarmable() {
		return this.alarmable;
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
        int hours = this.hours;
        int minutes = this.minutes;
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

    	int hours = this.hours;
        int minutes = this.minutes;
        
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
    		timeLeft = hours + " hour" + (hours > 1?"s ":" ");
    	timeLeft += minutes + " minute" + (minutes > 1?"s":"");
    	return timeLeft;
    }
    
    public Calendar getAlarmCalendar() {
    	Calendar calendar = Calendar.getInstance(Locale.getDefault());
    	calendar.set(Calendar.HOUR_OF_DAY, this.hours24);
    	calendar.set(Calendar.MINUTE, this.minutes);
    	calendar.set(Calendar.SECOND, 0);
    	return calendar;
    }
    
    public String getTimeString() {
    	return this.floatToTime24();
    }

	public boolean isNext() {
		return isNext;
	}

	public void setNext(boolean isNext) {
		this.isNext = isNext;
	}
}
