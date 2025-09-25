package com.example.tp0gym.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp0gym.R;

import java.util.ArrayList;
import java.util.List;

// Adapter: puente entre la lista de datos (reservas) y el RecyclerView (UI que las muestra)
public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.VH> {

    // Modelo simplificado para representar una reserva en la UI
    // ⚡️ En un caso real usarías tu modelo "Reservation"
    public static class ReservationUi {
        public String title, discipline, when, location, status;

        // Constructor rápido para instanciar reservas de prueba
        public ReservationUi(String t, String d, String w, String l, String s) {
            title = t;
            discipline = d;
            when = w;
            location = l;
            status = s;
        }
    }

    // Lista interna de datos que el RecyclerView va a mostrar
    private final List<ReservationUi> data = new ArrayList<>();

    // Método para actualizar los datos desde afuera (ej: un Fragment)
    public void setItems(List<ReservationUi> items) {
        data.clear();                   // Limpia los datos anteriores
        if (items != null) data.addAll(items); // Carga los nuevos
        notifyDataSetChanged();         // 🚨 Le avisa al RecyclerView que tiene que redibujar
    }

    // Se llama SOLO cuando hay que crear un nuevo item (celda)
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // "Inflar" = convertir el XML del item (item_reservation.xml) en un objeto View
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);

        // Devuelve un ViewHolder, que contendrá referencias a los TextViews
        return new VH(v);
    }

    // Se llama cada vez que una celda entra en pantalla → vincula datos con la vista
    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ReservationUi r = data.get(position); // Obtiene la reserva en esa posición
        // Mete los valores en los TextViews
        h.title.setText(r.title);
        h.discipline.setText(r.discipline);
        h.when.setText(r.when);
        h.location.setText(r.location);
        h.status.setText(r.status);
    }

    // Devuelve la cantidad de elementos totales → define cuántas filas va a tener la lista
    @Override
    public int getItemCount() {
        return data.size();
    }

    // ViewHolder = cache de vistas: guarda referencias a los TextView del XML
    // Así no tenés que buscarlos con findViewById en cada actualización → más performance
    static class VH extends RecyclerView.ViewHolder {
        TextView title, discipline, when, location, status;

        VH(@NonNull View v) {
            super(v);
            // Asocia cada variable con el TextView del XML item_reservation.xml
            title = v.findViewById(R.id.tvTitle);
            discipline = v.findViewById(R.id.tvDiscipline);
            when = v.findViewById(R.id.tvWhen);
            location = v.findViewById(R.id.tvLocation);
            status = v.findViewById(R.id.tvStatus);
        }
    }
}