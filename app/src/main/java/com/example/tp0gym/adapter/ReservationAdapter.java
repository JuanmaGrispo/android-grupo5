package com.example.tp0gym.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp0gym.R;
import com.example.tp0gym.ui.model.ReservationUiModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.VH> {

    public interface OnCancelClickListener {
        void onCancel(ReservationUiModel reservation);
    }

    private final List<ReservationUiModel> data = new ArrayList<>();
    private final List<String> loadingIds = new ArrayList<>(); // ids de reserva en loading
    private OnCancelClickListener cancelClickListener;

    public void setOnCancelClickListener(OnCancelClickListener listener) {
        this.cancelClickListener = listener;
    }

    /** Reemplaza toda la lista. */
    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<ReservationUiModel> items) {
        data.clear();
        if (items != null) data.addAll(items);
        loadingIds.clear();
        notifyDataSetChanged();
    }

    /** Devuelve el item en pos, por si lo necesitás. */
    public ReservationUiModel getItem(int position) {
        return data.get(position);
    }

    /** Marca una reserva como "en progreso" (deshabilita botón). */
    public void setLoading(String reservationId, boolean isLoading) {
        if (reservationId == null) return;
        int idx = indexOfReservation(reservationId);
        if (idx == -1) return;

        if (isLoading) {
            if (!loadingIds.contains(reservationId)) loadingIds.add(reservationId);
        } else {
            loadingIds.remove(reservationId);
        }
        notifyItemChanged(idx, "payload_loading");
    }

    /** Actualiza estado de UNA reserva (por ejemplo a "Cancelada"). */
    public void updateStatus(String reservationId, String newStatus) {
        if (reservationId == null) return;
        int idx = indexOfReservation(reservationId);
        if (idx == -1) return;

        ReservationUiModel old = data.get(idx);
        ReservationUiModel updated = old.withStatus(newStatus); // requiere helper en tu UiModel
        data.set(idx, updated);
        loadingIds.remove(reservationId);
        notifyItemChanged(idx);
    }

    /** Elimina una reserva de la lista (opcional). */
    public void removeById(String reservationId) {
        if (reservationId == null) return;
        int idx = indexOfReservation(reservationId);
        if (idx == -1) return;
        data.remove(idx);
        loadingIds.remove(reservationId);
        notifyItemRemoved(idx);
    }

    private int indexOfReservation(String reservationId) {
        for (int i = 0; i < data.size(); i++) {
            if (Objects.equals(data.get(i).getReservationId(), reservationId)) return i;
        }
        return -1;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        bindInternal(h, data.get(position), false);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            // payload parcial (solo loading)
            if (payloads.contains("payload_loading")) {
                h.setLoading(loadingIds.contains(data.get(position).getReservationId()));
                return;
            }
        }
        bindInternal(h, data.get(position), true);
    }

    private void bindInternal(@NonNull VH h, @NonNull ReservationUiModel r, boolean fullBind) {
        if (fullBind) {
            h.title.setText(r.getTitle());
            h.subtitle.setText(r.getSubtitle());
            h.when.setText(r.getFormattedDate());
            h.status.setText(r.getStatus());
        }

        // botón cancelar visible solo si está confirmada
        boolean isConfirmed = "Confirmada".equalsIgnoreCase(r.getStatus())
                || "CONFIRMED".equalsIgnoreCase(r.getStatus());
        h.btnCancel.setVisibility(isConfirmed ? View.VISIBLE : View.GONE);

        // loading por ítem
        boolean isLoading = loadingIds.contains(r.getReservationId());
        h.setLoading(isLoading);

        h.btnCancel.setOnClickListener(v -> {
            if (cancelClickListener == null) return;
            if (isLoading) return; // evitar doble click
            cancelClickListener.onCancel(r);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, subtitle, when, status;
        Button btnCancel;

        VH(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.tvTitle);
            subtitle = v.findViewById(R.id.tvSubtitle);
            when = v.findViewById(R.id.tvWhen);
            status = v.findViewById(R.id.tvStatus);
            btnCancel = v.findViewById(R.id.btnCancel);
        }

        void setLoading(boolean loading) {
            btnCancel.setEnabled(!loading);
            btnCancel.setText(loading ? "Cancelando…" : "Cancelar"); // ← literal
            btnCancel.setAlpha(loading ? 0.6f : 1f);
        }
    }
}