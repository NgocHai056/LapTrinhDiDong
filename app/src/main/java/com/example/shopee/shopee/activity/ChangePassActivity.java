package com.example.shopee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChangePassActivity extends AppCompatActivity {

    Toolbar tbar_changePass;
    EditText input_pass, input_newPass;
    TextView btn_sent;
    ApiShopee apiShopee;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

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
                String newPass = input_newPass.getText().toString().trim();

                if(TextUtils.isEmpty(pass)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu hiện tại", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(newPass)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu mới", Toast.LENGTH_LONG).show();
                }
                else {
                    ChangePass(email, pass, newPass);
                }
            }
        });
    }

    private void ChangePass(String email, String pass, String newPass) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        compositeDisposable.add(apiShopee.changePass(email, pass, newPass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()) {
                                Toast.makeText(getApplicationContext(), "Đổi mật khẩu thành công", Toast.LENGTH_LONG).show();
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

        tbar_changePass = findViewById(R.id.tbar_changePass);
        input_pass = findViewById(R.id.input_pass);
        input_newPass = findViewById(R.id.input_newPass);
        btn_sent = findViewById(R.id.btn_sent);
    }

    private void ActionToolBar() {
        setSupportActionBar(tbar_changePass);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tbar_changePass.setNavigationOnClickListener(new View.OnClickListener() {
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