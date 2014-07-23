package ap.mobile.prayertimes.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarHelper {
	
	public static String getDayMonthYearString(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		return sdf.format(calendar.getTime());
	}
	
}
