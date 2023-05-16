package com.example.shopee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText input_username, input_password;
    TextView link_signin, btn_login, forgotpwd_link;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    ApiShopee apiShopee;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Mapping();
        ActionToolBar();
        initControll();
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

    private void initControll() {
        link_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signinIntent = new Intent(getApplicationContext(), SigninActivity.class);
                startActivity(signinIntent);
            }
        });

        forgotpwd_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPassIntent = new Intent(getApplicationContext(), ForgotPassActivity.class);
                startActivity(forgotPassIntent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = input_username.getText().toString().trim();
                String pass = input_password.getText().toString().trim();

                if(TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập tên đăng nhập", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(pass)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập mật khẩu", Toast.LENGTH_LONG).show();
                }
                else {
                    //save data
                    Paper.book().write("username", username);
                    Paper.book().write("pass", pass);

                    if (user != null) {
                        //user da dang nhap firebase
                        Login(username, pass);
                    }
                    else {
                        // user da dang xuat
                        if(Login(username, pass)) {
                            final FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
                            User.updatePassword(pass)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Failed to update password!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            firebaseAuth.signInWithEmailAndPassword(username, pass)
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {

                                            } else {
                                                Toast.makeText(getApplicationContext(), "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                }
            }
        });
    }





    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.UserCurent.getEmail() != null && Utils.UserCurent.getUsername() != null && Utils.UserCurent.getPass() != null) {
            input_username.setText(Utils.UserCurent.getEmail());
            input_password.setText(Utils.UserCurent.getPass());
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}