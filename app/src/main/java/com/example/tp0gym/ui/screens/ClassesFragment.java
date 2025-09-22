package com.example.tp0gym.ui.screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp0gym.R;
import com.example.tp0gym.adapter.ClasesAdapter;
import com.example.tp0gym.modelo.Clase;
import com.example.tp0gym.repository.ClasesApi;
import com.example.tp0gym.repository.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ClasesAdapter adapter;

    public ClassesFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classes, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewClases);
        progressBar = view.findViewById(R.id.progressBarClases);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ClasesAdapter(null);
        recyclerView.setAdapter(adapter);

        loadClases();

        return view;
    }

    private void loadClases() {
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences prefs = requireContext().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);

        if(token == null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "No se encontró token, logueate de nuevo", Toast.LENGTH_SHORT).show();
            return;
        }

        ClasesApi api = RetrofitClient.getClient().create(ClasesApi.class);
        api.getClases("Bearer " + token).enqueue(new Callback<List<Clase>>() {
            @Override
            public void onResponse(Call<List<Clase>> call, Response<List<Clase>> response) {
                progressBar.setVisibility(View.GONE);
                if(response.isSuccessful() && response.body() != null) {
                    adapter = new ClasesAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Error al cargar clases", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Clase>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
