package com.example.tp0gym.ui.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp0gym.R;
import com.example.tp0gym.adapter.ReservationAdapter;
import com.example.tp0gym.mappers.ReservationMappers;
import com.example.tp0gym.repository.ReservationsRepository;
import com.example.tp0gym.repository.dto.ReservationDto;
import com.example.tp0gym.ui.model.ReservationUiModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ReservationFragment extends Fragment {

    private RecyclerView rvReservations;
    private ReservationAdapter adapter;

    @Inject
    ReservationsRepository reservationsRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        rvReservations = view.findViewById(R.id.recycler);
        rvReservations.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReservationAdapter();
        rvReservations.setAdapter(adapter);

        adapter.setOnCancelClickListener(res -> {
            adapter.setLoading(res.getReservationId(), true);

            reservationsRepository.cancelMyReservation(res.getSessionId())
                    .enqueue(new Callback<ReservationDto>() {
                        @Override
                        public void onResponse(Call<ReservationDto> call, Response<ReservationDto> resp) {
                            if (!isAdded()) return;

                            if (resp.isSuccessful()) {
                                adapter.updateStatus(res.getReservationId(), "Cancelada");


                                Toast.makeText(getContext(), "Reserva cancelada", Toast.LENGTH_SHORT).show();
                            } else {
                                adapter.setLoading(res.getReservationId(), false);
                                Toast.makeText(getContext(),
                                        "No se pudo cancelar (" + resp.code() + ")",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ReservationDto> call, Throwable t) {
                            if (!isAdded()) return;
                            adapter.setLoading(res.getReservationId(), false);
                            Toast.makeText(getContext(),
                                    "Error de red: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        loadReservations(null);

        return view;
    }

    private void loadReservations(String status) {
        reservationsRepository.fetchMyReservations(status).enqueue(new Callback<List<ReservationDto>>() {
            @Override
            public void onResponse(Call<List<ReservationDto>> call, Response<List<ReservationDto>> resp) {
                if (!isAdded()) return;

                if (resp.isSuccessful() && resp.body() != null) {
                    List<ReservationUiModel> uiList = new ArrayList<>();
                    for (ReservationDto dto : resp.body()) {
                        uiList.add(ReservationMappers.toUi(dto));
                    }
                    adapter.setItems(uiList);
                } else {
                    Toast.makeText(getContext(), "No se pudieron cargar reservas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ReservationDto>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}