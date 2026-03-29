package com.compmovil.ejemplo01.controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.compmovil.ejemplo01.R;

public class MainActivity extends AppCompatActivity {

    EditText nombre, apellido;
    Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // init component
        btnEnviar = findViewById(R.id.btnEnviar);
        nombre = findViewById(R.id.etiquetaNombre);
        apellido = findViewById(R.id.etiquetaApellido);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nombre.getText().toString().isEmpty()){
                    nombre.setError("Falta este campo");
                }else{
                    if(apellido.getText().toString().isEmpty()){
                        apellido.setError("Falta este campo");
                    } else {
                        Toast.makeText(getBaseContext(), "almacenado", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }


}