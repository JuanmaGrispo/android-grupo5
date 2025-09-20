package com.example.tp0gym;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp0gym.adapter.ClaseAdapter;
import com.example.tp0gym.modelo.Clase;
import com.example.tp0gym.repository.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ClaseAdapter adapter;
    private List<Clase> listaClases = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recyclerClases);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClaseAdapter(listaClases);
        recyclerView.setAdapter(adapter);

        cargarClases();
    }

    private void cargarClases() {
        RetrofitClient.getApiService().getClases().enqueue(new Callback<List<Clase>>() {
            @Override
            public void onResponse(Call<List<Clase>> call, Response<List<Clase>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaClases = response.body();
                    adapter.updateList(listaClases);
                }
            }

            @Override
            public void onFailure(Call<List<Clase>> call, Throwable t) {
                Log.e("API_ERROR", "Error al traer clases: " + t.getMessage());
            }
        });
    }
}
