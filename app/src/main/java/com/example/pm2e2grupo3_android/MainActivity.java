package com.example.pm2e2grupo3_android;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pm2e2grupo3_android.Activities.ContactosViewModels;
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
    private ContactosViewModels contactoViewModel;
    private List<ContactosModelo.Contenido> contactos;
    private ApiService apiService = RetroFitClient.getRetrofitInstance().create(ApiService.class);
    private EditText searchView;
    private Button btnAgregar, btnBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.buscar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnAgregar = findViewById(R.id.btnAgregar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactoViewModel = new ContactosViewModels();
        adapter = new ContactoAdapter(new ArrayList<>(), this,contactoViewModel);
        recyclerView.setAdapter(adapter); // Asignar el adaptador primero

        contactoViewModel.getContactosLiveData().observe(this, adapter::actualizarLista);
        contactoViewModel.cargarContactos();

        btnBuscar.setOnClickListener(v -> {
            String nombre = searchView.getText().toString();
            if(nombre.isEmpty()){
                contactoViewModel.getContactosLiveData().observe(this, adapter::actualizarLista);
                contactoViewModel.cargarContactos();
            }else{
                contactoViewModel.getContactosLiveData().observe(this, adapter::actualizarLista);
                contactoViewModel.cargarContactos(nombre);
            }
        });

        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Pantalla2Activity.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            contactoViewModel.getContactosLiveData().observe(this, adapter::actualizarLista);
            contactoViewModel.cargarContactos();
        }
        if(requestCode==2 && resultCode==RESULT_OK){
            contactoViewModel.getContactosLiveData().observe(this, adapter::actualizarLista);
            contactoViewModel.cargarContactos();
        }
    }
}
