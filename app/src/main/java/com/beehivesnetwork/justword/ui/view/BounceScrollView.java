package com.beehivesnetwork.justword.ui.view;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ScrollView;

/**
 * Created by davidtang on 2015-12-26.
 */
public class BounceScrollView extends NestedScrollView
{
    private static final int MAX_Y_OVER_SCROLL_DISTANCE = 50;

    private Context mContext;
    private int mMaxYOverScrollDistance;

    public BounceScrollView(Context context)
    {
        super(context);
        mContext = context;
        initBounceScrollView();
    }

    public BounceScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        initBounceScrollView();
    }

    public BounceScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mContext = context;
        initBounceScrollView();
    }

    private void initBounceScrollView()
    {
        //get the density of the screen and do some maths with it on the max overscroll distance
        //variable so that you get similar behaviors no matter what the screen size
        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float density = metrics.density;
        mMaxYOverScrollDistance = (int) (density * MAX_Y_OVER_SCROLL_DISTANCE);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent)
    {
        //This is where the magic happens, we have replaced the incoming maxOverScrollY with our own custom variable mMaxYOverscrollDistance;
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverScrollDistance, isTouchEvent);
    }

}
/**
 * I love youuuuuuu <3<3 gayau
 * missing you <3<3<3<3<3
 * Created by deer on 2015-12-27.
 */