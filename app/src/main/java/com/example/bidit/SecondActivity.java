package com.example.bidit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;


public class SecondActivity extends AppCompatActivity {

    ImageView selectedImage;
    TextView timer, textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        selectedImage = (ImageView) findViewById(R.id.selectedImage); // init a ImageView
        Intent intent = getIntent();
        timer = findViewById(R.id.timer);
        textView = findViewById(R.id.textView);
        //selectedImage.setImageResource(intent.getIntExtra("image", 0));

        textView.setText(String.valueOf(intent.getIntExtra("id", 0)));
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.get("https://bidit-web.herokuapp.com/api/auctions/" + intent.getIntExtra("id", 0))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            timer.setText(response.getJSONObject("highestBid").getString("name"));
                            Picasso.with(SecondActivity.this)
                                    .load(intent.getStringExtra("image"))
                                    .noPlaceholder()
                                    .fit()
                                    .centerCrop().into(selectedImage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        timer.setText(error.toString());
                    }
                });
    }

}