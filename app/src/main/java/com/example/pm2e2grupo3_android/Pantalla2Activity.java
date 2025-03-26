package com.example.pm2e2grupo3_android;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.pm2e2grupo3_android.Activities.ContactosViewModels;
import com.example.pm2e2grupo3_android.Activities.Utils;
import com.example.pm2e2grupo3_android.Models.ContactosModelo;
import com.example.pm2e2grupo3_android.Models.PaisesModelo;
import com.example.pm2e2grupo3_android.RetroFit.ApiService;
import com.example.pm2e2grupo3_android.RetroFit.RetroFitClient;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pantalla2Activity extends AppCompatActivity {
    private VideoView videoView;
    private CardView btnGrabar, btnGuardar;
    private EditText etNombre, etTelefono, etLatitud, etLongitud;
    private Spinner spinnerCodigoPais;
    private MediaRecorder mediaRecorder;
    private boolean grabando = false;
    private static final int REQUEST_AUDIO_PERMISSION = 1001;
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private String archivoSalida;
    private static Uri videoUri;
    private List<PaisesModelo.Contenido> categoriaList;
    private List<PaisesModelo.Contenido> paises;
    private int idPais;
    private ApiService apiService = RetroFitClient.getRetrofitInstance().create(ApiService.class);
    private Utils utils;
    private int accion=0;
    private List<ContactosModelo.Contenido1> contactos;
    private static ArrayList<String> codigoPaises;
    private ContactosViewModels viewModels;
    private static int id;
    private static int videosiono=0;
    private TextView titulo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video);

        // Inicialización de vistas
        videoView = findViewById(R.id.videoView);
        btnGrabar = findViewById(R.id.cardCapturar);
        btnGuardar = findViewById(R.id.cardGuardar);
        etNombre = findViewById(R.id.txtNombreContacto);
        etTelefono = findViewById(R.id.txtNumeroTelefonico);
        etLatitud = findViewById(R.id.txtLatitud);
        etLongitud = findViewById(R.id.txtLongitud);
        spinnerCodigoPais = findViewById(R.id.spinnerTelefono);
        titulo = findViewById(R.id.textView);

        cargarListaPaises();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("accion") && intent.hasExtra("id")) {
            accion = intent.getIntExtra("accion",0);
            if(accion==2){
                titulo.setText("Editando Registro");
            }
            id = intent.getIntExtra("id",-1);
        } else {
            Log.e("Pantalla Actualizar", "No se recibio el entero");
        }

        viewModels = new ViewModelProvider(this).get(ContactosViewModels.class);

        viewModels.getContactoGuardadoLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isGuardado) {
                if (isGuardado != null && isGuardado) {
                    Intent intent1 = new Intent();
                    setResult(RESULT_OK,intent1);
                    limpiarCampos();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Hubo un error al guardar el contacto", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModels.getMensajeLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String mensaje) {
                if (mensaje != null) {
                    if (!mensaje.equals("Por favor, complete todos los campos")) {
                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if (!checkPermissions()) {
            requestPermissions();
        }

        // Evento del botón de grabar
        btnGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GrabarVideo();
            }
        });

        spinnerCodigoPais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idPais = paises.get(position).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });

        // Evento del botón de guardar
        btnGuardar.setOnClickListener(view -> {
            llenarParaGuardarContacto();
        });

        if(accion==2){
            contactos = new ArrayList<>();
            llenarDatosEnActualizar();
        }

    }

    private void llenarParaGuardarContacto(){
        String nombres = etNombre.getText().toString().trim();
        String telefonos = etTelefono.getText().toString().trim();
        String latituds = etLatitud.getText().toString().trim();
        String longituds = etLongitud.getText().toString().trim();
        String codigoPais = idPais+"";
        String videoPath;
        if(accion==2){
            if(videosiono == 1){
                videoPath = getRealPathFromURI(videoUri);
                viewModels.actualizarDatos(id, this, nombres, telefonos, latituds, longituds, codigoPais, videoPath);
            }else if(videosiono == 0){
                viewModels.actualizarDatos(id, this, nombres, telefonos, latituds, longituds, codigoPais);
            }
        }else{
            videoPath = getRealPathFromURI(videoUri);
            viewModels.guardarContacto(this, nombres, telefonos, latituds, longituds, codigoPais, videoPath);
        }

    }

    private void GrabarVideo() {
        Intent video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(video.resolveActivity(getPackageManager())!=null){
            startActivityForResult(video,REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData(); // Obtener el URI del video
            if (videoUri != null) {
                Log.d("VideoView", "URI del video: " + videoUri.toString()); // Log para verificar
                videosiono=1;
                guardarVideo(data);
            } else {
                videosiono = 0;
                Log.e("VideoView", "No se obtuvo el URI del video.");
                Toast.makeText(this, "Error al capturar el video", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void guardarVideo(Intent data) {
        if (data != null && data.getData() != null) {
            videoUri = data.getData();
            Log.d("VideoView", "Guardando video en: " + videoUri.toString());
            reproducirVideo(videoUri);

            utils = new Utils();
            double latid = utils.getLatitud(Pantalla2Activity.this,Pantalla2Activity.this) ;
            double longi = utils.getLongitud(Pantalla2Activity.this,Pantalla2Activity.this);
            etLatitud.setText(latid+"");
            etLongitud.setText(longi+"");
        } else {
            etLatitud.setText(null);
            etLongitud.setText(null);
            Log.e("VideoView", "Error: No se pudo obtener el video.");
            Toast.makeText(this, "Error al guardar el video", Toast.LENGTH_SHORT).show();
        }
    }

    private void reproducirVideo(Uri uri) {
        videoView.setVideoURI(uri);
        videoView.start();
    }


    private void cargarListaPaises() {
        Call<List<PaisesModelo.Contenido>> call = apiService.obtenerCodigos();
        call.enqueue(new Callback<List<PaisesModelo.Contenido>>() {
            @Override
            public void onResponse(Call<List<PaisesModelo.Contenido>> call, Response<List<PaisesModelo.Contenido>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    paises = response.body(); // Guardamos los países recibidos
                    llenarSpinner(paises); // Llenamos el Spinner cuando los datos estén listos
                } else {
                    Log.e("Retrofit", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<PaisesModelo.Contenido>> call, Throwable t) {
                Log.e("Retrofit", "Fallo en la solicitud: " + t.getMessage());
            }
        });
    }


    private void llenarSpinner(List<PaisesModelo.Contenido> paises) {
        if (paises == null || paises.isEmpty()) {
            Toast.makeText(this, "No hay países disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        codigoPaises = new ArrayList<>();
        for (PaisesModelo.Contenido pais : paises) {
            codigoPaises.add("+" + pais.codigo);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, codigoPaises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCodigoPais.setAdapter(adapter);
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, REQUEST_PERMISSIONS);
    }

    private void llenarDatosEnActualizar(){
        int idContacto = id;
        Log.e("Retrofit", "Id: "+idContacto);
        if(idContacto==-1){
            Log.e("Intent Update", "No se obtuvo el valor");
            return;
        }

        Call<List<ContactosModelo.Contenido1>> call = apiService.obtenerContenidoPorId(idContacto);
        call.enqueue(new Callback<List<ContactosModelo.Contenido1>>() {
            @Override
            public void onResponse(Call<List<ContactosModelo.Contenido1>> call, Response<List<ContactosModelo.Contenido1>> response) {
                if(response.isSuccessful() && response.body() != null){
                    contactos.clear();
                    contactos.addAll(response.body());
                    llenarDatosEnCamposVista(contactos);
                    Log.e("Retrofit", "Llamado exitoso");
                }else{
                    Log.e("Retrofit", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<ContactosModelo.Contenido1>> call, Throwable t) {
                Log.e("Retrofit", "Fallo en la solicitud: " + t.getMessage());
            }
        });
    }

    private void llenarDatosEnCamposVista(List<ContactosModelo.Contenido1> contactos){
        int position = 0;
        ContactosModelo.Contenido1 contenido = contactos.get(position);

        String codigoPredeterminado = "+"+contenido.codigo;
        int posicion = codigoPaises.indexOf(codigoPredeterminado);
        Log.e("Spinner Seleccionar",posicion+"");
        spinnerCodigoPais.setSelection(posicion);

        etNombre.setText(contenido.nombre);
        etTelefono.setText(contenido.telefono);
        etLatitud.setText(contenido.latitud+"");
        etLongitud.setText(contenido.longitud+"");
        String path = "http://35.188.31.71/api/videos/" + contenido.videoContacto;
        Uri videoUri = Uri.parse(path);
        videoView.setVideoURI(videoUri);
        videoView.start();
    }

    private String getRealPathFromURI(Uri contentUri) {
        String result = null;
        String[] proj = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
        }
        return result;
    }

    private void limpiarCampos() {
        etNombre.setText("");
        etTelefono.setText("");
        etLatitud.setText("");
        etLongitud.setText("");
        videoView.setVideoURI(null);
        videoView.stopPlayback();
        videoUri = null;
        idPais = 1;
        spinnerCodigoPais.setSelection(0);
    }


}