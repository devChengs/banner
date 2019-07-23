package com.github.chengs.banner.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.github.chengs.banner.R;
import com.github.chengs.banner.bean.BannerInfo;
import com.github.chengs.banner.listener.OnBannerItemClickListener;
import com.github.chengs.banner.listener.OnLoadImageViewListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * LoopAdapterWrapper
 */
public class LoopAdapterWrapper<T> extends PagerAdapter {
    private final Context context;
    private final List<T> mData;//banner data
    private final OnBannerItemClickListener onBannerItemClickListener;
    private final OnLoadImageViewListener onLoadImageViewListener;

    private Map<Integer, View> pageMap = new HashMap<>();     //records all the pages in the ViewPager
    private boolean isAnimation;

    public void setAnimation(boolean animation) {
        isAnimation = animation;
    }

    public LoopAdapterWrapper(Context context, List<T> data, OnBannerItemClickListener onBannerItemClickListener, OnLoadImageViewListener onLoadImageViewListener) {
        this.context = context;
        this.mData = data;
        this.onBannerItemClickListener = onBannerItemClickListener;
        this.onLoadImageViewListener = onLoadImageViewListener;
    }


    @Override
    public int getCount() {
        return Short.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        pageMap.remove(position);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final int index = position % mData.size();
        final BannerInfo bannerInfo = (BannerInfo) mData.get(index);
        View child = null;
        if (onLoadImageViewListener != null) {
            child = onLoadImageViewListener.createImageView(context, isAnimation);
            ImageView imageView = child.findViewById(R.id.iv_loop_banner);
            onLoadImageViewListener.onLoadImageView(imageView, bannerInfo.getBannerImage());
            container.addView(child);
            container.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onBannerItemClickListener != null)
                        onBannerItemClickListener.onBannerClick(index, (ArrayList<BannerInfo>) mData);

                }
            });
        } else {
            throw new NullPointerException("LoopViewPagerLayout onLoadImageViewListener is not initialize,Be sure to initialize the onLoadImageView");
        }
        pageMap.put(position, child);
        return child;
    }


    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        pageMap.put(position, (View) object);
    }

    //获去当前VIew的方法

    public View getPrimaryItem(int position) {
        return pageMap.get(position);
    }

}