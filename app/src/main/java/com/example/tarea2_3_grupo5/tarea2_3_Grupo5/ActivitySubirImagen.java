package com.example.tarea2_3_grupo5.tarea2_3_Grupo5;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.tarea2_3_grupo5.R;
import com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Config.SQLiteConnection;
import com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Config.Transacciones;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ActivitySubirImagen extends AppCompatActivity {

    SQLiteConnection conexion;
    static final int peticion_acceso_camera = 101;
    static final int peticion_toma_fotografia = 102;
    private static final int REQUEST_CODE = 123;
    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView areaFoto;
    Button tomarImagen, seleccionarImagen, salvarImagen, regresarSubir;
    EditText descripcion;
    byte[] imagenByteArray = null;
    Bitmap imageBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_imagen);

        conexion = new SQLiteConnection(this, Transacciones.namedb, null, 1);
        areaFoto = (ImageView) findViewById(R.id.imageviewFoto);
        tomarImagen = (Button) findViewById(R.id.btnTomarImagen);
        seleccionarImagen = (Button) findViewById(R.id.btnSeleccionarImagen);
        salvarImagen = (Button) findViewById(R.id.btnSalvar);
        descripcion = (EditText) findViewById(R.id.txtDescripcion);
        regresarSubir = (Button) findViewById(R.id.btnRegresarSubir);

        tomarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisos();
            }
        });

        seleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ActivitySubirImagen.this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    // Si el permiso no es otrogado, lo pide
                    ActivityCompat.requestPermissions(ActivitySubirImagen.this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE);
                } else {
                    // Crea el intent para seleccionar la imagen
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                }
            }
        });

        salvarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(descripcion.getText().toString().trim().isEmpty()){
                    descripcion.setError("Porfavor ingrese una descripción para la imagen, no se permiten campos vacios!!!");
                } else if (imagenByteArray == null) {
                    Toast.makeText(getApplicationContext(), "Porfavor tome una foto o seleccione una foto a subir!", Toast.LENGTH_LONG).show();
                }else{
                    addImagen();
                }
            }
        });

        View.OnClickListener buttonClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class<?> actividad = null;
                if (view.getId()==R.id.btnRegresarSubir) {
                    actividad = MainActivity.class;
                }
                if (actividad != null) {
                    moveActivity(actividad);
                }
            }
        };

        regresarSubir.setOnClickListener(buttonClick);
    }

    private void moveActivity(Class<?> actividad) {
        Intent intent = new Intent(getApplicationContext(), actividad);
        startActivity(intent);
    }

    private void permisos() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, peticion_acceso_camera);
        } else {
            tomarFoto();
        }
    }

    private void tomarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, peticion_toma_fotografia);
        }
    }

    //override al metodo para procesar la seleccion de la imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            // Actualiza el imageview para colocar la imagen seleccionada
            areaFoto.setImageURI(selectedImageUri);

            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                imagenByteArray = getBytes(inputStream);
                // La imagen ahora se encuentra en forma de byte array dentro de la variable byteArray para poder guardarse en la base de datos
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == peticion_toma_fotografia && resultCode == RESULT_OK) {
            try {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                areaFoto.setImageBitmap(imageBitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imagenByteArray = stream.toByteArray();

            }catch (Exception ex){
                ex.toString();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Crea el intent para seleccionar la imagen
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            } else {
                showPermissionExplanation();
            }
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void showPermissionExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permiso Requerido");
        builder.setMessage("Para acceder a tu galería y seleccionar una imagen, necesitamos el permiso de almacenamiento. Por favor, otorga el permiso en la configuración de la aplicación.");
        builder.setPositiveButton("Ir a Ajustes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Abre los ajustes de la aplicacion
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void addImagen() {
        try {

            SQLiteDatabase db = conexion.getWritableDatabase();

            ContentValues valores = new ContentValues();
            valores.put(Transacciones.descripcion, descripcion.getText().toString());
            valores.put(Transacciones.foto, imagenByteArray);

            Long result = db.insert(Transacciones.tablaImagenes, Transacciones.id, valores);

            Toast.makeText(this, getString(R.string.respuesta), Toast.LENGTH_SHORT).show();
            db.close();
            areaFoto.setImageResource(R.drawable.uploadimage);
            descripcion.setText("");
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.errorIngreso), Toast.LENGTH_SHORT).show();
        }
    }
}