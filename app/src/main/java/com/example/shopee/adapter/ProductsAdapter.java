package com.example.shopee.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopee.Interface.ItemClickListener;
import com.example.shopee.R;
import com.example.shopee.activity.DetailActivity;
import com.example.shopee.model.Products;

import java.text.DecimalFormat;
import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Products> array;
    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    public ProductsAdapter(Context context, List<Products> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_DATA) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poduct, parent, false);
            return new MyViewHolder(item);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            Products product = array.get(position);
            myViewHolder.name_pd.setText(product.getName());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            myViewHolder.price_pd.setText("đ" + decimalFormat.format(Double.parseDouble(product.getPrice())));
            Glide.with(context).load(product.getSrc_img()).into(myViewHolder.img_pd);

            myViewHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    if(!isLongClick){
                         Intent intent = new Intent(context, DetailActivity.class);
                         intent.putExtra("product", product);
                         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         context.startActivity(intent);
                    }
                }
            });
        }
        else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            //loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return array.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_DATA;
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        //ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            //progressBar = itemView.findViewById(R.id.pgb_loading);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name_pd, price_pd;
        ImageView img_pd;
        private ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name_pd = itemView.findViewById(R.id.name_pd);
            price_pd = itemView.findViewById(R.id.price_pd);
            img_pd = itemView.findViewById(R.id.img_pd);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}
