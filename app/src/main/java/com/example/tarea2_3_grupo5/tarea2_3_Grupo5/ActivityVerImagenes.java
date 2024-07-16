package com.example.tarea2_3_grupo5.tarea2_3_Grupo5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.example.tarea2_3_grupo5.R;
import com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Adapters.CustomAdapterImagenes;
import com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Config.SQLiteConnection;
import com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Config.Transacciones;
import com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Models.imageItem;

import java.util.ArrayList;
import java.util.List;

public class ActivityVerImagenes extends AppCompatActivity {
    SQLiteConnection conexion;
    RecyclerView lista;
    Button regresar;
    List<imageItem> dataList;
    CustomAdapterImagenes adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_imagenes);

        conexion = new SQLiteConnection(this, Transacciones.namedb, null, 1);
        lista = (RecyclerView) findViewById(R.id.recyclerViewListaImagenes);
        regresar = (Button) findViewById(R.id.btnRegresar);
        dataList = new ArrayList<>();

        getImagenes();

        // Configuración del administrador de diseño y adaptador para el RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        lista.setLayoutManager(layoutManager);
        adapter = new CustomAdapterImagenes(this, dataList);
        lista.setAdapter(adapter);

        View.OnClickListener buttonClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class<?> actividad = null;
                if (view.getId()==R.id.btnRegresar) {
                    actividad = MainActivity.class;
                }
                if (actividad != null) {
                    moveActivity(actividad);
                }
            }
        };

        regresar.setOnClickListener(buttonClick);
    }

    private void getImagenes() {
        try {
            SQLiteDatabase db = conexion.getReadableDatabase();
            dataList.clear(); // Clear the list to avoid duplicates

            Cursor cursor = db.rawQuery(Transacciones.SelectTableImagenes, null);

            while (cursor.moveToNext()) {
                String description = cursor.getString(cursor.getColumnIndex(Transacciones.descripcion));
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(Transacciones.foto));
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                dataList.add(new imageItem(imageBitmap, description));
            }

            cursor.close();

            adapter.notifyDataSetChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void moveActivity(Class<?> actividad) {
        Intent intent = new Intent(getApplicationContext(), actividad);
        startActivity(intent);
    }
}