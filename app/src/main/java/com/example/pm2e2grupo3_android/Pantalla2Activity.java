package com.example.pm2e2grupo3_android;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class Pantalla2Activity extends AppCompatActivity {



    private VideoView videoView;
    private CardView btnGrabar, btnGuardar;
    private EditText etNombre, etTelefono, etLatitud, etLongitud;
    private Spinner spinnerCodigoPais;
    private MediaRecorder mediaRecorder;
    private boolean grabando = false;
    private static final int REQUEST_AUDIO_PERMISSION = 1001;
    private String archivoSalida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video);


        // Verificar y solicitar permisos al iniciar la actividad
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_AUDIO_PERMISSION);
        }

        // Inicialización de vistas
        videoView = findViewById(R.id.videoView);
        btnGrabar = findViewById(R.id.cardCapturar);
        btnGuardar = findViewById(R.id.cardGuardar);
        etNombre = findViewById(R.id.txtNombreContacto);
        etTelefono = findViewById(R.id.txtNumeroTelefonico);
        etLatitud = findViewById(R.id.txtLatitud);
        etLongitud = findViewById(R.id.txtLongitud);
        spinnerCodigoPais = findViewById(R.id.spinnerTelefono);

        // Evento del botón de grabar
        btnGrabar.setOnClickListener(view -> {
            if (grabando) {
                detenerGrabacion();
            } else {
                solicitarPermisosYGrabar();
            }
        });

        // Evento del botón de guardar
        btnGuardar.setOnClickListener(view -> guardarContacto());
    }

    private void solicitarPermisosYGrabar() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION);
        } else {
            iniciarGrabacion();
        }
    }

    private void iniciarGrabacion() {
        try {
            archivoSalida = getExternalFilesDir(null).getAbsolutePath() + "/grabacion.3gp";
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(archivoSalida);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
            grabando = true;
            //btnGrabar.setText("Detener");
            Toast.makeText(this, "Grabando audio...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al iniciar grabación", Toast.LENGTH_SHORT).show();
        }
    }

    private void detenerGrabacion() {
        if (grabando) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            grabando = false;
            //btnGrabar.setText("Grabar");
            Toast.makeText(this, "Grabación guardada en: " + archivoSalida, Toast.LENGTH_LONG).show();
        }
    }

    private void guardarContacto() {
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String latitud = etLatitud.getText().toString().trim();
        String longitud = etLongitud.getText().toString().trim();
        String codigoPais = spinnerCodigoPais.getSelectedItem().toString();

        if (nombre.isEmpty() || telefono.isEmpty() || latitud.isEmpty() || longitud.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Contacto guardado: " + nombre, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarGrabacion();
            } else {
                Toast.makeText(this, "Permiso denegado para grabar audio", Toast.LENGTH_SHORT).show();
            }
        }
    }
}