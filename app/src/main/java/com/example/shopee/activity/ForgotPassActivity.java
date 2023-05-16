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
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ForgotPassActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText input_email;
    TextView btn_sent;

    ApiShopee apiShopee;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        
        Mapping();
        ActionToolBar();
        initControll();
    }
    private void Mapping() {
        Paper.init(this);
        apiShopee = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiShopee.class);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        toolbar = findViewById(R.id.tbar_login);
        link_signin = findViewById(R.id.link_signin);
        input_username = findViewById(R.id.input_username);
        input_password = findViewById(R.id.input_password);
        btn_login = findViewById(R.id.btn_login);
        forgotpwd_link = findViewById(R.id.forgotpwd_link);

        //read data
        if(Paper.book().read("username") != null && Paper.book().read("pass") != null) {
            input_username.setText(Paper.book().read("username"));
            input_password.setText(Paper.book().read("pass"));

            /*if(Paper.book().read("isLogin") != null) {
                boolean flag = Paper.book().read("isLogin");
                if (flag) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Login(Paper.book().read("username"), Paper.book().read("pass"));
                        }
                    }, 1000);
                }
            }*/
        }
    }

    private void initControll() {
        btn_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = input_email.getText().toString().trim();
                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                }
                else {
                    compositeDisposable.add(apiShopee.forgotPass(email)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    userModel -> {
                                        if (userModel.isSuccess()){
                                            Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                            Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);
                                            startActivity(intentLogin);
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    throwable -> {
                                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                            ));
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
                finish();
            }
        });
    }


}