package com.example.shopee.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.adapter.CartAdapter;
import com.example.shopee.model.Cart;
import com.example.shopee.model.EventBus.CalTotalEvent;
import com.example.shopee.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    TextView txt_emptyCart, txt_total, btn_buy;
    Toolbar toolbar;
    RecyclerView rcv_Cart;
    CartAdapter cartAdapter;
    List<Cart> listCart;
    long totalProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Mapping();
        ActionToolBar();
        initControll();
        calTotal();
    }

    private void calTotal() {
        totalProducts = 0;
        for(int i=0; i<Utils.ListItemBuy.size(); i++){
            totalProducts = totalProducts + Utils.ListItemBuy.get(i).getPrice() * Utils.ListItemBuy.get(i).getQuantity();
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        txt_total.setText("đ" + decimalFormat.format(totalProducts));
    }

    private void initControll() {
        rcv_Cart.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcv_Cart.setLayoutManager(layoutManager);

        if(Utils.ListCart.size() == 0) {
            txt_emptyCart.setVisibility(View.VISIBLE);
        }
        else {
            cartAdapter = new CartAdapter(getApplicationContext(), Utils.ListCart);
            rcv_Cart.setAdapter(cartAdapter);
        }

        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.ListItemBuy.size() != 0) {
                    Intent intentPayment = new Intent(getApplicationContext(), PaymentActivity.class);
                intentPayment.putExtra("total", totalProducts);
                startActivity(intentPayment);
                //finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Bạn chưa chọn sản phẩm nào", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.ListItemBuy.clear();
                calTotal();
                finish();
            }
        });
    }

    private void Mapping() {
        txt_emptyCart = findViewById(R.id.txt_emptyCart);
        toolbar = findViewById(R.id.tbar_cart);
        rcv_Cart = findViewById(R.id.rcv_Cart);
        txt_total = findViewById(R.id.txt_total);
        btn_buy = findViewById(R.id.btn_buy);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventCalTotal(CalTotalEvent e) {
        if (e != null) {
            calTotal();
        }
    }

}