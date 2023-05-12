package com.example.sellers.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sellers.R;
import com.example.sellers.adapter.ProductsAdapter;
import com.example.sellers.model.EventBus.EditDeleteEvent;
import com.example.sellers.model.Products;
import com.example.sellers.retrofit.ApiShopee;
import com.example.sellers.retrofit.RetrofitClient;
import com.example.sellers.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CategoriesActivity extends AppCompatActivity {

    int page = 1;
    int type;
    String title;
    ProductsAdapter productsAdapter;
    List<Products> products;
    Products productEdDel;
    ApiShopee apiShopee;
    ImageView add_btn;
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
        add_btn = findViewById(R.id.add_btn);
        linearLayoutManager = new GridLayoutManager(this, 2);
        linearLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rcv_pd.setLayoutManager(linearLayoutManager);

        rcv_pd.setHasFixedSize(true);
        products = new ArrayList<>();

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.UserCurent.getEmail() == null) {
                    Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent insertIntent = new Intent(getApplicationContext(), InsertProductActivity.class);
                    startActivity(insertIntent);

                }
            }
        });
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Sửa")){
            EditProduct();
        } else if (item.getTitle().equals("Xoá")){
            DeleteProduct();
        }

        return super.onContextItemSelected(item);
    }

    private void DeleteProduct() {
        compositeDisposable.add(apiShopee.DeleteProduct(productEdDel.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if(messageModel.isSuccess()){
                                Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_LONG).show();
                                getData(page);
                            }else{
                                Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        },
                        throwable -> {
                            Log.d("log", throwable.getMessage());
                        }
                ));
    }

    private void EditProduct() {
            Intent intent = new Intent(getApplicationContext(), InsertProductActivity.class);
            intent.putExtra("sua", productEdDel);
            startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void EditDeleteEvent(EditDeleteEvent event){
        if(event != null){
            productEdDel = event.getProduct();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}