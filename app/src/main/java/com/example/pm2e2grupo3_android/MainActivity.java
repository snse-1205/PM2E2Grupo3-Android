package com.example.pm2e2grupo3_android;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactoAdapter adapter;
    private List<String> contactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar la lista de contactos
        contactos = new ArrayList<>();
        contactos.add("Jose Escalante");
        contactos.add("Jose de Jesus Sarmiento");
        contactos.add("Maña Lopez");

        // Configurar el adaptador
        adapter = new ContactoAdapter(contactos, this);
        recyclerView.setAdapter(adapter);

        // Botón para agregar contacto
        Button btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            // Aquí puedes agregar lógica para agregar un nuevo contacto
        });
    }
}
