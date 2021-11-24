package com.example.bidit;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class Auction {
    public int id, productId, winner_id, transaction_id, is_complete, price, highest_bid;
    public String time;
    public String name;
    public String description;

    public Auction(int id, int productId, int winner_id, int transaction_id, int is_complete, int price, String time, String name, String description, int highest_bid) {
        this.id = id;
        this.productId = productId;
        this.winner_id = winner_id;
        this.transaction_id = transaction_id;
        this.is_complete = is_complete;
        this.price = price;
        this.time = time;
        this.name = name;
        this.description = description;
        this.highest_bid = highest_bid;
    }

    public Auction(int id, int productId, int isComplete, int price, String time, String name, String description) {
        this.id = id;
        this.productId = productId;
        this.is_complete = isComplete;
        this.price = price;
        this.time = time;
        this.name = name;
        this.description = description;

    }
}
