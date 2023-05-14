package com.example.shopee.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopee.R;
import com.example.shopee.model.NotiSendData;
import com.example.shopee.retrofit.ApiPushNotification;
import com.example.shopee.retrofit.ApiShopee;
import com.example.shopee.retrofit.RetrofitClient;
import com.example.shopee.retrofit.RetrofitClientNoti;
import com.example.shopee.utils.Utils;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.coroutines.Continuation;
import retrofit2.Retrofit;

public class PaymentActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText input_fullName, input_phoneNumber, input_address;
    TextView txt_total, btn_order;

    ApiShopee apiShopee;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    long total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Mapping();
        ActionToolBar();
        initControll();
    }

    private void initControll() {

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        total = getIntent().getLongExtra("total", 0);
        txt_total.setText(decimalFormat.format(total));

        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    String fullName = input_fullName.getText().toString().trim();
                    String phoneNumber = input_phoneNumber.getText().toString().trim();
                    String address = input_address.getText().toString().trim();

                    if (TextUtils.isEmpty(fullName)) {
                        Toast.makeText(getApplicationContext(), "Bạn chưa nhập họ và tên", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(phoneNumber)) {
                        Toast.makeText(getApplicationContext(), "Bạn chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(address)) {
                        Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
                    } else {
                        //post data
                        int idUser = Utils.UserCurent.getId();
                        Log.d("test", new Gson().toJson(Utils.ListItemBuy));
                        String str = new Gson().toJson(Utils.ListItemBuy);
                        compositeDisposable.add(apiShopee.createOrder(idUser, fullName, phoneNumber, address, String.valueOf(total), str)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        userModel -> {
                                            pushNotication();
                                            Toast.makeText(getApplicationContext(), "Đặt hàng thành công.", Toast.LENGTH_SHORT).show();
                                            Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
                                            for (int i = 0; i<Utils.ListCart.size(); i++){
                                                for(int j=0; j<Utils.ListItemBuy.size(); j++){
                                                    if (Utils.ListItemBuy.get(j).getIdProduct() == Utils.ListCart.get(i).getIdProduct()) {
                                                        Utils.ListCart.remove(i);
                                                        //Utils.ListItemBuy.remove(j);
                                                    }
                                                }
                                            }
                                            Utils.ListItemBuy.clear();
                                            startActivity(intentMain);
                                            finish();
                                        },
                                        throwable -> {
                                            pushNotication();
                                            Toast.makeText(getApplicationContext(), "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                                            Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
                                            for (int i = 0; i<Utils.ListCart.size(); i++){
                                                for(int j=0; j<Utils.ListItemBuy.size(); j++){
                                                    if (Utils.ListItemBuy.get(j).getIdProduct() == Utils.ListCart.get(i).getIdProduct()) {
                                                        Utils.ListCart.remove(i);
                                                        //Utils.ListItemBuy.remove(j);
                                                    }
                                                }
                                            }
                                            Utils.ListItemBuy.clear();
                                            startActivity(intentMain);
                                            finish();
                                        }
                                ));
                    }
                }
        });
    }

    private void pushNotication() {
        String token = "fLXLVKiMRO-lbSuAOihi9K:APA91bG5R8xcM7ZS3KFuUlU1OQVuxkOFzpk-8SAVMyN07KmR7tdsZNpm6WWj_LKghB0jYgx-2kdcY54gJa5q9vBDJrnVMall5cEtyev-PnoVixT66SL-BDhnMBiJ7YgJzkqbBnlY-Q3P";
        Map<String, String> data = new HashMap<>();
        data.put("title", "Thông báo");
        data.put("body", "Bạn có đơn hàng mới");
        NotiSendData notiSendData = new NotiSendData(token, data);
        ApiPushNotification apiPushNotification = RetrofitClientNoti.getInstance().create(ApiPushNotification.class);
        compositeDisposable.add(apiPushNotification.sendNotification(notiSendData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        notiResponse -> {
                            Toast.makeText(getApplicationContext(), "Đã gửi thông báo", Toast.LENGTH_SHORT).show();
                        },
                        throwable -> {
                            Log.d("error", throwable.getMessage());
                        }
                ));
    }

    private void Mapping() {
        apiShopee = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiShopee.class);
        toolbar = findViewById(R.id.tbar_payment);
        input_fullName = findViewById(R.id.input_fullName);
        input_phoneNumber = findViewById(R.id.input_phoneNumber);
        input_address = findViewById(R.id.input_address);
        txt_total = findViewById(R.id.txt_total);
        btn_order = findViewById(R.id.btn_order);
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

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}