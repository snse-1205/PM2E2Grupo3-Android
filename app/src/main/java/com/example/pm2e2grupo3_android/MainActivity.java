package com.example.pm2e2grupo3_android;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pm2e2grupo3_android.Models.ContactosModelo;
import com.example.pm2e2grupo3_android.RetroFit.ApiService;
import com.example.pm2e2grupo3_android.RetroFit.RetroFitClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactoAdapter adapter;
    private List<ContactosModelo.Contenido> contactos;
    private ApiService apiService = RetroFitClient.getRetrofitInstance().create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactos = new ArrayList<>(); // Inicializar lista vacía
        adapter = new ContactoAdapter(contactos, this);
        recyclerView.setAdapter(adapter); // Asignar el adaptador primero

        cargarContactos();

        // Botón para agregar contacto
        Button btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Pantalla2Activity.class);
            startActivityForResult(intent, 1); // Usamos requestCode = 1
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Se guardó un contacto, recargar la lista
            cargarContactos();
        }
    }


    private void cargarContactos() {
        Call<List<ContactosModelo.Contenido>> call = apiService.obtenerContenido();
        call.enqueue(new Callback<List<ContactosModelo.Contenido>>() {
            @Override
            public void onResponse(Call<List<ContactosModelo.Contenido>> call, Response<List<ContactosModelo.Contenido>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contactos.clear(); // Limpiar datos previos si hay
                    contactos.addAll(response.body()); // Agregar nuevos datos
                    adapter.notifyDataSetChanged(); // Notificar al RecyclerView
                } else {
                    Log.e("Retrofit", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<ContactosModelo.Contenido>> call, Throwable t) {
                Log.e("Retrofit", "Fallo en la solicitud: " + t.getMessage());
            }
        });
    }

}
