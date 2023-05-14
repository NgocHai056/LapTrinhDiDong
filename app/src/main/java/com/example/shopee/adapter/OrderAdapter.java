package com.example.shopee.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopee.R;
import com.example.shopee.activity.MyOrderActivity;
import com.example.shopee.activity.UserActivity;
import com.example.shopee.model.EventBus.CalTotalEvent;
import com.example.shopee.model.Order;
import com.example.shopee.retrofit.ApiShopee;
import com.example.shopee.retrofit.RetrofitClient;
import com.example.shopee.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    Context context;
    List<Order> listOrder;
    ApiShopee apiShopee;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    int typeOrder;

    public OrderAdapter(Context context, List<Order> listOrder, int typeOrder) {
        this.context = context;
        this.listOrder = listOrder;
        this.typeOrder = typeOrder;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = listOrder.get(position);
        holder.fullName.setText(order.getFullName());
        holder.phoneNumber.setText(order.getPhoneNumber());
        holder.address.setText(order.getAddress());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.total.setText("đ" + decimalFormat.format(Double.parseDouble(order.getTotal())));

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.rcv_itemorder.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(order.getItem().size());
        //adapter order detail
        OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(order.getItem(), context.getApplicationContext());
        holder.rcv_itemorder.setLayoutManager(layoutManager);
        holder.rcv_itemorder.setAdapter(orderDetailAdapter);
        holder.rcv_itemorder.setRecycledViewPool(viewPool);

        if(typeOrder == 1) {
            holder.btn_order.setText("Huỷ đơn");
            holder.txt_line.setVisibility(View.VISIBLE);

            holder.btn_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                    //builder.setTitle("Thông báo");
                    builder.setMessage("Bạn có chắc chắn muốn huỷ đơn hàng này?");
                    builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int idRemove = order.getId();
                            compositeDisposable.add(apiShopee.deleteOrder(order.getId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            userModel -> {
                                                if(userModel.isSuccess()) {
                                                    Toast.makeText(view.getContext(), "Đã huỷ đơn hàng", Toast.LENGTH_LONG).show();
                                                }
                                                else {
                                                    Toast.makeText(view.getContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            },
                                            throwable -> {
                                                Toast.makeText(view.getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                    ));
                            Intent myOrderIntent = new Intent(context, MyOrderActivity.class);
                            myOrderIntent.putExtra("typeOrder", 1);
                            myOrderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(myOrderIntent);
                        }
                    });
                    builder.show();
                }
            });

        }
        else if (typeOrder == 2) {
            holder.btn_order.setText("Đã nhận");
            holder.txt_line.setVisibility(View.VISIBLE);

            holder.btn_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                    //builder.setTitle("Thông báo");
                    builder.setMessage("Bạn đã nhận được đơn hàng?");
                    builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            compositeDisposable.add(apiShopee.receivedOrder(order.getId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            userModel -> {
                                                if(userModel.isSuccess()) {
                                                    Toast.makeText(view.getContext(), "Xác nhận đã nhận đơn hàng", Toast.LENGTH_LONG).show();
                                                }
                                                else {
                                                    Toast.makeText(view.getContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            },
                                            throwable -> {
                                                Toast.makeText(view.getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                    ));
                            Intent myOrderIntent = new Intent(context, MyOrderActivity.class);
                            myOrderIntent.putExtra("typeOrder", 2);
                            myOrderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(myOrderIntent);
                        }
                    });
                    builder.show();
                }
            });
        }
        else {
            holder.btn_order.setWidth(0);
            holder.btn_order.setHeight(0);
            holder.txt_line.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listOrder.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView fullName, phoneNumber, address, total, btn_order, txt_line;
        RecyclerView rcv_itemorder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Mapping();
        }

        private void Mapping() {
            apiShopee = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiShopee.class);
            fullName = itemView.findViewById(R.id.fullName);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            address = itemView.findViewById(R.id.address);
            total = itemView.findViewById(R.id.total);
            rcv_itemorder = itemView.findViewById(R.id.rcv_itemorder);
            txt_line = itemView.findViewById(R.id.txt_line);
            btn_order = itemView.findViewById(R.id.btn_order);
        }
    }
}
