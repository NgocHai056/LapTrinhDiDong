package com.example.sellers.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import okhttp3.ResponseBody;
import java.io.IOException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sellers.R;
import androidx.databinding.DataBindingUtil;
import com.example.sellers.databinding.ActivityInsertProductBinding;
import com.example.sellers.model.MessageModel;
import com.example.sellers.model.Products;
import com.example.sellers.retrofit.ApiShopee;
import com.example.sellers.retrofit.RetrofitClient;
import com.example.sellers.utils.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertProductActivity extends AppCompatActivity {

    Spinner spinner;
    ApiShopee apiShopee;
    Toolbar toolbar;
    String mediaPath;

    String currentImagePath;

    Products productEdit;
    boolean flag = false;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    int loai = 0, quantity = 0;
    ActivityInsertProductBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInsertProductBinding.inflate(getLayoutInflater());
        apiShopee = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiShopee.class);
        setContentView(binding.getRoot());
        initView();
        initData();
        Mapping();
        ActionToolBar();

        Intent intent = getIntent();
        productEdit = (Products) intent.getSerializableExtra("sua");
        if(productEdit == null){
            flag = false;
        }else{
            flag = true;
            binding.addnewPd.setText("Lưu");
            binding.titleTsp.setTitle("Chỉnh sửa sản phẩm");

            binding.nameproduct.setText(productEdit.getName());
            binding.price.setText(productEdit.getPrice()+"");
            binding.describe.setText(productEdit.getDescribe());
            binding.image.setText(productEdit.getSrc_img());
            binding.quantity.setText(productEdit.getQuantity()+"");
            binding.spinnerType.setSelection(productEdit.getCategory());
        }
    }

    private void Mapping(){
        toolbar = findViewById(R.id.title_tsp);
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

    private void initData() {
        List<String> stringList = new ArrayList<>();
        stringList.add("Vui lòng chọn loại");
        stringList.add("Laptop");
        stringList.add("Điện thoại");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stringList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                loai = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.addnewPd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == false){
                    insertProduct();
                }else{
                    EditProduct();
                }
            }
        });

        binding.addimgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(InsertProductActivity.this)
                        .crop()
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
    }

    private void EditProduct() {
        String str_name = binding.nameproduct.getText().toString().trim();
        String str_price = binding.price.getText().toString().trim();
        String str_describe = binding.describe.getText().toString().trim();
        String str_src_img = binding.image.getText().toString().trim();
        String str_quantity = binding.quantity.getText().toString().trim();
        if (TextUtils.isEmpty(str_name) || TextUtils.isEmpty(str_price) || TextUtils.isEmpty(str_describe) || TextUtils.isEmpty(str_src_img) || TextUtils.isEmpty(str_quantity)|| loai == 0) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_LONG).show();
        } else if (Integer.parseInt(str_quantity) == 0 ) {
            Toast.makeText(getApplicationContext(), "Số lượng sản phẩm không hợp lệ", Toast.LENGTH_LONG).show();
        } else {
            quantity = Integer.parseInt(str_quantity);
            compositeDisposable.add(apiShopee.updateProduct(str_name, str_price, str_src_img, str_describe, quantity, loai, productEdit.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if (messageModel.isSuccess()){
                                    Toast.makeText(getApplicationContext(),messageModel.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),messageModel.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(),throwable.getMessage(), Toast.LENGTH_LONG).show();
                            }
                    ));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            mediaPath = getPath(uri);
            uploadMultipleFiles();
            Log.d("log","onActivityResult: "+mediaPath);
        }
    }
    private String getPath(Uri uri){
        String result;
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if(cursor == null){
            result = uri.getPath();
        }else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }

    // Uploading Image
    private void uploadMultipleFiles() {
        Uri uri = Uri.parse(mediaPath);
        File file = new File(getPath(uri));
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("file", file.getName(), requestBody1);
        Call<MessageModel> call = apiShopee.uploadFile(fileToUpload1);
        call.enqueue(new Callback< MessageModel >() {
            @Override
            public void onResponse(Call <MessageModel> call, Response <MessageModel> response) {
                MessageModel messageModel = response.body();
                if (messageModel != null) {
                    Log.d("log1", "Response: " + messageModel.getName());
                    if (messageModel.isSuccess()) {
                        Toast.makeText(getApplicationContext(), "Thành công rồi", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.v("Response", messageModel.toString());
                }
            }
            @Override
            public void onFailure(Call < MessageModel > call, Throwable t) {
                Log.d("log", t.getMessage());
            }
        });
    }
    private void insertProduct() {
        String str_name = binding.nameproduct.getText().toString().trim();
        String str_price = binding.price.getText().toString().trim();
        String str_describe = binding.describe.getText().toString().trim();
        String str_src_img = binding.image.getText().toString().trim();
        String str_quantity = binding.quantity.getText().toString().trim();
        if (TextUtils.isEmpty(str_name) || TextUtils.isEmpty(str_price) || TextUtils.isEmpty(str_describe) || TextUtils.isEmpty(str_src_img) || TextUtils.isEmpty(str_quantity)|| loai == 0) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_LONG).show();
        } else if (Integer.parseInt(str_quantity) == 0 ) {
            Toast.makeText(getApplicationContext(), "Số lượng sản phẩm không hợp lệ", Toast.LENGTH_LONG).show();
        } else {
            quantity = Integer.parseInt(str_quantity);
            compositeDisposable.add(apiShopee.insertProduct(str_name, str_price, str_src_img, str_describe, quantity, loai)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if (messageModel.isSuccess()){
                                    Toast.makeText(getApplicationContext(),messageModel.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),messageModel.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(),throwable.getMessage(), Toast.LENGTH_LONG).show();
                            }
                    ));
        }
    }

    private void initView() {
        spinner = findViewById(R.id.spinner_type);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}