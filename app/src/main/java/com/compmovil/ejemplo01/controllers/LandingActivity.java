package com.compmovil.ejemplo01.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.compmovil.ejemplo01.R;

public class LandingActivity extends AppCompatActivity {

    Button sessionBtn, registerBtn;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        sessionBtn = findViewById(R.id.btn_iniciar_sesion);
        registerBtn = findViewById(R.id.btn_registrarse);

        sessionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LandingActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
