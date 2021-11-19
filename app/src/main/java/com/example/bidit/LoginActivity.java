package com.example.bidit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView email = (TextView) findViewById(R.id.email);
        TextView password = (TextView) findViewById(R.id.password);

        MaterialButton login_button = (MaterialButton) findViewById(R.id.login_button);

        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(email.getText().toString().equals("admin") && password.getText().toString().equals("password")){
                    Toast.makeText(LoginActivity.this, "LOGIN SUCCESFUL",Toast.LENGTH_SHORT).show();
                    Intent tohome = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(tohome);
                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect username or password",Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView register = findViewById(R.id.register_here);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Redirecting to BidIT register page", Toast.LENGTH_SHORT);
                Intent browserRegister = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bidit-web.herokuapp.com/register"));
                startActivity(browserRegister);
            }
        });
    }
}