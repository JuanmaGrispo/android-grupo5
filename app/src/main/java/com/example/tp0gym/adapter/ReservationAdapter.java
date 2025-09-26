package com.example.tp0gym.adapter;

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

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.VH> {

    private final List<ReservationUiModel> data = new ArrayList<>();
    private OnCancelClickListener cancelClickListener;

    public interface OnCancelClickListener {
        void onCancel(ReservationUiModel reservation);
    }

    public void setOnCancelClickListener(OnCancelClickListener listener) {
        this.cancelClickListener = listener;
    }

    public void setItems(List<ReservationUiModel> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
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
        ReservationUiModel r = data.get(position);

        h.title.setText(r.getTitle());
        h.subtitle.setText(r.getSubtitle());
        h.when.setText(r.getFormattedDate());
        h.status.setText(r.getStatus());

        h.btnCancel.setOnClickListener(v -> {
            if (cancelClickListener != null) cancelClickListener.onCancel(r);
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
    }
}