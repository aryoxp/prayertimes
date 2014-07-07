package ap.mobile.prayertimes.adapters;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ap.mobile.prayertimes.R;
import ap.mobile.prayertimes.base.PrayerPreference;

public class PrayerPreferenceAdapter extends BaseAdapter {

	private ArrayList<PrayerPreference> prayerPreferenceList;
	private Context context;
	private int selectedIndex;
	
	public PrayerPreferenceAdapter(Context context, ArrayList<PrayerPreference> prayerPreferenceList, int index) {
		this.prayerPreferenceList = prayerPreferenceList;
		this.context = context;
		this.selectedIndex = index;
	}
	
	@Override
	public int getCount() {
		return this.prayerPreferenceList.size();
	}

	@Override
	public PrayerPreference getItem(int position) {
		return this.prayerPreferenceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		public TextView label;
		public ImageView tick;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		PrayerPreference pref = this.prayerPreferenceList.get(position);
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_list_preference, parent, false);
			TextView label = (TextView) convertView.findViewById(R.id.listItemLabel);
			ImageView tick = (ImageView) convertView.findViewById(R.id.listItemTick);
			ViewHolder vh = new ViewHolder();
			vh.label = label;
			vh.tick = tick;
			convertView.setTag(vh);
		}
		
		ViewHolder vh = (ViewHolder) convertView.getTag();
		vh.label.setText(pref.entry);
		if(position != this.selectedIndex)
			vh.tick.setVisibility(View.INVISIBLE);
		else vh.tick.setVisibility(View.VISIBLE);
		return convertView;
	}

}
