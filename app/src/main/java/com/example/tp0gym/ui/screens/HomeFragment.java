package com.example.tp0gym.ui.screens;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.example.tp0gym.modelo.Clase;
import com.example.tp0gym.repository.ClasesApi;
import com.example.tp0gym.adapter.ClasesAdapter;
import com.example.tp0gym.repository.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ClasesAdapter adapter;
    private List<Clase> listaClases = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClasesAdapter(listaClases);
        recyclerView.setAdapter(adapter);

        cargarClases();

        return view;
    }

    private void cargarClases() {
        String token = getContext()
                .getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                .getString("TOKEN", null);

        if (token == null) {
            Log.e("API_ERROR", "Token no encontrado");
            return;
        }

        ClasesApi api = RetrofitClient.getClient().create(ClasesApi.class);
        api.getClases("Bearer " + token).enqueue(new Callback<List<Clase>>() {
            @Override
            public void onResponse(Call<List<Clase>> call, Response<List<Clase>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaClases.clear();
                    listaClases.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API_ERROR", "Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Clase>> call, Throwable t) {
                Log.e("API_ERROR", "Error cargando clases", t);
            }
        });
    }

}
