package com.example.bidit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference database;
    static boolean check;

    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        syncUserInfo();
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String emailCache = prefs.getString("email", "");
        String nameCache = prefs.getString("name", "");
        String idCache = prefs.getString("id", "");
        if (!emailCache.equals("") && !nameCache.equals("")) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
        TextView email = (TextView) findViewById(R.id.email);
        TextView password = (TextView) findViewById(R.id.password);

        MaterialButton login_button = (MaterialButton) findViewById(R.id.login_button);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "",
                        "Please wait...", true);
                check = false;
                database = FirebaseDatabase.getInstance().getReference("users");
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot item : snapshot.getChildren()) {
                            String emailTemp = item.child("email").getValue().toString();
                            String passTemp = item.child("password").getValue().toString();
                            BCrypt.Result result = BCrypt.verifyer().verify(password.getText().toString().toCharArray(), passTemp);
                            if (emailTemp.equals(email.getText().toString().trim()) && result.verified) {
                                check = true;
                                SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("email", emailTemp);
                                editor.putString("name", item.child("name").getValue().toString());
                                editor.putString("id", item.child("id").getValue().toString());
                                editor.apply();
                                Toast.makeText(LoginActivity.this, "Hello, " + item.child("name").getValue().toString() + "!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                break;
                            }
                        }
                        if (!check) {
                            Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(getIntent());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this, "Unexpected error", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });

        TextView register = findViewById(R.id.register_here);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "Redirecting to BidIT register page", Toast.LENGTH_SHORT).show();
                Intent browserRegister = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bidit-web.herokuapp.com/register"));
                startActivity(browserRegister);
            }
        });
    }

    public void syncUserInfo() {
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.get("http://bidit-web.herokuapp.com/api/users/")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONArray jArray = (JSONArray) response;
                        if (jArray != null) {
                            for (int i = 0; i < jArray.length(); i++) {
                                try {
                                    JSONObject object = jArray.getJSONObject(i);
                                    User user = new User(object.get("name").toString(), object.get("email").toString(), object.get("password").toString(), Integer.parseInt(object.get("id").toString()));
                                    database = FirebaseDatabase.getInstance().getReference();
                                    database.child("users").child(i + "").setValue(user);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}