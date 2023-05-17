package com.example.shopee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopee.R;
import com.example.shopee.model.Item;

import java.text.DecimalFormat;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.MyViewHolder> {

    List<Item> listItem;
    Context context;

    public OrderDetailAdapter(List<Item> listItem, Context context) {
        this.listItem = listItem;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orderdetail, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Item item = listItem.get(position);
        Glide.with(context).load(item.getSrc_img()).into(holder.img);
        holder.name.setText(item.getName());
        holder.quantity.setText("x" + item.getQuantity());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.price.setText("đ" + decimalFormat.format(Double.parseDouble(item.getPrice())));
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img;
        TextView name, quantity, price;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Mapping();
        }

        private void Mapping() {
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
        }
    }
}
