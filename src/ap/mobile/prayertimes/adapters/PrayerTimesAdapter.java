package ap.mobile.prayertimes.adapters;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Service;
import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ap.mobile.prayertimes.R;
import ap.mobile.prayertimes.base.Prayer;

public class PrayerTimesAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Prayer> prayerTimes;
	private int displayFormat;
	
	public PrayerTimesAdapter(Context context, ArrayList<Prayer> prayerTimes, Calendar calendar) {
		this.context = context;
		this.prayerTimes = prayerTimes;
		this.displayFormat = Integer.parseInt(
				PreferenceManager.getDefaultSharedPreferences(context)
				.getString("timeFormatPreference",String.valueOf(Prayer.FORMAT_12)));
	}
	
	@Override
	public int getCount() {
		return this.prayerTimes.size();
	}

	@Override
	public Object getItem(int position) {
		return this.prayerTimes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private static class ViewHolder {
		TextView prayerNameText;
		TextView prayerTimeText;
		View container;
	}
	
	int nextPrayerPosition = 0;
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null) {
			ViewHolder vh = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_prayer_time, parent, false);
			vh.container = convertView.findViewById(R.id.itemPrayerContainer);
			vh.prayerNameText = (TextView) convertView.findViewById(R.id.itemPrayerNameText);
			vh.prayerTimeText = (TextView) convertView.findViewById(R.id.itemPrayerTimeText);
			convertView.setTag(vh);
		}
		
		Prayer p = this.prayerTimes.get(position);		
		ViewHolder vh = (ViewHolder) convertView.getTag();
		if(p.isNext())
			vh.container.setBackgroundColor(this.context.getResources().getColor(R.color.lime));
		vh.prayerNameText.setText(p.getName());
		vh.prayerTimeText.setText(p.toString(this.displayFormat));

		return convertView;
	}

}
