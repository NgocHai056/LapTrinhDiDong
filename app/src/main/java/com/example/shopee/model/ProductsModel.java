package com.example.shopee.model;

import java.util.List;

public class ProductsModel {
    boolean success;
    String message;
    List<Products> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Products> getResult() {
        return result;
    }

    public void setResult(List<Products> result) {
        this.result = result;
    }
}
