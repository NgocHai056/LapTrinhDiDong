package com.example.shopee.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.adapter.OrderAdapter;
import com.example.shopee.model.OrderModel;
import com.example.shopee.retrofit.ApiShopee;
import com.example.shopee.retrofit.RetrofitClient;
import com.example.shopee.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MyOrderActivity extends AppCompatActivity {

    ApiShopee apiShopee;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    RecyclerView rcv_myorder;
    Toolbar toolbar;
    int typeOrder;
    TextView txt_emptyOrder, order_unconfimred, order_shipping, order_received;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        typeOrder = getIntent().getIntExtra("typeOrder", 1);

        Mapping();
        ActionToolBar();
        initControll();
        initView(typeOrder);
        getOrder(typeOrder);
        handleOnClickOrder();
    }

    private void initView(int typeOrder) {
        if(typeOrder == 1) {
            order_unconfimred.setTextColor(ContextCompat.getColor(this, R.color.primary));
            order_unconfimred.setBackground(ContextCompat.getDrawable(this, R.drawable.boder_bottom));

            order_shipping.setTextColor(ContextCompat.getColor(this, R.color.black));
            order_shipping.setBackground(ContextCompat.getDrawable(this, R.color.white));

            order_received.setTextColor(ContextCompat.getColor(this, R.color.black));
            order_received.setBackground(ContextCompat.getDrawable(this, R.color.white));
        } else if (typeOrder == 2) {
            order_shipping.setTextColor(ContextCompat.getColor(this, R.color.primary));
            order_shipping.setBackground(ContextCompat.getDrawable(this, R.drawable.boder_bottom));

            order_unconfimred.setTextColor(ContextCompat.getColor(this, R.color.black));
            order_unconfimred.setBackground(ContextCompat.getDrawable(this, R.color.white));

            order_received.setTextColor(ContextCompat.getColor(this, R.color.black));
            order_received.setBackground(ContextCompat.getDrawable(this, R.color.white));
        } else if (typeOrder == 3) {
            order_received.setTextColor(ContextCompat.getColor(this, R.color.primary));
            order_received.setBackground(ContextCompat.getDrawable(this, R.drawable.boder_bottom));

            order_shipping.setTextColor(ContextCompat.getColor(this, R.color.black));
            order_shipping.setBackground(ContextCompat.getDrawable(this, R.color.white));

            order_unconfimred.setTextColor(ContextCompat.getColor(this, R.color.black));
            order_unconfimred.setBackground(ContextCompat.getDrawable(this, R.color.white));
        }
    }

    private void handleOnClickOrder() {
        order_unconfimred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initView(1);
                getOrder(1);
            }
        });

        order_shipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initView(2);
                getOrder(2);
            }
        });

        order_received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initView(3);
                getOrder(3);
            }
        });
    }

    private void initControll() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rcv_myorder.setLayoutManager(layoutManager);
    }

    private void Mapping() {
        apiShopee = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiShopee.class);

        txt_emptyOrder = findViewById(R.id.txt_emptyOrder);
        toolbar = findViewById(R.id.tbar_myorder);
        rcv_myorder = findViewById(R.id.rcv_myorder);
        order_unconfimred = findViewById(R.id.order_unconfimred);
        order_shipping = findViewById(R.id.order_shipping);
        order_received = findViewById(R.id.order_received);
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() ,UserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getOrder(int typeOrder) {

        //compositeDisposable.clear();

        compositeDisposable.add(apiShopee.showOrder(Utils.UserCurent.getId(), typeOrder)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        orderModel -> {
                            if (orderModel.isSuccess()) {
                                OrderAdapter adapter = new OrderAdapter(getApplicationContext(), orderModel.getResult(), typeOrder);
                                rcv_myorder.setAdapter(adapter);
                            }
                            else {
                                rcv_myorder.setAdapter(null);
                                txt_emptyOrder.setVisibility(View.VISIBLE);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        finish();
        super.onDestroy();
    }
}