package com.example.shopee.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.retrofit.ApiShopee;
import com.example.shopee.retrofit.RetrofitClient;
import com.example.shopee.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChangeNameActivity extends AppCompatActivity {

    Toolbar tbar_changeName;
    EditText input_pass, input_newName;
    TextView btn_sent;

    ApiShopee apiShopee;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        Mapping();
        ActionToolBar();
        initControll();
    }

    private void initControll() {
        btn_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Utils.UserCurent.getEmail();
                String pass = input_pass.getText().toString().trim();
                String newName = input_newName.getText().toString().trim();

                if(TextUtils.isEmpty(pass)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(newName)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập tên mới", Toast.LENGTH_LONG).show();
                }
                else {
                    ChangeName(email, pass, newName);
                }
            }
        });
    }

    private void ChangeName(String email, String pass, String newName) {
        compositeDisposable.add(apiShopee.changeName(email, pass, newName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()) {
                                Utils.UserCurent.setUsername(newName);
                                Toast.makeText(getApplicationContext(), "Đổi tên thành công", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void Mapping() {
        apiShopee = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiShopee.class);

        tbar_changeName = findViewById(R.id.tbar_changeName);
        input_pass = findViewById(R.id.input_pass);
        input_newName = findViewById(R.id.input_newName);
        btn_sent = findViewById(R.id.btn_sent);
    }

    private void ActionToolBar() {
        setSupportActionBar(tbar_changeName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tbar_changeName.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}