package com.example.m45.moviesbrowser;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by m45 on 8/3/16.
 */
public class ThumbnailsAdapter extends ArrayAdapter {

    private final Activity context;
    private final ArrayList<String> posters;

    public ThumbnailsAdapter(Activity context, ArrayList<String> posters) {
        super(context, R.layout.listview_thumbnails, posters);

        this.context = context;
        this.posters = posters;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View rowView = layoutInflater.inflate(R.layout.listview_thumbnails, null, true);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);

        Picasso.with(context).load(posters.get(position)).into(imageView);


        return rowView;
    }
}
