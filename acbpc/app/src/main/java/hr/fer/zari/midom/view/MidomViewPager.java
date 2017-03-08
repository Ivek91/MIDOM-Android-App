package hr.fer.zari.midom.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import hr.fer.zari.midom.MidomApplication;

public class MidomViewPager extends ViewPager {

    private boolean swipingEnabled;

    public MidomViewPager(Context context) {
        super(context);
    }

    public MidomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return swipingEnabled && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipingEnabled && super.onTouchEvent(event);
    }

    public boolean isSwipingEnabled() {
        return swipingEnabled;
    }

    public void setSwipingEnabled(boolean swipingEnabled) {
        this.swipingEnabled = swipingEnabled;
    }
}
