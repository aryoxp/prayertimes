package ap.mobile.prayertimes.views;

import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import ap.mobile.prayertimes.R;

public class Compass extends FrameLayout {
	
	private View arrow_qibla;
	private View arrow_compass;
	private float n, q = 0;
	private RotateAnimation northAnimation;
	private RotateAnimation qiblaAnimation;
	
	private AnimationSet animSetNorth;
	private AnimationSet animSetQibla;
	
	public Compass(Context context) {
		this(context, null);
		
	}
	public Compass(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.inflateLayout();
	}
		 
	public Compass(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
	}

	private void inflateLayout() {
		LayoutInflater li = (LayoutInflater)getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
	    View v = li.inflate(R.layout.compass, this);
	    this.arrow_compass = v.findViewById(R.id.arrow_compass);
	    this.arrow_qibla = v.findViewById(R.id.arrow_qibla);
	}
	 
	private float currentNorthDegree = 0;
	private float currentQiblaDegree = 0;
	
	public void update(double qibla, double north){	
		
		this.n = (float) north;
		this.q = (float) qibla;
		
		this.northAnimation = new RotateAnimation(currentNorthDegree, n, 
				RotateAnimation.RELATIVE_TO_SELF, 0.5f, 
			    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		this.northAnimation.setDuration(90);
		this.northAnimation.setFillAfter(true);
		this.qiblaAnimation = new RotateAnimation(currentQiblaDegree, q,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f, 
			    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		this.qiblaAnimation.setDuration(90);
		this.qiblaAnimation.setFillAfter(true);
		
		animSetNorth = new AnimationSet(true);
		animSetNorth.setInterpolator(new DecelerateInterpolator());
		animSetNorth.setFillAfter(true);
		animSetNorth.setFillEnabled(true);
		animSetNorth.addAnimation(northAnimation);
		
		animSetQibla = new AnimationSet(true);
		animSetQibla.setInterpolator(new DecelerateInterpolator());
		animSetQibla.setFillAfter(true);
		animSetQibla.setFillEnabled(true);
		animSetQibla.addAnimation(qiblaAnimation);
		
		this.arrow_compass.startAnimation(animSetNorth);
		this.arrow_qibla.startAnimation(animSetQibla);
		
		this.currentNorthDegree = n;
		this.currentQiblaDegree = q;

	}
	  
}
