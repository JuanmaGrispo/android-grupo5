package com.example.tp0gym.ui.screens;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp0gym.R;
import com.example.tp0gym.adapter.ClasesAdapter;
import com.example.tp0gym.modelo.Clase;
import com.example.tp0gym.repository.api.ClasesApi;
import com.example.tp0gym.utils.AppPreferences;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@AndroidEntryPoint
public class ClasesFragment extends Fragment {

    private RecyclerView rvClases;
    private ClasesAdapter clasesAdapter;
    private Spinner spinnerSede, spinnerDisciplina;
    private Button btnFecha, btnFiltrar;
    private String fechaSeleccionada = null;

    @Inject
    Retrofit retrofit;

    @Inject
    AppPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clases, container, false);

        // Referencias a la UI
        rvClases = view.findViewById(R.id.rvClases);
        spinnerSede = view.findViewById(R.id.spinnerSede);
        spinnerDisciplina = view.findViewById(R.id.spinnerDisciplina);
        btnFecha = view.findViewById(R.id.btnFecha);
        btnFiltrar = view.findViewById(R.id.btnFiltrar);

        // Configuración del RecyclerView
        rvClases.setLayoutManager(new LinearLayoutManager(getContext()));
        clasesAdapter = new ClasesAdapter(null);
        rvClases.setAdapter(clasesAdapter);

        // Inicializar filtros y acciones
        setupSpinners();
        setupDatePicker();
        setupFilterButton();

        // Cargar clases inicialmente sin filtros
        cargarClases(null, null, null);

        return view;
    }

    private void setupSpinners() {
        ArrayAdapter<String> sedeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Todas", "Sede A", "Sede B"});
        sedeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSede.setAdapter(sedeAdapter);

        ArrayAdapter<String> disciplinaAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Todas", "Funcional", "Yoga", "Crossfit"});
        disciplinaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisciplina.setAdapter(disciplinaAdapter);
    }

    private void setupDatePicker() {
        btnFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(getContext(), (view, y, m, d) -> {
                fechaSeleccionada = String.format("%04d-%02d-%02d", y, m + 1, d);
                btnFecha.setText(fechaSeleccionada);
            }, year, month, day);
            dpd.show();
        });
    }

    private void setupFilterButton() {
        btnFiltrar.setOnClickListener(v -> {
            String sede = getSelectedValue(spinnerSede);
            String disciplina = getSelectedValue(spinnerDisciplina);

            cargarClases(sede, disciplina, fechaSeleccionada);

            Toast.makeText(getContext(),
                    "Aplicando filtros -> sede=" + sede +
                            ", disciplina=" + disciplina +
                            ", fecha=" + fechaSeleccionada,
                    Toast.LENGTH_SHORT).show();
        });
    }

    private String getSelectedValue(Spinner spinner) {
        String val = spinner.getSelectedItem().toString();
        return val.equals("Todas") ? null : val;
    }

    private void cargarClases(String sede, String disciplina, String fecha) {
        String token = prefs.getToken();

        ClasesApi api = retrofit.create(ClasesApi.class);
        Call<List<Clase>> call = api.getClases("Bearer " + token, sede, disciplina, fecha);
        call.enqueue(new Callback<List<Clase>>() {
            @Override
            public void onResponse(Call<List<Clase>> call, Response<List<Clase>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    clasesAdapter.updateData(response.body());
                } else {
                    Toast.makeText(getContext(), "Error al cargar clases", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Clase>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}