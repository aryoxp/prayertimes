package ap.mobile.prayertimes.adapters;

import java.util.ArrayList;

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
	
	public PrayerTimesAdapter(Context context, ArrayList<Prayer> prayerTimes) {
		this.context = context;
		this.prayerTimes = prayerTimes;
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
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null) {
			ViewHolder vh = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_prayer_time, parent, false);
			vh.prayerNameText = (TextView) convertView.findViewById(R.id.itemPrayerNameText);
			vh.prayerTimeText = (TextView) convertView.findViewById(R.id.itemPrayerTimeText);
			convertView.setTag(vh);
		}
		
		ViewHolder vh = (ViewHolder) convertView.getTag();
		vh.prayerNameText.setText(this.prayerTimes.get(position).getName());
		vh.prayerTimeText.setText(this.prayerTimes.get(position).toString(Prayer.FORMAT_12));
		
		return convertView;
	}

}
