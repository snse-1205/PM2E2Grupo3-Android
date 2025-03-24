package com.example.pm2e2grupo3_android.Models;

public class ContactosModelo {
    int id;
    String Nombre;
    String CodigoPais;
    String Telefono;
    Double Latitud;
    Double Longitud;

    public ContactosModelo() {
    }

    public ContactosModelo(int id, String nombre, String codigoPais, String telefono, Double latitud, Double longitud) {
        this.id = id;
        Nombre = nombre;
        CodigoPais = codigoPais;
        Telefono = telefono;
        Latitud = latitud;
        Longitud = longitud;
    }

    public Double getLongitud() {
        return Longitud;
    }

    public void setLongitud(Double longitud) {
        Longitud = longitud;
    }

    public Double getLatitud() {
        return Latitud;
    }

    public void setLatitud(Double latitud) {
        Latitud = latitud;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public String getCodigoPais() {
        return CodigoPais;
    }

    public void setCodigoPais(String codigoPais) {
        CodigoPais = codigoPais;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
