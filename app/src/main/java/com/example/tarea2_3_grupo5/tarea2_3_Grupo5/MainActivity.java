package com.example.tarea2_3_grupo5.tarea2_3_Grupo5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tarea2_3_grupo5.R;

public class MainActivity extends AppCompatActivity {

    Button subirImagen, verImagenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subirImagen = (Button) findViewById(R.id.btnSubirImagen);
        verImagenes = (Button) findViewById(R.id.btnVerImagenes);

        View.OnClickListener buttonClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class<?> actividad = null;
                if (view.getId()==R.id.btnSubirImagen) {
                    actividad = ActivitySubirImagen.class;
                }
                if (view.getId()==R.id.btnVerImagenes) {
                    actividad = ActivityVerImagenes.class;
                }
                if (actividad != null) {
                    moveActivity(actividad);
                }
            }
        };

        subirImagen.setOnClickListener(buttonClick);
        verImagenes.setOnClickListener(buttonClick);
    }

    private void moveActivity(Class<?> actividad) {
        Intent intent = new Intent(getApplicationContext(), actividad);
        startActivity(intent);
    }
}