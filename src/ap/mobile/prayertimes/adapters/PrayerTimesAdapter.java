package ap.mobile.prayertimes.adapters;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Service;
import android.content.Context;
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
	private double now;
	
	public PrayerTimesAdapter(Context context, ArrayList<Prayer> prayerTimes, Calendar calendar) {
		this.context = context;
		this.prayerTimes = prayerTimes;
		now = calendar.get(Calendar.HOUR_OF_DAY);
		now += calendar.get(Calendar.MINUTE)/60;
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
		
		Prayer pBefore = null;
		Prayer p = this.prayerTimes.get(position);
		if(position > 0) {
			pBefore = this.prayerTimes.get(position-1);
		} else pBefore = this.prayerTimes.get(this.getCount()-1);
		
		ViewHolder vh = (ViewHolder) convertView.getTag();
		if(position == 0) {
			if(p.getTime() > now || pBefore.getTime() < now) {
				vh.container.setBackgroundColor(this.context.getResources().getColor(R.color.lime));
				this.nextPrayerPosition = position;
			}
		} else if(pBefore.getTime() < now && p.getTime() > now) {
			if(position != 4)
				vh.container.setBackgroundColor(this.context.getResources().getColor(R.color.lime));
			this.nextPrayerPosition = position;
		}
		
		if(position == 5 && this.nextPrayerPosition == 4)
			vh.container.setBackgroundColor(this.context.getResources().getColor(R.color.lime));
		
		vh.prayerNameText.setText(p.getName());
		vh.prayerTimeText.setText(p.toString(Prayer.FORMAT_12));
		
		
		
		return convertView;
	}

}
