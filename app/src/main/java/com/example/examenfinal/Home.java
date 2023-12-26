package com.example.examenfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity {

    Button buttonBackH, buttonRegistroH, buttonHistorialH, buttonMapaH, buttonRegistroHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        buttonBackH = findViewById(R.id.buttonBackHome);
        buttonRegistroH = findViewById(R.id.buttonRegistroHome);
        buttonHistorialH = findViewById(R.id.buttonHistorialHome);
        buttonMapaH = findViewById(R.id.buttonMapaHome);
        buttonRegistroHistorial= findViewById(R.id.buttonRegistroHistorial);

        buttonBackH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, MainActivity.class);
                startActivity(intent);
            }
        });
        buttonRegistroHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, RegistroHistorial.class);
                startActivity(intent);
            }
        });


        buttonRegistroH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Register.class);
                startActivity(intent);
            }
        });
        buttonHistorialH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,RegistroFecha.class);
                startActivity(intent);
            }
        });

        buttonMapaH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Mapa.class);
                startActivity(intent);
            }
        });


    }
}