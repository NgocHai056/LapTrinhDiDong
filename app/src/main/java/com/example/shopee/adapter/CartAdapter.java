package com.example.shopee.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopee.Interface.TextViewClickListener;
import com.example.shopee.R;
import com.example.shopee.model.Cart;
import com.example.shopee.model.EventBus.CalTotalEvent;
import com.example.shopee.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    Context context;
    List<Cart> listCart;

    public CartAdapter(Context context, List<Cart> listCart) {
        this.context = context;
        this.listCart = listCart;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Cart cart = listCart.get(position);
        holder.name.setText(cart.getNameProduct());
        holder.quantity.setText(cart.getQuantity() + "");
        Glide.with(context).load(cart.getImg()).into(holder.img);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.price.setText("đ" + decimalFormat.format(cart.getPrice()));
        long total = cart.getPrice() * cart.getQuantity();
        //holder.total.setText(decimalFormat.format(total));


        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Utils.ListItemBuy.add(cart);
                    EventBus.getDefault().postSticky(new CalTotalEvent());
                }
                else {
                    for (int i = 0; i<Utils.ListItemBuy.size(); i++){
                        if (Utils.ListItemBuy.get(i).getIdProduct() == cart.getIdProduct()){
                            Utils.ListItemBuy.remove(i);
                            EventBus.getDefault().postSticky(new CalTotalEvent());
                        }
                    }
                }
            }
        });

        holder.setListener(new TextViewClickListener() {
            @Override
            public void onTextViewClick(View view, int pos, int value) {
                if(value == 1){
                    if(listCart.get(pos).getQuantity() > 1){
                        int newQuantity = listCart.get(pos).getQuantity() - 1;
                        listCart.get(pos).setQuantity(newQuantity);

                        holder.quantity.setText(listCart.get(pos).getQuantity() + "");
                        EventBus.getDefault().postSticky(new CalTotalEvent());
                    }
                    else if(listCart.get(pos).getQuantity() == 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                        //builder.setTitle("Thông báo");
                        builder.setMessage("Bạn có chắc chắn muốn xoá bỏ sản phẩm này?");
                        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int newQuantity = listCart.get(pos).getQuantity() - 1;
                                listCart.get(pos).setQuantity(newQuantity);
                                Utils.ListCart.remove(pos);
                                notifyDataSetChanged();
                                EventBus.getDefault().postSticky(new CalTotalEvent());
                            }
                        });
                        builder.show();
                    }
                } else if (value == 2) {
                    int newQuantity = listCart.get(pos).getQuantity() + 1;
                    listCart.get(pos).setQuantity(newQuantity);

                    holder.quantity.setText(listCart.get(pos).getQuantity() + "");
                    EventBus.getDefault().postSticky(new CalTotalEvent());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCart.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CheckBox checkbox;
        ImageView img;
        TextView name, price, btn_reduce, quantity, btn_increase;
        TextViewClickListener listener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            btn_reduce = itemView.findViewById(R.id.btn_reduce);
            quantity = itemView.findViewById(R.id.quantity);
            btn_increase = itemView.findViewById(R.id.btn_increase);

            //even click
            btn_increase.setOnClickListener(this);
            btn_reduce.setOnClickListener(this);
        }

        public void setListener(TextViewClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            if(view == btn_reduce){
                //value = 1: bớt sản phẩm
                listener.onTextViewClick(view, getAdapterPosition(), 1);
            }
            else if(view == btn_increase){
                //value = 2: thêm sản phẩm
                listener.onTextViewClick(view, getAdapterPosition(), 2);
            }
        }
    }
}
