package ap.mobile.prayertimes;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class AboutFragment extends DialogFragment {
	
	View layout;
	View okButton;
	TextView dialogTextView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		this.layout = inflater.inflate(R.layout.fragment_about, container);
		return this.layout;
	}
		
	@Override
	public void onStart() {
		super.onStart();
		LayoutParams lp = getDialog().getWindow().getAttributes();
		DisplayMetrics dm = getResources().getDisplayMetrics();
		lp.width = (int) (dm.widthPixels * 0.9);
		lp.height = (int) (dm.heightPixels * 0.8);
		getDialog().getWindow().setAttributes(lp);
	}

}

