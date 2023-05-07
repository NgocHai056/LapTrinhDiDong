package com.example.shopee.utils;

import com.example.shopee.model.Cart;
import com.example.shopee.model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    //public static final String BASE_URL = "http://192.168.43.43/data_shopee/";
    public static final String BASE_URL = "http://192.168.2.17/data_shopee/";
    //public static final String SHOPEE_URL = "http://192.168.2.17:8080/api/";
    public static List<Cart> ListCart;
    public static List<Cart> ListItemBuy = new ArrayList<>();
    public static User UserCurent = new User();
}
