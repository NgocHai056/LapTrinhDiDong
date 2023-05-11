package com.example.shopee.retrofit;

import com.example.shopee.model.CategoriesModel;
import com.example.shopee.model.MessageModel;
import com.example.shopee.model.OrderModel;
import com.example.shopee.model.ProductsModel;
import com.example.shopee.model.User;
import com.example.shopee.model.UserModel;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiShopee {
    //GET DATA
    @GET("getCategories.php")
    Observable<CategoriesModel> getCategories();

    @GET("getProducts.php")
    Observable<ProductsModel> getProcducts();

    @POST("getProductsByCategory.php")
    @FormUrlEncoded
    Observable<ProductsModel> getProductsByCategory(
            @Field("page") int page,
            @Field("loai") int loai
    );

    //POST DATA
    @POST("signin.php")
    @FormUrlEncoded
    Observable<UserModel> signin(
            @Field("email") String email,
            @Field("pass") String pass ,
            @Field("username") String username,
            @Field("Uid") String Uid
    );

    @POST("login.php")
    @FormUrlEncoded
    Observable<UserModel> login(
            @Field("email") String email,
            @Field("pass") String pass
    );

    @POST("forgotPass.php")
    @FormUrlEncoded
    Observable<UserModel> forgotPass(
            @Field("email") String email
    );

    @POST("order.php")
    @FormUrlEncoded
    Observable<UserModel> createOrder(
            @Field("idUser") int idUser,
            @Field("fullName") String fullName,
            @Field("phoneNumber") String phoneNumber,
            @Field("address") String address,
            @Field("total") String total,
            @Field("detail") String detail
    );

    @POST("showOrder.php")
    @FormUrlEncoded
    Observable<OrderModel> showOrder(
            @Field("idUser") int idUser,
            @Field("state") int state
    );

    @POST("search.php")
    @FormUrlEncoded
    Observable<ProductsModel> search(
            @Field("search") String search
    );

    @POST("changePass.php")
    @FormUrlEncoded
    Observable<UserModel> changePass(
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("newPass") String newPass
    );

    @POST("changeName.php")
    @FormUrlEncoded
    Observable<UserModel> changeName(
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("newName") String newName
    );

    @POST("deleteOrder.php")
    @FormUrlEncoded
    Observable<UserModel> deleteOrder(
            @Field("idOrder") int idOrder
    );

    @POST("receivedOrder.php")
    @FormUrlEncoded
    Observable<UserModel> receivedOrder(
            @Field("idOrder") int idOrder
    );

    @POST("updateToken.php")
    @FormUrlEncoded
    Observable<MessageModel> updateToken(
            @Field("id") int id,
            @Field("token") String token
    );
}
