package com.github.chengs.banner.layoutmanager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import static android.support.v7.widget.RecyclerView.NO_POSITION;


public class BannerLayoutManager extends LinearLayoutManager {

    private boolean mInfinite = true;

    private int mPendingScrollPosition = NO_POSITION;

    protected float mInterval; //the mInterval of each item's mOffset

    protected float mOffset;

    private boolean mShouldReverseLayout = false;


    public BannerLayoutManager(Context context) {
        super(context);
    }

    public BannerLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public BannerLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}
