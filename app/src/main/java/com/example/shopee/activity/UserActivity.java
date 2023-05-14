package com.example.shopee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.model.User;
import com.example.shopee.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Book;
import io.paperdb.Paper;

public class UserActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView username, email, order_unconfimred, order_shipping, order_received, change_name, change_pass, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Mapping();
        ActionToolBar();
        LoadInformation();
        HandleClickOrder();
        HandleClickSettingAccount();
    }

    private void HandleClickSettingAccount() {

        change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChangeNameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChangePassActivity.class);
                startActivity(intent);
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // xoa key user
                Paper.book().delete("user");
                Utils.UserCurent = new User();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });
    }

    private void HandleClickOrder() {
        order_unconfimred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyOrderActivity.class);
                intent.putExtra("typeOrder", 1);
                startActivity(intent);
            }
        });

        order_shipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyOrderActivity.class);
                intent.putExtra("typeOrder", 2);
                startActivity(intent);
            }
        });

        order_received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyOrderActivity.class);
                intent.putExtra("typeOrder", 3);
                startActivity(intent);
            }
        });
    }

    private void LoadInformation() {
        username.setText(Utils.UserCurent.getUsername());
        email.setText(Utils.UserCurent.getEmail());
    }



    private void Mapping() {
        toolbar = findViewById(R.id.tbar_user);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        order_unconfimred = findViewById(R.id.order_unconfimred);
        order_shipping = findViewById(R.id.order_shipping);
        order_received = findViewById(R.id.order_received);
        change_name = findViewById(R.id.change_name);
        change_pass = findViewById(R.id.change_pass);
        logout = findViewById(R.id.logout);
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