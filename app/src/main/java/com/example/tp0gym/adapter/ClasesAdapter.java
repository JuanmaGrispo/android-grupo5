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

public class ClasesAdapter extends RecyclerView.Adapter<ClasesAdapter.ClaseViewHolder> {

    private List<Clase> clases;

    public ClasesAdapter(List<Clase> clases) {
        this.clases = clases;
    }

    @NonNull
    @Override
    public ClaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clase, parent, false);
        return new ClaseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ClaseViewHolder holder, int position) {
        Clase c = clases.get(position);
        holder.tvTitle.setText(c.getTitle());
        holder.tvDescription.setText(c.getDescription() != null ? c.getDescription() : "");
        holder.tvDuration.setText("Duración: " + c.getDefaultDurationMin() + " min");
        holder.tvCapacity.setText("Cupos: " + c.getDefaultCapacity());
        String loc = c.getLocationName() != null ? c.getLocationName() : c.getLocationAddress();
        holder.tvLocation.setText(loc != null ? loc : "Sin ubicación");
    }

    @Override
    public int getItemCount() {
        return clases == null ? 0 : clases.size();
    }

    static class ClaseViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDuration, tvCapacity, tvLocation;
        public ClaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }
    }
    public void updateData(List<Clase> nuevasClases) {
        this.clases = nuevasClases;
        notifyDataSetChanged();
    }

}
