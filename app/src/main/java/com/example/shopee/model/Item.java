package com.example.shopee.model;

public class Item {
    int idProduct;
    String name;
    int quantity;
    String src_img;
    String price;

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSrc_img() {
        return src_img;
    }

    public void setSrc_img(String src_img) {
        this.src_img = src_img;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
