package com.github.chengs.banner;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.github.chengs.banner.adapter.IndicatorAdapter;
import com.github.chengs.banner.layoutmanager.CarouselLayoutManager;
import com.github.chengs.banner.layoutmanager.ViewPagerLayoutManager;

/**
 * Created by Cs on 2019-05-11 .
 */
@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class Banner extends FrameLayout {

    protected RecyclerView mRecyclerView;

    private BaseBannerAdapter mAdapter;

    protected ViewPagerLayoutManager mLayoutManager;

    //指示器
    protected RecyclerView mIndicatorContainer;

    //刷新间隔时间
    protected int mAutoPlayDuration;

    //是否循环显示
    protected boolean mIsInfinite;

    //是否显示指示器
    protected boolean mIsShowIndicator;

    protected Drawable mSelectedDrawable;
    protected Drawable mUnselectedDrawable;

    protected IndicatorAdapter mIndicatorAdapter;

    protected int mIndicatorMargin;//指示器间距

    protected int mBannerSize = 1;
    protected int mCurrentIndex;
    protected boolean mIsPlaying;

    protected int mItemSpace;

    protected int mMaxVisibleItemCount;

    protected float mMinScale;

    protected float mMoveSpeed;

    protected boolean mIsAutoPlaying;

    private boolean mIsStart = false;

    protected int WHAT_AUTO_PLAY = 1000;

    public Banner(Context context) {
        super(context);
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }


    protected void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Banner);
        mIsShowIndicator = typedArray.getBoolean(R.styleable.Banner_showIndicator, BannerConfig.IS_SHOW_INDICATOR);
        mAutoPlayDuration = typedArray.getInt(R.styleable.Banner_interval, BannerConfig.TIME);
        mIsAutoPlaying = typedArray.getBoolean(R.styleable.Banner_autoPlaying, BannerConfig.IS_AUTO_PLAY);
        mIsInfinite = typedArray.getBoolean(R.styleable.Banner_infinite, BannerConfig.IS_INFINITE);
        mSelectedDrawable = typedArray.getDrawable(R.styleable.Banner_indicatorSelectedSrc);
        mUnselectedDrawable = typedArray.getDrawable(R.styleable.Banner_indicatorUnselectedSrc);
        mItemSpace = typedArray.getInt(R.styleable.Banner_itemSpace, BannerConfig.ITEM_SPACE);
        mMinScale = typedArray.getFloat(R.styleable.Banner_minScale, BannerConfig.SCALE);
        mMoveSpeed = typedArray.getFloat(R.styleable.Banner_moveSpeed, BannerConfig.DEFAULT_SPEED);
        mMaxVisibleItemCount = typedArray.getInt(R.styleable.Banner_maxVisibleItemCount, BannerConfig.DEFAULT_ITEM_COUNT);
        if (mSelectedDrawable == null) {
            //绘制默认选中状态图形
            GradientDrawable selectedGradientDrawable = new GradientDrawable();
            selectedGradientDrawable.setShape(GradientDrawable.OVAL);
            selectedGradientDrawable.setColor(Color.RED);
            selectedGradientDrawable.setSize(dp2px(5), dp2px(5));
            selectedGradientDrawable.setCornerRadius(dp2px(5) / 2);
            mSelectedDrawable = new LayerDrawable(new Drawable[]{selectedGradientDrawable});
        }
        if (mUnselectedDrawable == null) {
            //绘制默认未选中状态图形
            GradientDrawable unSelectedGradientDrawable = new GradientDrawable();
            unSelectedGradientDrawable.setShape(GradientDrawable.OVAL);
            unSelectedGradientDrawable.setColor(Color.GRAY);
            unSelectedGradientDrawable.setSize(dp2px(5), dp2px(5));
            unSelectedGradientDrawable.setCornerRadius(dp2px(5) / 2);
            mUnselectedDrawable = new LayerDrawable(new Drawable[]{unSelectedGradientDrawable});
        }

        mIndicatorMargin = typedArray.getDimensionPixelSize(R.styleable.Banner_indicatorSpace, dp2px(4));
        int marginLeft = typedArray.getDimensionPixelSize(R.styleable.Banner_indicatorMarginLeft, dp2px(16));
        int marginRight = typedArray.getDimensionPixelSize(R.styleable.Banner_indicatorMarginRight, dp2px(0));
        int marginBottom = typedArray.getDimensionPixelSize(R.styleable.Banner_indicatorMarginBottom, dp2px(11));
        int g = typedArray.getInt(R.styleable.Banner_indicatorGravity, 0);
        int gravity;
        if (g == 0) {
            gravity = GravityCompat.START;
        } else if (g == 2) {
            gravity = GravityCompat.END;
        } else {
            gravity = Gravity.CENTER;
        }
        int o = typedArray.getInt(R.styleable.Banner_orientation, 0);
        int orientation = 0;
        if (o == 0) {
            orientation = LinearLayoutManager.HORIZONTAL;
        } else if (o == 1) {
            orientation = LinearLayoutManager.VERTICAL;
        }
        typedArray.recycle();
        //recyclerView部分
        mRecyclerView = new RecyclerView(context);
        new PagerSnapHelper().attachToRecyclerView(mRecyclerView);
        mLayoutManager = getLayoutManager(context, mItemSpace, orientation);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mCurrentIndex = mLayoutManager.getCurrentPosition();
                Log.d("chengs", "-----------" + mCurrentIndex);
                refreshIndicator();
                onBannerScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mCurrentIndex = mLayoutManager.getCurrentPosition();
                refreshIndicator();
                onBannerScrollStateChanged(recyclerView, newState);
            }
        });
        LayoutParams vpLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mRecyclerView, vpLayoutParams);
        //指示器部分
        mIndicatorContainer = new RecyclerView(context);
        LinearLayoutManager indicatorLayoutManager = new LinearLayoutManager(context, orientation, false);
        mIndicatorContainer.setLayoutManager(indicatorLayoutManager);
        mIndicatorAdapter = new IndicatorAdapter(context);
        mIndicatorContainer.setAdapter(mIndicatorAdapter);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | gravity;
        params.setMargins(marginLeft, 0, marginRight, marginBottom);
        addView(mIndicatorContainer, params);
        if (!mIsShowIndicator) {
            mIndicatorContainer.setVisibility(GONE);
        }
    }

    protected Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == WHAT_AUTO_PLAY) {
                mCurrentIndex = mLayoutManager.getCurrentPosition();
                if (mCurrentIndex == mBannerSize - 1) {
                    mCurrentIndex = 0;
                    mLayoutManager.scrollToPosition(mCurrentIndex);
                } else {
                    mLayoutManager.scrollToPosition(++mCurrentIndex);
                }
                refreshIndicator();
                mHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, mAutoPlayDuration);

            }
            return false;
        }
    });


    /**
     * 设置播放间隔时间
     *
     * @param autoPlayDuration
     */
    public Banner setInterval(int autoPlayDuration) {
        mAutoPlayDuration = autoPlayDuration;
        return this;
    }

    /**
     * 设置是否无限循环
     *
     * @param isInfinite
     */
    public Banner setInfinite(boolean isInfinite) {
        this.mIsInfinite = isInfinite;
        mLayoutManager.setInfinite(isInfinite);
        return this;
    }

    /**
     * 设置Item间距
     *
     * @param itemSpace
     */
    public Banner setItemSpace(int itemSpace) {
        this.mItemSpace = itemSpace;
        if (mLayoutManager instanceof CarouselLayoutManager) {
            ((CarouselLayoutManager) mLayoutManager).setItemSpace(itemSpace);
        }
        return this;
    }

    /**
     * 设置移动速度
     *
     * @param moveSpeed
     */
    public Banner setMoveSpeed(float moveSpeed) {
        this.mMoveSpeed = moveSpeed;
        if (mLayoutManager instanceof CarouselLayoutManager) {
            ((CarouselLayoutManager) mLayoutManager).setMoveSpeed(moveSpeed);
        }
        return this;
    }

    public Banner setMaxVisibleItemCount(int maxVisibleItemCount) {
        this.mMaxVisibleItemCount = maxVisibleItemCount;
        if (mLayoutManager instanceof CarouselLayoutManager) {
            mLayoutManager.setMaxVisibleItemCount(maxVisibleItemCount);
        }
        return this;
    }

    /**
     * 设置缩放比例
     *
     * @param minScale (0~1)
     */
    public Banner setMinScale(float minScale) {
        this.mMinScale = minScale;
        if (mLayoutManager instanceof CarouselLayoutManager) {
            ((CarouselLayoutManager) mLayoutManager).setMinScale(minScale);
        }
        return this;
    }

    /**
     * 设置是否禁止滚动播放
     */
    public Banner setAutoPlaying(boolean isAutoPlaying) {
        this.mIsAutoPlaying = isAutoPlaying;
        setPlaying(this.mIsAutoPlaying);
        return this;
    }

    /**
     * 设置轮播数据集
     */
    public Banner setAdapter(BaseBannerAdapter adapter) {
        this.mAdapter = adapter;
        return this;
    }

    public void start() {
        if (mAdapter == null) {
            Log.i("banner", "RecyclerView Adapter Cannot be null");
            return;
        }
        mBannerSize = mAdapter.mData.size();
        mIndicatorAdapter.setData(mSelectedDrawable, mUnselectedDrawable, mIndicatorMargin, mBannerSize);
        mRecyclerView.setAdapter(mAdapter);
        mIsStart = true;
        setPlaying(true);
    }


    /**
     * 设置是否自动播放（上锁）
     *
     * @param mIsPlaying 开始播放
     */
    protected synchronized void setPlaying(boolean mIsPlaying) {
        if (mIsAutoPlaying && mIsStart) {
            if (!this.mIsPlaying && mIsPlaying) {
                mHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, mAutoPlayDuration);
                this.mIsPlaying = true;
            } else if (this.mIsPlaying && !mIsPlaying) {
                mHandler.removeMessages(WHAT_AUTO_PLAY);
                this.mIsPlaying = false;
            }
        }
    }


    protected void onBannerScrolled(RecyclerView recyclerView, int dx, int dy) {

    }

    protected void onBannerScrollStateChanged(RecyclerView recyclerView, int newState) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPlaying(false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setPlaying(true);
                break;
        }
        //解决recyclerView嵌套问题
        try {
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    //解决recyclerView嵌套问题
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    //解决recyclerView嵌套问题
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setPlaying(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setPlaying(false);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            setPlaying(true);
        } else {
            setPlaying(false);
        }
    }

    /**
     * 改变导航的指示点
     */
    protected synchronized void refreshIndicator() {
        if (mIsShowIndicator && mBannerSize > 1) {
            mIndicatorAdapter.setPosition(mCurrentIndex);
            mIndicatorAdapter.notifyDataSetChanged();
        }
    }

    public ViewPagerLayoutManager getLayoutManager(Context context, int itemSpace, int orientation) {
        CarouselLayoutManager manager = new CarouselLayoutManager(context, itemSpace, orientation);
        manager.setInfinite(mIsInfinite);
        manager.setMoveSpeed(mMoveSpeed);
        manager.setMinScale(mMinScale);
        manager.setItemSpace(mItemSpace);
        manager.setMaxVisibleItemCount(mMaxVisibleItemCount);
        return manager;
    }

    protected int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }
}
