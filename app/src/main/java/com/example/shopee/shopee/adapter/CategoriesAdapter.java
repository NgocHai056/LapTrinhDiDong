package com.example.shopee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shopee.R;
import com.example.shopee.model.Categories;

import java.util.List;

public class CategoriesAdapter extends BaseAdapter {

    public CategoriesAdapter(Context context, int layout, List<Categories> array) {
        this.array = array;
        this.layout = layout;
        this.context = context;
    }

    public CategoriesAdapter(Context context, List<Categories> array) {
        this.array = array;
        this.context = context;
    }

    List<Categories> array;
    private int layout;
    Context context;

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class ViewHolder {
        TextView name;
        ImageView img;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null) {

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(layout, null);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)  view.findViewById(R.id.item_name);
            viewHolder.img = (ImageView) view.findViewById(R.id.item_img);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.name.setText(array.get(i).getName());
        Glide.with(context).load(array.get(i).getSrc_img()).into(viewHolder.img);

        return view;
    }
}
