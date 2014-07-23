package ap.mobile.prayertimes;

import java.util.Calendar;
import java.util.Locale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ReminderReceiver extends BroadcastReceiver {

	private MediaPlayer player;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String prayerName = intent.getStringExtra("PrayerName");
		String prayerTime = intent.getStringExtra("PrayerTime");
		int prayerId = 1;//intent.getIntExtra("PrayerId", 0);
		//int prayerId = intent.getIntExtra("PrayerId", 0);
		int prayerHour = intent.getIntExtra("PrayerHour", 0);
		int prayerMinute = intent.getIntExtra("PrayerMinute", 0);
		
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		if(prayerHour == cal.get(Calendar.HOUR_OF_DAY) && prayerMinute == cal.get(Calendar.MINUTE))
		//if(true)
		{
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
			boolean useAdzan = pref.getBoolean("useAdzanPreference", false);
			boolean playSound = pref.getBoolean("reminderEnabledSoundPreference", false);
			if(useAdzan && playSound) {
				try {
					AssetFileDescriptor afd = context.getAssets().openFd("raw/alaqsa2_64_22.mp3");
				    this.player = new MediaPlayer();
				    this.player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
				    this.player.prepare();
				    this.player.start();
				} catch(Exception ex) {
					Log.d(context.getPackageName(), ex.getMessage());
				}
			}
			
			Intent notificationIntent = new Intent(context, MainActivity.class);
		    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		    PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0,
		            notificationIntent, 0);
		    
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(context)
			        .setSmallIcon(R.drawable.ic_moonstar)
			        .setContentTitle("Prayer Time Reminder")
			        .setContentIntent(appPendingIntent)
			        .setContentText(prayerTime + " The time for " + prayerName + " prayer is due now.");
		    mBuilder.setAutoCancel(true);
		    if(!useAdzan && playSound)
		    	mBuilder.setDefaults(Notification.DEFAULT_SOUND);
		    if(useAdzan && playSound) {
		    	mBuilder.setSound(Uri.parse("android.resource://ap.mobile.prayertimes/" + R.raw.alaqsa2_64_22));
		    }
		    
		    NotificationManager mNotificationManager =
		        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		    mNotificationManager.notify(prayerId, mBuilder.build());
		} else {
			Log.d(context.getPackageName(), prayerName + " alarm has passed, notification not showing.");
		}
	}

}
