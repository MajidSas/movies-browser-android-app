package com.example.m45.moviesbrowser;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by m45 on 8/7/16.
 */
class SlidesAdapter extends PagerAdapter {

    Context mContext;
    ArrayList<String> imagesLinks;
    LayoutInflater mLayoutInflater;

    public SlidesAdapter(Context context, ArrayList<String> imagesLinks) {
        mContext = context;
        this.imagesLinks = imagesLinks;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imagesLinks.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewSlide);
        Picasso.with(mContext).load(imagesLinks.get(position)).into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}