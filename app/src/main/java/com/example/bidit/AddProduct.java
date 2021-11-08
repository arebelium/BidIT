package com.example.bidit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AddProduct extends AppCompatActivity {


    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
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
                        intent = new Intent(AddProduct.this, HomeActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_add:
                        intent = new Intent(AddProduct.this, AddProduct.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_reserved:
                        return true;
                    case R.id.nav_account:
                        return true;
                    case R.id.nav_settings:
                        return true;
                    case R.id.nav_logout:
                        return true;
                    default:
                        return onOptionsItemSelected(item);
                }
            }
        });
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txtName = findViewById(R.id.editTxtName);
                EditText txtDescription = findViewById(R.id.editTxtDescription);
                EditText txtPrice = findViewById(R.id.editTxtPrice);
                Product product = new Product(txtName.getText().toString(),txtDescription.getText().toString(), Double.parseDouble(txtPrice.getText().toString()));
                database = FirebaseDatabase.getInstance().getReference();
                writeNewProduct(product);
                finish(); //reloads page
                startActivity(getIntent());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void writeNewProduct(Product product) { //writes product to database
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        Date currentLocalTime = cal.getTime();
        @SuppressLint("SimpleDateFormat") DateFormat date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS");
        String localTime = date.format(currentLocalTime);
        database.child("products").child(localTime).setValue(product);
    }
   
}