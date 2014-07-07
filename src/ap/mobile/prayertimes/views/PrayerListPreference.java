package ap.mobile.prayertimes.views;

import java.util.ArrayList;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;
import ap.mobile.prayertimes.adapters.PrayerPreferenceAdapter;
import ap.mobile.prayertimes.base.PrayerPreference;

public class PrayerListPreference extends ListPreference {

	Context context;
	
	public PrayerListPreference(Context context) {
		this(context, null);
		this.context = context;
	}
	
	public PrayerListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {

		int index = this.findIndexOfValue(this.getSharedPreferences().getString(getKey(), "0"));
		
		CharSequence[] entries = this.getEntries();
		CharSequence[] values = this.getEntryValues();
		ArrayList<PrayerPreference> prayerPreferenceList = new ArrayList<PrayerPreference>();

		int i = 0;
		for(CharSequence s:entries) {
			prayerPreferenceList.add(new PrayerPreference(s, values[i]));
			i++;
		}
		
		PrayerPreferenceAdapter adapter = new PrayerPreferenceAdapter(this.context, prayerPreferenceList, index);
		builder.setAdapter(adapter, this);
		super.onPrepareDialogBuilder(builder);
		
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick(dialog, which);
	}
	
}
