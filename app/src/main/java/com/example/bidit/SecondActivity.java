package com.example.bidit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class SecondActivity extends AppCompatActivity {

    ImageView selectedImage;
    TextView timer, timer2, txtName, txtDescription, txtBids;
    Button btnDescription, btnBids, btnPlace;
    private DatabaseReference database, database2;
    String time, bidText, btnText;

    public void onBackPressed() {
        Intent intent = getIntent();
        String a = intent.getStringExtra("activity");
        if (a.equals("MyBids")) {
            startActivity(new Intent(SecondActivity.this, MyBids.class));
        } else if (a.equals("Home")) {
            startActivity(new Intent(SecondActivity.this, HomeActivity.class));

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_bg));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.navigation_image, null);
        actionBar.setCustomView(view);

        selectedImage = (ImageView) findViewById(R.id.selectedImage);
        Intent intent = getIntent();
        timer = findViewById(R.id.txtTimer);
        timer2 = findViewById(R.id.txtTimer2);
        txtName = findViewById(R.id.txtName);
        txtDescription = findViewById(R.id.txtDescription);
        txtBids = findViewById(R.id.txtBids);
        btnDescription = findViewById(R.id.btnDescription);
        btnBids = findViewById(R.id.btnBids);
        btnPlace = findViewById(R.id.btnPlace);
        Picasso.with(SecondActivity.this)
                .load(intent.getStringExtra("image"))
                .noPlaceholder()
                .fit()
                .centerCrop().into(selectedImage);
        database = FirebaseDatabase.getInstance().getReference("auctions");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    if (Integer.parseInt(item.child("id").getValue().toString()) == intent.getIntExtra("id", 0)) {
                        time = item.child("time").getValue().toString();
                        btnText = item.child("highest_bid").getValue().toString();
                        timer.setText(time);
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss");
                        try {
                            Date futureDate = dateFormat.parse(time);
                            Date currentDate = new Date();
                            new CountDownTimer(Objects.requireNonNull(futureDate).getTime() - currentDate.getTime(), 1000) {
                                @SuppressLint("SetTextI18n")
                                public void onTick(long millisUntilFinished) {
                                    NumberFormat f = new DecimalFormat("00");
                                    long days = (millisUntilFinished / 86400000);
                                    long hour = (millisUntilFinished / 3600000) % 24;
                                    long min = (millisUntilFinished / 60000) % 60;
                                    long sec = (millisUntilFinished / 1000) % 60;
                                    timer.setText("Expires in:");
                                    timer2.setText(f.format(days) + " days " + f.format(hour) + "h :" + f.format(min) + "m :" + f.format(sec) + "s");
                                }

                                @SuppressLint("SetTextI18n")
                                public void onFinish() {
                                    timer.setText("00:00:00");
                                    timer2.setVisibility(View.GONE);
                                }
                            }.start();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        txtName.setText(item.child("name").getValue().toString());
                        btnDescription.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (txtDescription.getVisibility() == View.GONE) {
                                    txtDescription.setVisibility(View.VISIBLE);
                                    txtDescription.setText(item.child("description").getValue().toString());
                                } else {
                                    txtDescription.setVisibility(View.GONE);
                                }

                            }
                        });
                        btnBids.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (txtBids.getVisibility() == View.GONE) {
                                    bidText = "     ---------------";
                                    database2 = FirebaseDatabase.getInstance().getReference("bids").child(String.valueOf(intent.getIntExtra("id", 0)));
                                    database2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot item : snapshot.getChildren()) {
                                                String name = item.child("name").getValue().toString();
                                                String amount = item.child("amount").getValue().toString();
                                                String id = item.child("id").getValue().toString();
                                                SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                                                String idCache = prefs.getString("id", "");
                                                bidText = bidText + "\n" + (name + "          " + amount) + "\n" + "     ---------------";
                                                txtBids.setVisibility(View.VISIBLE);
                                                txtBids.setText(bidText);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                } else {
                                    txtBids.setVisibility(View.GONE);
                                }
                            }
                        });
                        int placingBet = maxOf(Integer.parseInt(btnText) + 1, Integer.parseInt(item.child("price").getValue().toString()) + 1);
                        btnPlace.setText(btnPlace.getText() + "(" + placingBet + ")");
                        btnPlace.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                                String idCache = prefs.getString("id", "");
                                AndroidNetworking.initialize(getApplicationContext());
                                AndroidNetworking.post("http://bidit-web.herokuapp.com/api/auctions/bids/create")
                                        .addBodyParameter("auctionId", String.valueOf(intent.getIntExtra("id", 0)))
                                        .addBodyParameter("bid", String.valueOf(placingBet))
                                        .addBodyParameter("userId", idCache)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                //Toast.makeText(SecondActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onError(ANError anError) {
                                                // Toast.makeText(SecondActivity.this, anError.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                syncAuctionsInfo();
                                ProgressDialog dialog = ProgressDialog.show(SecondActivity.this, "",
                                        "Refreshing...", true);
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SecondActivity.this, "Unexpected error", Toast.LENGTH_SHORT).show();
            }

        });
        timer2.setText(String.valueOf(intent.getIntExtra("id", 0)));

    }

    public int maxOf(int valueOne, int valueTwo) {
        if (valueOne > valueTwo) return valueOne;
        return valueTwo;
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
                                    syncAuctionInfo(String.valueOf(object.getJSONObject("auction").getInt("id")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

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
                            int productId, winnerId, transactionId, highestBid = 0;
                            int id = response.getJSONObject("auction").getInt("id");
                            productId = Integer.parseInt(response.getJSONObject("auction").getString("product_id"));
                            try {
                                winnerId = response.getJSONObject("auction").getInt("winner_id");
                            } catch (JSONException e) {
                                winnerId = 0;
                            }
                            try {
                                transactionId = response.getJSONObject("auction").getInt("transactionId");
                            } catch (JSONException e) {
                                transactionId = 0;
                            }
                            try {
                                JSONObject bid = response.getJSONObject("highestBid");
                                highestBid = bid.getInt("amount");
                            } catch (JSONException e) {
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
                            finish();
                            startActivity(getIntent());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SecondActivity.this, e.toString(),
                                    Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                syncAuctionsInfo();
                ProgressDialog dialog = ProgressDialog.show(SecondActivity.this, "",
                        "Refreshing...", true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}