package com.example.tp0gym.ui.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp0gym.R;
import com.example.tp0gym.adapter.ReservationAdapter;

import java.util.Arrays;

public class ReservationFragment extends Fragment {

    private RecyclerView rvReservations;         // La lista en pantalla
    private ReservationAdapter adapter;          // El puente datos <-> UI

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 1. Inflamos el layout del fragment (fragment_reservations.xml)
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        // 2. Encontramos el RecyclerView dentro del layout
        rvReservations = view.findViewById(R.id.recycler);

        // 3. Le damos un layout manager (definir cómo se muestran los ítems: en lista vertical)
        rvReservations.setLayoutManager(new LinearLayoutManager(getContext()));

        // 4. Creamos el adapter y se lo enchufamos al RecyclerView
        adapter = new ReservationAdapter();
        rvReservations.setAdapter(adapter);

        // 5. (TEMPORAL) Cargar datos de prueba para ver que todo funciona
        adapter.setItems(Arrays.asList(
                new ReservationAdapter.ReservationUi("Yoga", "Mind & Body", "2025-10-01 10:00", "Sede A", "Confirmada"),
                new ReservationAdapter.ReservationUi("Crossfit", "Fuerza", "2025-10-02 18:00", "Sede B", "Cancelada"),
                new ReservationAdapter.ReservationUi("Pilates", "Flexibilidad", "2025-10-03 09:00", "Sede A", "Confirmada")
        ));

        return view;
    }
}