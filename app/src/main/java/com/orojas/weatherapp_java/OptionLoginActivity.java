package com.orojas.weatherapp_java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.orojas.weatherapp_java.databinding.ActivityMainBinding;
import com.orojas.weatherapp_java.databinding.ActivityOptionLoginBinding;

public class OptionLoginActivity extends AppCompatActivity {
    private ActivityOptionLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOptionLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionLoginActivity.this, LoginEmailActivity.class));
            }
        });
    }
}