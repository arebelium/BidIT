package com.example.bidit;

public class Item {

    String productListName;
    int productListImage;
    int productListId;

    public Item(String productName, int productImage, int productId) {
        this.productListImage = productImage;
        this.productListName = productName;
        this.productListId = productId;
    }

    public String getProductName() {
        return productListName;
    }

    public int getProductImage() {
        return productListImage;
    }

    public int getProductId() {
        return productListId;
    }
}