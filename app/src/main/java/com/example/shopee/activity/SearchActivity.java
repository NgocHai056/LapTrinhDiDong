package com.example.shopee.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.adapter.ProductsAdapter;
import com.example.shopee.adapter.SearchAdapter;
import com.example.shopee.model.Products;
import com.example.shopee.retrofit.ApiShopee;
import com.example.shopee.retrofit.RetrofitClient;
import com.example.shopee.utils.Utils;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView rcv_search;
    SearchView searchView;

    SearchAdapter searchAdapter;
    List<Products> products;
    ApiShopee apiShopee;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Mapping();
        ActionToolBar();
        initView();
    }

    private void initView() {
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                getDataSearch();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                getDataSearch();
                return false;
            }
        });
    }

    private void Mapping() {
        apiShopee = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiShopee.class);
        toolbar = findViewById(R.id.tbar_search);
        rcv_search = findViewById(R.id.rcv_search);
        searchView = findViewById(R.id.searchView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rcv_search.setLayoutManager(layoutManager);

    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(searchView.getQuery().toString() != "") {
                    String str = "";
                    searchView.setQuery(str, false);
                }
                else {*/
                    finish();
                //}
            }
        });
    }

    public void getDataSearch() {
        String str_search = searchView.getQuery().toString().trim();
        compositeDisposable.add(apiShopee.search(str_search)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                   productsModel -> {
                       searchAdapter = new SearchAdapter(getApplicationContext(), productsModel.getResult());
                        rcv_search.setAdapter(searchAdapter);
                   },
                   throwable -> {
                       Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                ));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}