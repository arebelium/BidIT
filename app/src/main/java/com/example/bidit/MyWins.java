package com.example.bidit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class MyWins extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    GridView simpleList;
    ArrayList<Item> productList = new ArrayList<Item>();

    public void onBackPressed() {
        startActivity(new Intent(MyWins.this, HomeActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wins);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_bg));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.navigation_image, null);
        actionBar.setCustomView(view);

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
                        intent = new Intent(MyWins.this, HomeActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_add:
                        intent = new Intent(MyWins.this, AddProduct.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_bids:
                        intent = new Intent(MyWins.this, MyBids.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_wins:
                        intent = new Intent(MyWins.this, MyWins.class);
                        startActivity(intent);
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
                        intent = new Intent(MyWins.this, LoginActivity.class);
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
        syncWinsInfo();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.refresh:
                ProgressDialog dialog = ProgressDialog.show(MyWins.this, "",
                        "Refreshing...", true);
                finish();
                startActivity(getIntent());
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public void syncWinsInfo() {
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String idCache = prefs.getString("id", "");
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.get("http://bidit-web.herokuapp.com/api/users/"+idCache+"/wins")
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
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            MyAdapter myAdapter = new MyAdapter(MyWins.this, R.layout.grid_view_items, productList);
                            simpleList.setAdapter(myAdapter);
                            simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Toast.makeText(MyWins.this, "Add to cart",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

}