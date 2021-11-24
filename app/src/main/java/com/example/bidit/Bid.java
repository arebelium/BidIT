package com.example.bidit;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Bid {
    public int id, amount, auctionId;
    public String name;

    public Bid(int id, int amount, String name, int auctionId) {
        this.id = id;
        this.amount = amount;
        this.name = name;
        this.auctionId = auctionId;
    }
}
