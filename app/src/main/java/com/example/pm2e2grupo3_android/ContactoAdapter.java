package com.example.pm2e2grupo3_android;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pm2e2grupo3_android.Models.ContactosModelo;

import java.util.List;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder> {

    private List<ContactosModelo.Contenido> contactos; // Lista de contactos
    private Context context; // Contexto para inflar vistas y mostrar diálogos

    // Constructor
    public ContactoAdapter(List<ContactosModelo.Contenido> contactos, Context context) {
        this.contactos = contactos;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el diseño de cada elemento de la lista (item_contacto.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_contacto, parent, false);
        return new ContactoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        // Obtener el contacto en la posición actual
        ContactosModelo.Contenido contenido = contactos.get(position);

        holder.tvNombre.setText(contenido.nombre);
        String Telefono = "+"+contenido.codigo+" "+contenido.telefono;
        holder.txTelefono.setText(Telefono);

        holder.itemView.setOnClickListener(v -> {
            mostrarAlertDialog();
        });
    }

    @Override
    public int getItemCount() {
        // Devolver el número de contactos en la lista
        return contactos.size();
    }

    // ViewHolder para ContactoAdapter
    public static class ContactoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre; // TextView para mostrar el nombre del contacto
        TextView txTelefono;
        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Vincular el TextView con el diseño
            tvNombre = itemView.findViewById(R.id.tvNombre);
            txTelefono = itemView.findViewById(R.id.txtTelefono);
        }
    }

    // Método para mostrar un diálogo con opciones al hacer clic en un contacto
    private void mostrarAlertDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("Acción para contacto")
                .setMessage("¿Qué acción deseas realizar?")
                .setPositiveButton("Ubicación", (dialog, which) -> {
                    Intent intent = new Intent(context, MapScreenActivity.class);
                    context.startActivity(intent);
                })
                .setNeutralButton("Actualizar", (dialog, which) -> {
                    Intent intent = new Intent(context, Pantalla2Activity.class);
                    context.startActivity(intent);
                })
                .setNegativeButton("Eliminar", (dialog, which) -> {
                    // Acción para eliminar
                })
                .show();
    }
}