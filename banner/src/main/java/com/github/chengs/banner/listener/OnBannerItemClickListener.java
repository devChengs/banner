package com.github.chengs.banner.listener;



import com.github.chengs.banner.bean.BannerInfo;

import java.util.ArrayList;

public interface OnBannerItemClickListener {
    /**
     * banner click
     *
     * @param index  subscript
     * @param banner bean
     */
    void onBannerClick(int index, ArrayList<BannerInfo> banner);
}
