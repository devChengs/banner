package com.github.chengs.banner.listener;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Cs on 2019-07-05.
 */
public interface OnScrollListener {

    void onBannerScrolledPosition(RecyclerView recyclerView,int position);

    void onBannerScrolled(RecyclerView recyclerView, int dx, int dy);

    void onBannerScrollStateChanged(RecyclerView recyclerView,int position, int newState);
}
