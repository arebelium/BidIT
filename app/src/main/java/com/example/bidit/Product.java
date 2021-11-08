package com.example.bidit;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Product {
    public String name, description;
    public Double price;

    public Product() {
    }
    public Product(String name) {
        this.name = name;
    }
    public Product(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public Product(String name, String description, Double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
