package com.example.bidit;

public class Item {

    String productListName;
    int productListId;
    String productImageUrl;

    public Item(String productName, int productId, String url) {
        this.productListName = productName;
        this.productListId = productId;
        this.productImageUrl = url;
    }

    public String getProductName() {
        return productListName;
    }

    public int getProductId() {
        return productListId;
    }

    public String getProductImageUrl(){return productImageUrl;}

}