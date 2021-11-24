package com.example.bidit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter {

    ArrayList<Item> productList = new ArrayList<Item>();

    public MyAdapter(Context context, int textViewResourceId, ArrayList objects) {
        super(context, textViewResourceId, objects);
        productList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.grid_view_items, null);
        TextView textView = (TextView) v.findViewById(R.id.txtTimer2);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        textView.setText(productList.get(position).getProductName());
        Picasso.with(getContext())
                .load(productList.get(position).getProductImageUrl())
                .noPlaceholder()
                .fit()
                .centerCrop().into(imageView);
        //imageView.setImageResource(productList.get(position).getProductImage());
        return v;

    }

}


