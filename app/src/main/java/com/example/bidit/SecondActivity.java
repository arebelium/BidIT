package com.example.bidit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


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
        selectedImage.setImageResource(intent.getIntExtra("image", 0));
        textView.setText(String.valueOf(intent.getIntExtra("id",0)));
    }
}