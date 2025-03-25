package com.example.pm2e2grupo3_android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapScreenActivity extends AppCompatActivity {

    private MapView mapView;
    private GoogleMap googleMap;
    private Button btnRegresar, btnTrazarRuta;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private double latitude;
    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("latitud") && intent.hasExtra("longitud")) {
            latitude = intent.getDoubleExtra("latitud", 0.0);
            longitude = intent.getDoubleExtra("longitud", 0.0);
        } else {
            Log.e("MapScreenActivity", "No se recibieron las coordenadas");
        }

        mapView = findViewById(R.id.mapView);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnTrazarRuta = findViewById(R.id.btnTrazarRuta);

        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        MapsInitializer.initialize(getApplicationContext());

        // Inicializar el mapa
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                habilitarUbicacion();
                LatLng destino = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(destino).title("Destino"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, 15));
            }
        });

        btnRegresar.setOnClickListener(v -> finish());

        btnTrazarRuta.setOnClickListener(v -> {
            // Aquí puedes agregar lógica para trazar ruta
        });

        // Solicitar permisos si no están otorgados
        if (!tienePermisosUbicacion()) {
            solicitarPermisosUbicacion();
        }
    }

    private boolean tienePermisosUbicacion() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void solicitarPermisosUbicacion() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void habilitarUbicacion() {
        if (tienePermisosUbicacion() && googleMap != null) {
            try {
                googleMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                habilitarUbicacion();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}



