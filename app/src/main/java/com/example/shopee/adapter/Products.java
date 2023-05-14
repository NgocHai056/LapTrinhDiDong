package com.example.shopee.model;

import java.io.Serializable;

public class Products implements Serializable {
    int id;
    String name;
    String price;
    String src_img;
    String describe;
    int category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSrc_img() {
        return src_img;
    }

    public void setSrc_img(String src_img) {
        this.src_img = src_img;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
