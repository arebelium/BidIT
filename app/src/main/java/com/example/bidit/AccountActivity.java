package com.example.bidit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AccountActivity extends AppCompatActivity {


    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseReference database;

    int bids;
    int wins;
    int deals;

    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        showNavbarImage();
        getCountBids();

        //Texts in LinearLayout
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        TextView txtFirstName = findViewById(R.id.txtFirstNameAccount);
        TextView txtEmail = findViewById(R.id.txtEmailAccount);
        TextView txtBids = findViewById(R.id.txtStatsAccount);
        txtFirstName.setText("First name: \n\n" + prefs.getString("name", ""));
        txtEmail.setText("Email: \n\n" + prefs.getString("email", ""));
        txtBids.setText(" Bids Total: " + bids + "\n Wins Total: " + wins + "\nDeals Total: " + deals);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        intent = new Intent(AccountActivity.this, HomeActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_add:
                        intent = new Intent(AccountActivity.this, AddProduct.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_bids:
                        intent = new Intent(AccountActivity.this, MyBids.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_wins:
                        intent = new Intent(AccountActivity.this, MyWins.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_account:
                        intent = new Intent(AccountActivity.this, AccountActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_settings:
                        return true;
                    case R.id.nav_logout:
                        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("email", "");
                        editor.putString("name", "");
                        editor.apply();
                        intent = new Intent(AccountActivity.this, LoginActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        return onOptionsItemSelected(item);
                }
            }
        });
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getCountBids() {
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String idCache = prefs.getString("id", "");
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.get("http://bidit-web.herokuapp.com/api/users/"+idCache+"/bids")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONArray jArray = (JSONArray) response;
                        bids = jArray.length();
                    }
                    @Override
                    public void onError(ANError error) {
                    }
                });
        AndroidNetworking.get("http://bidit-web.herokuapp.com/api/users/"+idCache+"/wins")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONArray jArray = (JSONArray) response;
                        wins = jArray.length();
                    }
                    @Override
                    public void onError(ANError error) {
                    }
                });
        AndroidNetworking.get("http://bidit-web.herokuapp.com/api/users/"+idCache+"/transactions")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONArray jArray = (JSONArray) response;
                        deals = jArray.length();
                    }
                    @Override
                    public void onError(ANError error) {
                    }
                });
    }

    public void showNavbarImage() {
        //navbar image start
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_bg));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.navigation_image, null);
        actionBar.setCustomView(view);
        //navbar image end
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}