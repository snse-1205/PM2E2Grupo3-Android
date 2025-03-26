package com.example.pm2e2grupo3_android.Activities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pm2e2grupo3_android.Models.ContactosModelo;
import com.example.pm2e2grupo3_android.RetroFit.ApiService;
import com.example.pm2e2grupo3_android.RetroFit.RetroFitClient;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactosViewModels extends ViewModel {
    private List<ContactosModelo.Contenido> contactos;
    private final MutableLiveData<List<ContactosModelo.Contenido>> contactosLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeLiveData = new MutableLiveData<>();
    private final ApiService apiService = RetroFitClient.getRetrofitInstance().create(ApiService.class);
    private final MutableLiveData<Boolean> contactoGuardadoLiveData = new MutableLiveData<>();

    public LiveData<Boolean> getContactoGuardadoLiveData() {
        return contactoGuardadoLiveData;
    }
    public void cargarContactos() {
        contactos = new ArrayList<>();
        Call<List<ContactosModelo.Contenido>> call = apiService.obtenerContenido();
        call.enqueue(new Callback<List<ContactosModelo.Contenido>>() {
            @Override
            public void onResponse(Call<List<ContactosModelo.Contenido>> call, Response<List<ContactosModelo.Contenido>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contactos.clear();
                    contactos.addAll(response.body());

                    contactosLiveData.setValue(contactos);
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

    public void cargarContactos(String nombres) {
        contactos = new ArrayList<>();
        Call<List<ContactosModelo.Contenido>> call = apiService.buscarContactos(nombres);
        call.enqueue(new Callback<List<ContactosModelo.Contenido>>() {
            @Override
            public void onResponse(Call<List<ContactosModelo.Contenido>> call, Response<List<ContactosModelo.Contenido>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contactos.clear();
                    contactos.addAll(response.body());

                    contactosLiveData.setValue(contactos);
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

    public LiveData<List<ContactosModelo.Contenido>> getContactosLiveData() {
        return contactosLiveData;
    }

    public LiveData<String> getMensajeLiveData() {
        return mensajeLiveData; // Para observar los mensajes de estado
    }

    public void eliminarElemento(int id){
        if(id==-1){
            return;
        }
        Call<Void> call = apiService.eliminarContacto(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    List<ContactosModelo.Contenido> listaActual = contactosLiveData.getValue();
                    if (listaActual != null) {
                        listaActual.removeIf(contacto -> contacto.id == id);
                        contactosLiveData.setValue(listaActual);
                    }
                    Log.d("Retrofit", "Eliminado exitosamente");
                }else{
                    Log.e("Retrofit", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Retrofit", "Fallo en la solicitud: " + t.getMessage());
            }
        });
    }

    public void guardarContacto(Context context, String nombres, String telefonos, String latituds, String longituds, String codigoPais, String videoPath) {
        if (nombres.isEmpty() || telefonos.isEmpty() || latituds.isEmpty() || longituds.isEmpty()) {
            Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            mensajeLiveData.setValue("Por favor, complete todos los campos");
            return;
        }
        if (videoPath == null || videoPath.isEmpty()) {
            Toast.makeText(context, "No se pudo obtener la ruta del video", Toast.LENGTH_SHORT).show();
            mensajeLiveData.setValue("No se pudo obtener la ruta del video");
            return;
        }

        File videofile = new File(videoPath);
        if (!videofile.exists()) {
            Toast.makeText(context, "Por favor, tome un video", Toast.LENGTH_SHORT).show();
            mensajeLiveData.setValue("Por favor, tome un video");
            return;
        }
        RequestBody nombre = RequestBody.create(MediaType.parse("text/plain"),nombres);
        RequestBody idPais = RequestBody.create(MediaType.parse("text/plain"),codigoPais);
        RequestBody telefono = RequestBody.create(MediaType.parse("text/plain"),telefonos);
        RequestBody latitud = RequestBody.create(MediaType.parse("text/plain"),latituds);
        RequestBody longitud = RequestBody.create(MediaType.parse("text/plain"),longituds);
        RequestBody RequestFile = RequestBody.create(MediaType.parse("video/mp4"),videofile);
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("video",videofile.getName(),RequestFile);

        Call<Void> call = apiService.crearContacto(nombre,idPais,telefono,latitud,longitud,videoPart);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    contactoGuardadoLiveData.setValue(true);
                    cargarContactos();
                    mensajeLiveData.setValue("Contacto Creado Exitosamente");
                    Log.d("Retrofit", "Contacto Creado Exitosamente :3");
                } else {
                    contactoGuardadoLiveData.setValue(false);
                    mensajeLiveData.setValue("Error: " + response.message());
                    Log.e("Retrofit", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                contactoGuardadoLiveData.setValue(false);
                mensajeLiveData.setValue("Fallo en la solicitud: " + t.getMessage());
                Log.e("Retrofit", "Fallo en la solicitud: " + t.getMessage());
            }
        });

    }

    public void actualizarDatos(int id, Context context, String nombres, String telefonos, String latituds, String longituds, String codigoPais, String videoPath){
        if (nombres.isEmpty() || telefonos.isEmpty() || latituds.isEmpty() || longituds.isEmpty()) {
            mensajeLiveData.setValue("Por favor, complete todos los campos");
            return;
        }
        if (videoPath == null || videoPath.isEmpty()) {
            mensajeLiveData.setValue("Por favor, complete los Campos de Video");
            return;
        }

        File videofile = new File(videoPath);
        if (!videofile.exists()) {
            mensajeLiveData.setValue("Por favor, complete todos los Campos de Video");
            return;
        }
        RequestBody nombre = RequestBody.create(MediaType.parse("text/plain"),nombres);
        RequestBody idPais = RequestBody.create(MediaType.parse("text/plain"),codigoPais);
        RequestBody telefono = RequestBody.create(MediaType.parse("text/plain"),telefonos);
        RequestBody latitud = RequestBody.create(MediaType.parse("text/plain"),latituds);
        RequestBody longitud = RequestBody.create(MediaType.parse("text/plain"),longituds);
        RequestBody RequestFile = RequestBody.create(MediaType.parse("video/mp4"),videofile);
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("video",videofile.getName(),RequestFile);

        Call<Void> call = apiService.actualizarContactos(id, nombre,idPais,telefono,latitud,longitud,videoPart);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    contactoGuardadoLiveData.setValue(true);
                    cargarContactos();
                    mensajeLiveData.setValue("Contacto Actualizado Exitosamente");
                    Log.d("Retrofit", "Contacto Actualizado Exitosamente :3");
                }else{
                    contactoGuardadoLiveData.setValue(false);
                    mensajeLiveData.setValue("Error: " + response.message());
                    Log.e("Retrofit", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                contactoGuardadoLiveData.setValue(false);
                mensajeLiveData.setValue("Fallo en la solicitud: " + t.getMessage());
                Log.e("Retrofit", "Fallo en la solicitud: " + t.getMessage());
            }
        });
    }

    public void actualizarDatos(int id, Context context, String nombres, String telefonos, String latituds, String longituds, String codigoPais){
        if (nombres.isEmpty() || telefonos.isEmpty() || latituds.isEmpty() || longituds.isEmpty()) {
            mensajeLiveData.setValue("Por favor, complete todos los campos");
            return;
        }
        RequestBody nombre = RequestBody.create(MediaType.parse("text/plain"),nombres);
        RequestBody idPais = RequestBody.create(MediaType.parse("text/plain"),codigoPais);
        RequestBody telefono = RequestBody.create(MediaType.parse("text/plain"),telefonos);
        RequestBody latitud = RequestBody.create(MediaType.parse("text/plain"),latituds);
        RequestBody longitud = RequestBody.create(MediaType.parse("text/plain"),longituds);

        Call<Void> call = apiService.actualizarContactos(id, nombre,idPais,telefono,latitud,longitud);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    contactoGuardadoLiveData.setValue(true);
                    cargarContactos();
                    mensajeLiveData.setValue("Contacto Actualizado Exitosamente");
                    Log.d("Retrofit", "Contacto Actualizado Exitosamente :3");
                }else{
                    contactoGuardadoLiveData.setValue(false);
                    mensajeLiveData.setValue("Error: " + response.message());
                    Log.e("Retrofit", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                contactoGuardadoLiveData.setValue(false);
                mensajeLiveData.setValue("Fallo en la solicitud: " + t.getMessage());
                Log.e("Retrofit", "Fallo en la solicitud: " + t.getMessage());
            }
        });
    }

}
