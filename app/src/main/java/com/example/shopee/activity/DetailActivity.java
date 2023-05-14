package com.example.shopee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopee.R;
import com.example.shopee.adapter.ProductsAdapter;
import com.example.shopee.model.Cart;
import com.example.shopee.model.Products;
import com.example.shopee.retrofit.ApiShopee;
import com.example.shopee.retrofit.RetrofitClient;
import com.example.shopee.utils.Utils;
import com.nex3z.notificationbadge.NotificationBadge;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class DetailActivity extends AppCompatActivity {

    String title;
    Toolbar toolbar;
    TextView txt_name, txt_price, txt_describe, btn_buy;
    ImageView cart_icon, img;
    LinearLayout btn_add;
    Spinner spinner;
    Products product;
    NotificationBadge badge;
    FrameLayout btn_cart;

    int page = 1;
    int type;
    RecyclerView rcv_pdsame;
    ProductsAdapter productsAdapter;
    List<Products> products;
    ApiShopee apiShopee;
    LinearLayoutManager linearLayoutManager;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    Handler handler = new Handler();
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        apiShopee = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiShopee.class);

        Mapping();
        ActionToolBar();
        getData();
        initControll();

        type = product.getCategory();
        getDataForRCV(page);
        addLoadEvent();
    }

    private void initControll() {
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utils.UserCurent.getEmail() == null) {
                    Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
                else {
                    addToCart();
                }
            }
        });

        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.UserCurent.getEmail() == null) {
                    Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
                else {
                    addToCart();
                    Intent cartIntent = new Intent(getApplicationContext(), CartActivity.class);
                    startActivity(cartIntent);
                }
            }
        });
    }

    private void addToCart() {
        if(Utils.ListCart.size() > 0) {
            boolean flag = false;
            int quantity = 1;
            for(int i=0; i<Utils.ListCart.size(); i++) {
                if (Utils.ListCart.get(i).getIdProduct() == product.getId()) {
                    Utils.ListCart.get(i).setQuantity(quantity + Utils.ListCart.get(i).getQuantity());
                    long price = Long.parseLong(product.getPrice());
                    Utils.ListCart.get(i).setPrice(price);
                    flag = true;
                }
            }
            if(flag == false) {
                long price = Long.parseLong(product.getPrice()) * quantity;
                Cart cart = new Cart();
                cart.setPrice(price);
                cart.setQuantity(quantity);
                cart.setIdProduct(product.getId());
                cart.setNameProduct(product.getName());
                cart.setImg(product.getSrc_img());
                Utils.ListCart.add(cart);
            }
        }
        else {
            int quantity = 1;
            long price = Long.parseLong(product.getPrice()) * quantity;
            Cart cart = new Cart();
            cart.setPrice(price);
            cart.setQuantity(quantity);
            cart.setIdProduct(product.getId());
            cart.setNameProduct(product.getName());
            cart.setImg(product.getSrc_img());
            Utils.ListCart.add(cart);
        }
        int totalItem = 0;
        for (int i=0; i<Utils.ListCart.size(); i++){
            totalItem = totalItem + Utils.ListCart.get(i).getQuantity();
        }
        badge.setVisibility(View.VISIBLE);
        badge.setText(String.valueOf(totalItem));
    }

    private void getData() {
        product = (Products) getIntent().getSerializableExtra("product");
        title = product.getName();
        txt_name.setText(product.getName());
        txt_describe.setText(product.getDescribe());
        Glide.with(getApplicationContext()).load(product.getSrc_img()).into(img);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        txt_price.setText("đ" + decimalFormat.format(Double.parseDouble(product.getPrice())));
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void Mapping() {
        toolbar = findViewById(R.id.tbar_detail);
        txt_name = findViewById(R.id.name_detail);
        txt_price = findViewById(R.id.price_detail);
        txt_describe = findViewById(R.id.describe_detail);
        img = findViewById(R.id.img_detail);
        cart_icon = findViewById(R.id.icon_cart);
        btn_buy = findViewById(R.id.btn_buy);
        btn_add = findViewById(R.id.btn_add);
        //spinner = findViewById(R.id.spn_quantity);

        rcv_pdsame = findViewById(R.id.rcv_pdsame);
        linearLayoutManager = new GridLayoutManager(this, 2);
        linearLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rcv_pdsame.setLayoutManager(linearLayoutManager);

        rcv_pdsame.setHasFixedSize(true);
        products = new ArrayList<>();

        badge = findViewById(R.id.quantity_cart);
        if (Utils.ListCart.size() != 0) {
            int totalItem = 0;
            for (int i=0; i<Utils.ListCart.size(); i++){
                totalItem = totalItem + Utils.ListCart.get(i).getQuantity();
            }

            badge.setText(String.valueOf(totalItem));
        }

        btn_cart = findViewById(R.id.btn_cart);
        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.UserCurent.getEmail() == null) {
                    Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent cartIntent = new Intent(getApplicationContext(), CartActivity.class);
                    startActivity(cartIntent);
                    finish();
                }
            }
        });
    }

    private void addLoadEvent() {
        rcv_pdsame.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isLoading == false) {
                    if(linearLayoutManager.findLastCompletelyVisibleItemPosition() == products.size()-1) {
                        isLoading = true;
                        loadMore();
                    }
                }
            }
        });
    }

    private void loadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                products.add(null);
                productsAdapter.notifyItemInserted(products.size() - 1);
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                products.remove(products.size()-1);
                productsAdapter.notifyItemRemoved(products.size());
                page = page + 1;
                getDataForRCV(page);
                productsAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 1500);
    }

    private void getDataForRCV(int page) {
        compositeDisposable.add(apiShopee.getProductsByCategory(page, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        productsModel -> {
                            if (productsModel.isSuccess()) {
                                if(productsAdapter == null) {
                                    products = productsModel.getResult();
                                    productsAdapter = new ProductsAdapter(getApplicationContext(), products);
                                    rcv_pdsame.setAdapter(productsAdapter);
                                }
                                else {
                                    int pst = products.size()-1;
                                    int quantityAdd = productsModel.getResult().size();
                                    for (int i=0; i<quantityAdd; i++){
                                        products.add(productsModel.getResult().get(i));
                                    }
                                    productsAdapter.notifyItemRangeInserted(pst, quantityAdd);
                                }
                            }
                            else {
                                if(isLoading == false) {
                                    Toast.makeText(getApplicationContext(), "Không tìm thấy sản phẩm nào nữa", Toast.LENGTH_LONG).show();
                                    isLoading = true;
                                }
                                else {
                                    isLoading = true;
                                }
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Không kết nối được với sever " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.ListCart.size() != 0) {
            int totalItem = 0;
            for (int i=0; i<Utils.ListCart.size(); i++){
                totalItem = totalItem + Utils.ListCart.get(i).getQuantity();
            }
            badge.setVisibility(View.VISIBLE);
            badge.setText(String.valueOf(totalItem));
        }
        else {
            badge.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}