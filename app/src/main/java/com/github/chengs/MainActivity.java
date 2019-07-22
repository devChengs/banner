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
import com.github.chengs.banner.Banner;
import com.github.chengs.banner.BaseBannerAdapter;
import com.github.chengs.banner.BaseViewHolder;
import com.github.chengs.banner.listener.OnScrollListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BaseBannerAdapter.OnItemChildClickListener, OnScrollListener {


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
}
