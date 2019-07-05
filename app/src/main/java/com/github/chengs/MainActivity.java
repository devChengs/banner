package com.github.chengs;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.github.chengs.banner.Banner;
import com.github.chengs.banner.BaseBannerAdapter;
import com.github.chengs.banner.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BaseBannerAdapter.OnItemChildClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Banner recyclerBanner =  findViewById(R.id.recycler);
//        Banner bannerVertical =  findViewById(R.id.recycler_ver);

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


    class Adapter  extends BaseBannerAdapter<String, BaseViewHolder>{

        public Adapter(int layoutResId, @Nullable List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            Glide.with(MainActivity.this).load(item).into((ImageView) helper.getView(R.id.image));
            helper.setText(R.id.txt,"getLayoutPosition="+helper.getLayoutPosition()+"         getAdapterPosition="+helper.getAdapterPosition());
        }
    }
}
