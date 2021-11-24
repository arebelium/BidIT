package com.example.bidit;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.FileUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseReference database;
    GridView simpleList;
    ArrayList<Item> productList = new ArrayList<Item>();

    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //navbar image start
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_bg));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.navigation_image, null);
        actionBar.setCustomView(view);
        //navbar image end


        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        intent = new Intent(HomeActivity.this, HomeActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_add:
                        intent = new Intent(HomeActivity.this, AddProduct.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_reserved:
                        return true;
                    case R.id.nav_account:
                        return true;
                    case R.id.nav_settings:
                        return true;
                    case R.id.nav_logout:
                        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("email", "");
                        editor.putString("name", "");
                        editor.apply();
                        intent = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        return onOptionsItemSelected(item);
                }
            }
        });
        actionBarDrawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        simpleList = (GridView) findViewById(R.id.simpleGridView);
        syncAuctionsInfo();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void syncAuctionsInfo() {
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.get("http://bidit-web.herokuapp.com/api/auctions/")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONArray jArray = (JSONArray) response;
                        if (jArray != null) {
                            for (int i = 0; i < jArray.length(); i++) {
                                try {
                                    JSONObject object = jArray.getJSONObject(i);
                                    productList.add(new Item(object.getJSONObject("product").getString("name"), object.getJSONObject("auction").getInt("id"), object.getJSONObject("product").getString("image")));
                                    syncAuctionInfo(String.valueOf(object.getJSONObject("auction").getInt("id")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            MyAdapter myAdapter = new MyAdapter(HomeActivity.this, R.layout.grid_view_items, productList);
                            simpleList.setAdapter(myAdapter);
                            simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(HomeActivity.this, SecondActivity.class);
                                    intent.putExtra("image", productList.get(position).getProductImageUrl());
                                    intent.putExtra("id", productList.get(position).getProductId());
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                    }
                });
    }

    public void syncAuctionInfo(String id) {
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.get("https://bidit-web.herokuapp.com/api/auctions/" + id)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int productId, winnerId, transactionId, highestBid=0;
                            int id = response.getJSONObject("auction").getInt("id");
                            productId = Integer.parseInt(response.getJSONObject("auction").getString("product_id"));
                            try{
                                winnerId = response.getJSONObject("auction").getInt("winner_id");
                            } catch (JSONException e){
                                winnerId = 0;
                            }
                            try{
                                transactionId = response.getJSONObject("auction").getInt("transactionId");
                            } catch (JSONException e){
                                transactionId = 0;
                            }
                            try{
                                JSONObject bid = response.getJSONObject("highestBid");
                                highestBid = bid.getInt("amount");
                            } catch (JSONException e){
                                highestBid = 0;
                            }
                            int isComplete = response.getJSONObject("auction").getInt("is_complete");
                            int price = response.getJSONObject("product").getInt("price");
                            String time = response.getJSONObject("auction").getString("expires_at");
                            String name = response.getJSONObject("product").getString("name");
                            String description = response.getJSONObject("product").getString("description");
                            ArrayList<Bid> bids = new ArrayList<>();
                            JSONArray jArray = response.getJSONArray("bids");
                            if (jArray != null) {
                                for (int i = 0; i < jArray.length(); i++) {
                                    try {
                                        JSONObject object = jArray.getJSONObject(i);
                                        Bid bid = new Bid(Integer.parseInt(object.getString("id")), object.getInt("amount"), object.getString("name"), id);
                                        bids.add(bid);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            Auction auction = new Auction(id, productId, winnerId, transactionId, isComplete, price, time, name, description, highestBid);
                            database = FirebaseDatabase.getInstance().getReference();
                            database.child("auctions").child(id + "").setValue(auction);
                            database.child("bids").child(id + "").setValue(bids);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, e.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                    }
                });
    }

}
