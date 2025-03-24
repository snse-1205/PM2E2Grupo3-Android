package com.example.pm2e2grupo3_android.RetroFit;

import com.example.pm2e2grupo3_android.Models.ContactosModelo;
import com.example.pm2e2grupo3_android.Models.PaisesModelo;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    @GET("contactos/")
    Call<List<ContactosModelo.Contenido>> obtenerContenido();

    @Multipart
    @POST("contactos/")
    Call<Void> crearContacto(
            @Part("nombre")RequestBody nombre,
            @Part("idPais")RequestBody idPais,
            @Part("telefono")RequestBody telefono,
            @Part("latitud")RequestBody latitud,
            @Part("longitud")RequestBody longitud,
            @Part MultipartBody.Part video);

    @GET("paises/")
    Call<List<PaisesModelo.Contenido>> obtenerCodigos();

    //Faltantes

    @GET("contactos/{id}")
    Call<List<ContactosModelo.Contenido1>> obtenerContenidoPorId(@Path("id") int id);

    @Multipart
    @PUT("contactos/{id}")
    Call<Void> actualizarContactos(
            @Path("id") int id,
            @Part("nombre")RequestBody nombre,
            @Part("idPais")RequestBody idPais,
            @Part("telefono")RequestBody telefono,
            @Part("latitud")RequestBody latitud,
            @Part("longitud")RequestBody longitud,
            @Part MultipartBody.Part video);

    @DELETE("contactos/{id}")
    Call<List<ContactosModelo.Contenido1>> eliminarContacto(@Path("id") int id);
}
