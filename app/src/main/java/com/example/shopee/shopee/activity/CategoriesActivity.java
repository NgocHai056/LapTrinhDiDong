package com.example.shopee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.adapter.CategoriesAdapter;
import com.example.shopee.adapter.ProductsAdapter;
import com.example.shopee.model.Products;
import com.example.shopee.retrofit.ApiShopee;
import com.example.shopee.retrofit.RetrofitClient;
import com.example.shopee.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;

public class CategoriesActivity extends AppCompatActivity {

    int page = 1;
    int type;
    String title;
    ProductsAdapter productsAdapter;
    List<Products> products;
    ApiShopee apiShopee;

    Toolbar toolbar;
    RecyclerView rcv_pd;
    LinearLayoutManager linearLayoutManager;
    Handler handler = new Handler();
    boolean isLoading = false;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        apiShopee = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiShopee.class);

        type = getIntent().getIntExtra("type", 1);
        title = getIntent().getStringExtra("title");
        Mapping();
        ActionToolBar();
        getData(page);
        addLoadEvent();
    }

    private void addLoadEvent() {
        rcv_pd.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                getData(page);
                productsAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 1500);
    }

    private void getData(int page) {
        compositeDisposable.add(apiShopee.getProductsByCategory(page, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        productsModel -> {
                            if (productsModel.isSuccess()) {
                                if(productsAdapter == null) {
                                    products = productsModel.getResult();
                                    productsAdapter = new ProductsAdapter(getApplicationContext(), products);
                                    rcv_pd.setAdapter(productsAdapter);
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

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void Mapping() {
        toolbar = findViewById(R.id.title_ctg);
        rcv_pd = findViewById(R.id.rcv_pd);

        linearLayoutManager = new GridLayoutManager(this, 2);
        linearLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rcv_pd.setLayoutManager(linearLayoutManager);

        rcv_pd.setHasFixedSize(true);
        products = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}