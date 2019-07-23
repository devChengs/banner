package com.github.chengs;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.github.chengs.banner.Banner;
import com.github.chengs.banner.BaseBannerAdapter;
import com.github.chengs.banner.BaseViewHolder;
import com.github.chengs.banner.bean.BannerInfo;
import com.github.chengs.banner.listener.OnBannerItemClickListener;
import com.github.chengs.banner.listener.OnDefaultImageViewLoader;
import com.github.chengs.banner.listener.OnScrollListener;
import com.github.chengs.banner.view.BannerBgContainer;
import com.github.chengs.banner.view.LoopLayout;
import com.github.chengs.banner.widget.IndicatorLocation;
import com.github.chengs.banner.widget.LoopStyle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BaseBannerAdapter.OnItemChildClickListener, OnScrollListener, OnBannerItemClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Banner recyclerBanner =  findViewById(R.id.recycler);
        recyclerBanner.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onBannerScrolledPosition(RecyclerView recyclerView, int position) {

            }

            @Override
            public void onBannerScrolled(RecyclerView recyclerView, int dx, int dy) {

            }

            @Override
            public void onBannerScrollStateChanged(RecyclerView recyclerView, int position, int newState) {

            }
        });
        List<String> list = new ArrayList<>();

        list.add("https://gratisography.com/thumbnails/gratisography-sleeping-coffee-beans-thumbnail.jpg");
        list.add("https://gratisography.com/thumbnails/gratisography-217-thumbnail-small.jpg");
        list.add("https://gratisography.com/thumbnails/gratisography-dog-waiting-patiently-thumbnail.jpg");
        list.add("https://gratisography.com/thumbnails/gratisography-spaceship-tinfoil-car-thumbnail.jpg");
        list.add("https://gratisography.com/thumbnails/gratisography-urban-bunny-stairwell-thumbnail.jpg");
        Adapter adapter = new Adapter(R.layout.item_image,list);
        adapter.setOnItemChildClickListener(this);
        recyclerBanner.setAdapter(adapter)
                .setInfinite(true).setItemSpace(80).setMinScale(0.8f).setAutoPlaying(true).start();


        initLoopBanner();
    }

    private void initLoopBanner() {
        BannerBgContainer bannerBgContainer;
        LoopLayout loopLayout;
        loopLayout = findViewById(R.id.loop_layout);
        bannerBgContainer = findViewById(R.id.banner_bg_container);
        loopLayout.setLoop_ms(3000);//轮播的速度(毫秒)
        loopLayout.setLoop_duration(400);//滑动的速率(毫秒)
        loopLayout.setScaleAnimation(true);// 设置是否需要动画
        loopLayout.setLoop_style(LoopStyle.Empty);//轮播的样式-默认empty
        loopLayout.setIndicatorLocation(IndicatorLocation.Center);//指示器位置-中Center
        loopLayout.initializeData(this);
        // 准备数据
        ArrayList<BannerData> bannerInfos = new ArrayList<>();
        List<Object> bgList = new ArrayList<>();

        bannerInfos.add(new BannerData("first","http://shop.jiujiuwu995.com/public/upload/temp/2019/07-02/9cd6076b3949e8a747f116823b1d34cb.jpg"));
        bannerInfos.add(new BannerData("second","http://shop.jiujiuwu995.com/public/upload/temp/2019/07-02/4f7ffae28b2ce6f4c895fd3cdb135de1.jpg" ));
        bannerInfos.add(new BannerData("three","http://shop.jiujiuwu995.com/public/upload/temp/2019/06-28/b9b4b378213b075a48da3927a9ec7e39.jpg" ));
        // 设置监听
        loopLayout.setOnLoadImageViewListener(new OnDefaultImageViewLoader() {
            @Override
            public void onLoadImageView(ImageView view, Object object) {
                //设置图片圆角角度
                RoundedCorners roundedCorners= new RoundedCorners(10);
                //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
                RequestOptions options= RequestOptions.bitmapTransform(roundedCorners).override(300, 300);
                Glide.with(view.getContext())
                        .load(object)
                        .apply(options)
                        .into(view);
            }
        });
        loopLayout.setOnBannerItemClickListener(this);
        if (bannerInfos.size() == 0) {
            return;
        }
        loopLayout.setLoopData(bannerInfos);
        bannerBgContainer.setBannerBackBg(this, bannerInfos);
        loopLayout.setBannerBgContainer(bannerBgContainer);
        loopLayout.startLoop();
    }

    @Override
    public void onItemChildClick(BaseBannerAdapter adapter, View view, int position) {
        Toast.makeText(this,String.format("第%d个",position),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBannerScrolledPosition(RecyclerView recyclerView, int position) {
        Log.d("chengs","position###"+position);
    }

    @Override
    public void onBannerScrolled(RecyclerView recyclerView, int dx, int dy) {
        Log.d("chengs","dx="+dx+"--dy="+dy);

    }

    @Override
    public void onBannerScrollStateChanged(RecyclerView recyclerView,int position, int newState) {
        Log.d("chengs","newState="+newState+",position---"+position);
    }

    @Override
    public void onBannerClick(int index, ArrayList<BannerInfo> banner) {

    }


    class Adapter  extends BaseBannerAdapter<String, BaseViewHolder>{

        public Adapter(int layoutResId, @Nullable List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            Glide.with(MainActivity.this).load(item).into((ImageView) helper.getView(R.id.image));
            helper.setText(R.id.txt,"getLayoutPosition="+helper.getLayoutPosition()+"         getAdapterPosition="+helper.getAdapterPosition());
            Log.d("chengs","getLayoutPosition="+helper.getLayoutPosition()+"         getAdapterPosition="+helper.getAdapterPosition());
        }
    }

    class BannerData implements BannerInfo{

        public String title;
        public Object res;

        public BannerData(String title, Object res) {
            this.title = title;
            this.res = res;
        }

        @Override
        public Object getBannerImage() {
            return res;
        }

        @Override
        public Object getBannerBackground() {
            return res;
        }

        @Override
        public String getTitle() {
            return "";
        }
    }
}
