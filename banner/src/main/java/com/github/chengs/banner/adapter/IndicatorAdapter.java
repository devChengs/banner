package com.github.chengs.banner.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;


public class IndicatorAdapter  extends RecyclerView.Adapter {

    private Context mContext;

    private int mCurrentPosition = 0;

    private int mIndicatorMargin = 0;

    private int mSize = 0;

    private Drawable mSelectedDrawable;
    private Drawable mUnSelectedDrawable;

    public IndicatorAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(Drawable selectedDrawable,Drawable unSelectedDrawable,int indicatorMargin,int size){
        mSelectedDrawable = selectedDrawable;
        mUnSelectedDrawable = unSelectedDrawable;
        mIndicatorMargin = indicatorMargin;
        mSize = size;
        notifyDataSetChanged();
    }

    public void setPosition(int currentPosition) {
        this.mCurrentPosition = currentPosition;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ImageView bannerPoint = new ImageView(mContext);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(mIndicatorMargin, mIndicatorMargin, mIndicatorMargin, mIndicatorMargin);
        bannerPoint.setLayoutParams(layoutParams);
        return new RecyclerView.ViewHolder(bannerPoint) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageView bannerPoint = (ImageView) holder.itemView;
        bannerPoint.setImageDrawable(mCurrentPosition == position ? mSelectedDrawable : mUnSelectedDrawable);

    }

    @Override
    public int getItemCount() {
        return mSize;
    }
}