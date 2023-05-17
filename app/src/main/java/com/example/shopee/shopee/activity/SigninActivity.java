package com.example.shopee.activity;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SigninActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText input_email ,input_username, input_password, input_confirmPassword;
    TextView btn_signin, link_logout;

    FirebaseAuth firebaseAuth;
    ApiShopee apiShopee;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Mapping();
        ActionToolBar();
        initControll();
    }

    private void initControll() {
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sigin();
            }
        });

        link_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
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

    private void sigin() {
        String email = input_email.getText().toString().trim();
        String username = input_username.getText().toString().trim();
        String password = input_password.getText().toString().trim();
        String confirmPassword = input_confirmPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập tên người dùng", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu xác nhận", Toast.LENGTH_LONG).show();
        }
        else {
            if(password.equals(confirmPassword)) {
                if(password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập mật khẩu dài ít nhất 6 kí tự", Toast.LENGTH_LONG).show();
                    return;
                }
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if(user != null) {
                                        postData(email, password, username, user.getUid());
                                    }
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Đăng kí thất bại", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
            else {
                Toast.makeText(getApplicationContext(), "Mật khẩu không khớp", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void postData(String email, String password, String fullname, String Uid) {
        compositeDisposable.add(apiShopee.signin(email, password, fullname, Uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()) {
                                Utils.UserCurent.setEmail(email);
                                Utils.UserCurent.setPass(password);
                                Utils.UserCurent.setUsername(fullname);
                                Toast.makeText(getApplicationContext(), "Đăng ký thành công", Toast.LENGTH_LONG).show();
                                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(loginIntent);
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Tài khoản đã tồn tại", Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void Mapping() {
        apiShopee = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiShopee.class);

        toolbar = findViewById(R.id.tbar_login);
        input_email = findViewById(R.id.input_email);
        input_username = findViewById(R.id.input_username);
        input_password = findViewById(R.id.input_password);
        input_confirmPassword =findViewById(R.id.input_confirmPassword);
        btn_signin = findViewById(R.id.btn_signin);
        link_logout = findViewById(R.id.link_logout);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}