package ap.mobile.prayertimes.helpers;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import ap.mobile.prayertimes.ReminderReceiver;
import ap.mobile.prayertimes.base.Prayer;
import ap.mobile.prayertimes.base.UserLocation;
import ap.mobile.prayertimes.interfaces.CalculatePrayerTimesInterface;
import ap.mobile.prayertimes.tasks.CalculatePrayerTimesTask;

public class ReminderHelper {
	public static void setReminder(Context context, final boolean reminderEnabled, CalculatePrayerTimesInterface calculatePrayerTimesInterface) {
		final Context ctx = context.getApplicationContext();
		if(calculatePrayerTimesInterface == null)
			calculatePrayerTimesInterface = new CalculatePrayerTimesInterface() {
				
				@Override
				public void onCalculateStart() {
					
				}
				
				@Override
				public void onCalculateComplete(ArrayList<Prayer> prayerTimes) {
					ArrayList<Intent> alarmIntent = new ArrayList<Intent>();
					//int i=0;
					for(Prayer p : prayerTimes) {
						if(!p.getAlarmable()) continue;
						
						Intent intent = new Intent(ctx, ReminderReceiver.class);
						intent.putExtra("PrayerId", p.getId());
						intent.putExtra("PrayerName", p.getName());
						intent.putExtra("PrayerTime", p.getTimeString());
						intent.putExtra("PrayerHour", p.getHours());
						intent.putExtra("PrayerMinute", p.getMinutes());
						
						alarmIntent.add(intent);
						
						//boolean alarmUp = (PendingIntent.getBroadcast(getActivity().getApplicationContext(), 
						//		p.getId(), intent, PendingIntent.FLAG_NO_CREATE) != null);
						
						AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
						if(reminderEnabled) {				
							PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 
									p.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
							Calendar cal = p.getAlarmCalendar();
							Log.d(ctx.getPackageName(), 
									"Alarm set for " + p.getName() + " on " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) );
							alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, p.getAlarmCalendar().getTimeInMillis(),
							        AlarmManager.INTERVAL_DAY, pendingIntent);
							
						} else {
							int i = 0;
							for(Intent in : alarmIntent) {
								PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 
										p.getId(), in, PendingIntent.FLAG_NO_CREATE);
								alarmManager.cancel(pendingIntent);
								Log.d("alarm clear", p.getName());
								alarmIntent.remove(i);
							}
						}
					}
					if(reminderEnabled)
						Toast.makeText(ctx, "Prayer times reminder initialized", Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(ctx, "Prayer times reminder inactive", Toast.LENGTH_SHORT).show();
				}
			};
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Double latitude = Double.valueOf(prefs.getString("latitude", "0"));
		Double longitude = Double.valueOf(prefs.getString("longitude", "0"));        
        Integer timezone = Integer.valueOf(prefs.getString("timezonePreference", "0"));                        
		UserLocation userLocation = new UserLocation(latitude, longitude, timezone);
		
		CalculatePrayerTimesTask calculatePrayerTimesTask = new CalculatePrayerTimesTask(prefs, calculatePrayerTimesInterface);
		calculatePrayerTimesTask.execute(userLocation);
	}
}
