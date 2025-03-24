package com.example.pm2e2grupo3_android.Models;

import java.util.List;

public class PaisesModelo {
    int id;
    String nombre;
    String codigo;
    int longitud;

    public class Respuesta{
        List<Contenido> result;
    }

    public class Contenido {
        public int id;
        public int codigo;
        public String longitud;
    }

    public PaisesModelo() {
    }

    public PaisesModelo(int id, String nombre, String codigo, int longitud) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.longitud = longitud;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getLongitud() {
        return longitud;
    }

    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }
}
