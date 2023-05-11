    package com.example.shopee.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.shopee.R;
import com.example.shopee.adapter.CategoriesAdapter;
import com.example.shopee.adapter.ProductsAdapter;
import com.example.shopee.model.Categories;
import com.example.shopee.model.MessageModel;
import com.example.shopee.model.Products;
import com.example.shopee.model.ProductsModel;
import com.example.shopee.retrofit.ApiShopee;
import com.example.shopee.retrofit.RetrofitClient;
import com.example.shopee.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

    public class MainActivity extends AppCompatActivity {

        ViewFlipper viewFlipper;
        GridView gv_categories;
        RecyclerView rcv_pd;
        FrameLayout btn_cart;
        ImageView btn_user;
        TextView searchView;

        CategoriesAdapter categoriesAdapter;
        ProductsAdapter productsAdapter;
        List<Categories> categories;
        List<Products> products;
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        ApiShopee apiShopee;
        NotificationBadge badge;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            apiShopee = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiShopee.class);

            getToken();
            Mapping();
            if (isConnected(this)) {
                ActionViewFlipper();
                getCategories();
                getProcducts();
                getClickEventCategory();
            } else {
                Toast.makeText(getApplicationContext(), "Không có Internet, vui lòng kết nối", Toast.LENGTH_LONG).show();
            }
        }

        private void SetBadge() {
            int totalItem = 0;
            for (int i=0; i<Utils.ListCart.size(); i++){
                totalItem = totalItem + Utils.ListCart.get(i).getQuantity();
            }
            if (totalItem != 0) {
                badge.setVisibility(View.VISIBLE);
                badge.setText(String.valueOf(totalItem));
            }
            else {
                badge.setVisibility(View.GONE);
            }
        }

        private void getToken() {
            FirebaseMessaging.getInstance().getToken()
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            if(!TextUtils.isEmpty(s)) {
                                compositeDisposable.add(apiShopee.updateToken(Utils.UserCurent.getId(), s)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                messageModel -> {
                                                },
                                                throwable -> {
                                                    Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                        ));
                            }
                        }
                    });
        }

        private void getClickEventCategory() {
            gv_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent ctgActivity = new Intent(getApplicationContext(), CategoriesActivity.class);
                    ctgActivity.putExtra("type", (i+1));

                    ctgActivity.putExtra("title", ((TextView)view.findViewById(R.id.item_name)).getText());
                    startActivity(ctgActivity);
                }
            });
        }

        private void getProcducts() {
            compositeDisposable.add(apiShopee.getProcducts()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            productsModel -> {
                                if (productsModel.isSuccess()) {
                                    
                                }
                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(), "Không kết nối được với sever" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                            }
                    ));
        }

        private void getCategories() {
            compositeDisposable.add(apiShopee.getCategories()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            categoriesModel -> {
                                if (categoriesModel.isSuccess()) {
                                    categories = categoriesModel.getResult();
                                    categoriesAdapter = new CategoriesAdapter(getApplicationContext(),R.layout.item_category, categories);
                                    gv_categories.setAdapter(categoriesAdapter);
                                }
                            }
                    )
            );
        }

        private void ActionViewFlipper() {


            ImageView imageView1 = new ImageView(this);
            imageView1.setImageResource(R.drawable.img_1);
            viewFlipper.addView(imageView1);
            ImageView imageView2 = new ImageView(this);
            imageView2.setImageResource(R.drawable.img_2);
            viewFlipper.addView(imageView2);
            ImageView imageView3 = new ImageView(this);
            imageView3.setImageResource(R.drawable.img_3);
            viewFlipper.addView(imageView3);
            ImageView imageView4 = new ImageView(this);
            imageView4.setImageResource(R.drawable.img_4);
            viewFlipper.addView(imageView4);
            ImageView imageView5 = new ImageView(this);
            imageView5.setImageResource(R.drawable.img_5);
            viewFlipper.addView(imageView5);
            ImageView imageView6 = new ImageView(this);
            imageView6.setImageResource(R.drawable.img_6);
            viewFlipper.addView(imageView6);
            ImageView imageView7 = new ImageView(this);
            imageView7.setImageResource(R.drawable.img_7);
            viewFlipper.addView(imageView7);
            ImageView imageView8 = new ImageView(this);
            imageView8.setImageResource(R.drawable.img_8);
            viewFlipper.addView(imageView8);
            ImageView imageView9 = new ImageView(this);
            imageView9.setImageResource(R.drawable.img_9);
            viewFlipper.addView(imageView9);

            //viewFlipper.setFlipInterval(9000);
            viewFlipper.setAutoStart(true);
            Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
            Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
            viewFlipper.setInAnimation(slide_in);
            viewFlipper.setOutAnimation(slide_out);
        }

        private void Mapping() {
            viewFlipper = findViewById(R.id.viewFlipper);
            gv_categories = (GridView) findViewById(R.id.gv_categories);
            rcv_pd = findViewById(R.id.rcv_pd);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
            rcv_pd.setLayoutManager(layoutManager);
            rcv_pd.setHasFixedSize(true);
            badge = findViewById(R.id.quantity_cart);
            btn_cart = findViewById(R.id.btn_cart);
            btn_user = findViewById(R.id.btn_user);
            searchView = findViewById(R.id.searchView);

            categories = new ArrayList<>();
            products = new ArrayList<>();

            if(Utils.ListCart == null){
                Utils.ListCart = new ArrayList<>();
            }
            else {
                int totalItem = 0;
                for (int i=0; i<Utils.ListCart.size(); i++){
                    totalItem = totalItem + Utils.ListCart.get(i).getQuantity();
                }

                if(totalItem != 0) {
                    badge.setText(String.valueOf(totalItem));
                }
            }

            btn_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Utils.UserCurent.getEmail() == null) {
                        Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Intent intentCart = new Intent(getApplicationContext(), CartActivity.class);
                        startActivity(intentCart);
                    }
                }
            });

            btn_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Utils.UserCurent.getEmail() == null) {
                        Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Intent intentUser = new Intent(getApplicationContext(), UserActivity.class);
                        startActivity(intentUser);
                    }
                }
            });

            searchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentSearch = new Intent(getApplicationContext(), SearchActivity.class);
                    startActivity(intentSearch);
                }
            });
        }

        @Override
        protected void onResume() {
            super.onResume();
            int totalItem = 0;
            for (int i=0; i<Utils.ListCart.size(); i++){
                totalItem = totalItem + Utils.ListCart.get(i).getQuantity();
            }

            if(totalItem != 0) {
                badge.setText(String.valueOf(totalItem));
            }
        }

        private boolean isConnected(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((wifi != null && wifi.isConnected()) || (mobi != null && mobi.isConnected())) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onStart() {
            super.onStart();
            SetBadge();
        }

        @Override
        protected void onDestroy() {
            compositeDisposable.clear();
            super.onDestroy();
        }
    }