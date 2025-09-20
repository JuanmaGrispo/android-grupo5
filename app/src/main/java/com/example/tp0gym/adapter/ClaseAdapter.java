package com.example.tp0gym.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp0gym.R;
import com.example.tp0gym.modelo.Clase;

import java.util.List;

public class ClaseAdapter extends RecyclerView.Adapter<ClaseAdapter.ClaseViewHolder> {
    private List<Clase> clases;

    public ClaseAdapter(List<Clase> clases) {
        this.clases = clases;
    }

    @NonNull
    @Override
    public ClaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clase, parent, false);
        return new ClaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClaseViewHolder holder, int position) {
        Clase clase = clases.get(position);
        holder.tvTitle.setText(clase.getTitle());
        holder.tvDiscipline.setText("Disciplina: " + clase.getDiscipline());
        holder.tvDuration.setText("Duración: " + clase.getDefaultDurationMin() + " min");
        holder.tvCapacity.setText("Cupos: " + clase.getDefaultCapacity());
    }

    @Override
    public int getItemCount() {
        return clases.size();
    }

    public void updateList(List<Clase> nuevasClases) {
        this.clases = nuevasClases;
        notifyDataSetChanged();
    }

    public static class ClaseViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDiscipline, tvDuration, tvCapacity;

        public ClaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDiscipline = itemView.findViewById(R.id.tvDiscipline);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
        }
    }
}

