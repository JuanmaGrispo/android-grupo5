package com.example.tp0gym.ui.screens;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp0gym.R;
import com.example.tp0gym.adapter.ClasesAdapter;
import com.example.tp0gym.modelo.BranchDto;
import com.example.tp0gym.modelo.Clase;
import com.example.tp0gym.modelo.SessionDto;
import com.example.tp0gym.modelo.SessionsResponse;
import com.example.tp0gym.repository.api.BranchesApi;
import com.example.tp0gym.repository.api.ClasesApi;
import com.example.tp0gym.repository.api.SessionsApi;
import com.example.tp0gym.utils.AppPreferences;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    private Button btnApplyFilter, btnClearFilter;
    private TextView tvStartDate, tvEndDate;
    private LinearLayout filterInfoContainer;
    private TextView tvFilterInfo, tvDateRange;
    
    // Datos para filtros
    private List<BranchDto> allBranches = new ArrayList<>();
    private List<Clase> allClasses = new ArrayList<>();
    private List<String> allDisciplines = new ArrayList<>();
    private List<SessionDto> allSessions = new ArrayList<>();
    
    // Filtros de fecha
    private LocalDate startDate, endDate;
    private boolean isFilterActive = false;

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
        btnApplyFilter = view.findViewById(R.id.btnApplyFilter);
        btnClearFilter = view.findViewById(R.id.btnClearFilter);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        filterInfoContainer = view.findViewById(R.id.filterInfoContainer);
        tvFilterInfo = view.findViewById(R.id.tvFilterInfo);
        tvDateRange = view.findViewById(R.id.tvDateRange);

        // Configuración del RecyclerView
        rvClases.setLayoutManager(new LinearLayoutManager(getContext()));
        clasesAdapter = new ClasesAdapter(null);
        rvClases.setAdapter(clasesAdapter);

        // Inicializar filtros y acciones
        setupDateSelectors();
        setupButtons();

        // Cargar datos iniciales
        loadInitialData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Mostrar PermissionsFragment solo la primera vez
        if (!prefs.getPermissionsAsked()) {
            NavController nav = NavHostFragment.findNavController(this);
            nav.navigate(R.id.action_clases_to_permissions);
        }
    }

    private void loadInitialData() {
        // Cargar branches primero
        loadBranches();
        // Luego cargar clases (que también extraerá disciplines)
        loadClasses();
        // Cargar sesiones iniciales (sin filtros)
        loadSessions();
    }

    private void loadBranches() {
        String token = prefs.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Token no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        BranchesApi api = retrofit.create(BranchesApi.class);
        Call<List<BranchDto>> call = api.getBranches("Bearer " + token);
        call.enqueue(new Callback<List<BranchDto>>() {
            @Override
            public void onResponse(Call<List<BranchDto>> call, Response<List<BranchDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allBranches = response.body();
                    populateBranchSpinner();
                } else {
                    Toast.makeText(getContext(), "Error al cargar sedes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BranchDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión al cargar sedes: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateBranchSpinner() {
        List<String> branchNames = new ArrayList<>();
        branchNames.add("Todas"); // Siempre primero
        
        for (BranchDto branch : allBranches) {
            if (branch.getName() != null) {
                branchNames.add(branch.getName());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, branchNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSede.setAdapter(adapter);
    }

    private void setupDateSelectors() {
        tvStartDate.setOnClickListener(v -> showDatePicker(true));
        tvEndDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void setupButtons() {
        btnApplyFilter.setOnClickListener(v -> applyFilters());
        btnClearFilter.setOnClickListener(v -> clearDateFilter());
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    LocalDate selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                    String formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    
                    if (isStartDate) {
                        startDate = selectedDate;
                        tvStartDate.setText(formattedDate);
                    } else {
                        endDate = selectedDate;
                        tvEndDate.setText(formattedDate);
                    }
                    
                    updateApplyButtonState();
                }, year, month, day);

        // No permitir fechas futuras
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateApplyButtonState() {
        boolean canApply = startDate != null && endDate != null && !startDate.isAfter(endDate);
        btnApplyFilter.setEnabled(canApply);
    }

    private String getSelectedValue(Spinner spinner) {
        String val = spinner.getSelectedItem().toString();
        return val.equals("Todas") ? null : val;
    }

    private void loadSessions() {
        String token = prefs.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Token no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        SessionsApi api = retrofit.create(SessionsApi.class);
        Call<SessionsResponse> call = api.getSessions("Bearer " + token, null, null, null, null, 1, 100);
        call.enqueue(new Callback<SessionsResponse>() {
            @Override
            public void onResponse(Call<SessionsResponse> call, Response<SessionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allSessions = response.body().getItems();
                    // Convertir sesiones a clases para el adapter
                    List<Clase> clasesFromSessions = convertSessionsToClases(allSessions);
                    clasesAdapter.updateData(clasesFromSessions);
                } else {
                    Toast.makeText(getContext(), "Error al cargar sesiones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SessionsResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Clase> convertSessionsToClases(List<SessionDto> sessions) {
        List<Clase> clases = new ArrayList<>();
        for (SessionDto session : sessions) {
            if (session.getClassRef() != null) {
                Clase clase = new Clase();
                clase.setId(session.getClassRef().getId());
                clase.setTitle(session.getClassRef().getTitle());
                clase.setDescription(session.getClassRef().getDescription());
                clase.setDiscipline(session.getClassRef().getDiscipline());
                clase.setDefaultDurationMin(session.getDurationMin());
                clase.setDefaultCapacity(session.getCapacity());
                clase.setInstructorName(session.getClassRef().getInstructorName());
                clase.setLocationName(session.getClassRef().getLocationName());
                clase.setLocationAddress(session.getClassRef().getLocationAddress());
                clases.add(clase);
            }
        }
        return clases;
    }

    private void applyFilters() {
        if (startDate == null || endDate == null) {
            Toast.makeText(getContext(), "Selecciona un rango de fechas", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedBranch = getSelectedValue(spinnerSede);
        String selectedDiscipline = getSelectedValue(spinnerDisciplina);

        // Llamar API de sesiones con filtros
        loadSessionsWithFilters(selectedBranch, selectedDiscipline, startDate, endDate);
    }

    private void loadSessionsWithFilters(String branch, String discipline, LocalDate from, LocalDate to) {
        String token = prefs.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Token no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        String fromStr = from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String toStr = to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        SessionsApi api = retrofit.create(SessionsApi.class);
        Call<SessionsResponse> call = api.getSessions("Bearer " + token, fromStr, toStr, branch, discipline, 1, 100);
        call.enqueue(new Callback<SessionsResponse>() {
            @Override
            public void onResponse(Call<SessionsResponse> call, Response<SessionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allSessions = response.body().getItems();
                    List<Clase> clasesFromSessions = convertSessionsToClases(allSessions);
                    clasesAdapter.updateData(clasesFromSessions);
                    
                    // Mostrar información del filtro
                    isFilterActive = true;
                    filterInfoContainer.setVisibility(View.VISIBLE);
                    tvFilterInfo.setText("Mostrando: " + allSessions.size() + " sesiones");
                    String dateRangeText = from.format(DateTimeFormatter.ofPattern("dd MMM")) + " - " + 
                                          to.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
                    tvDateRange.setText(dateRangeText);
                } else {
                    Toast.makeText(getContext(), "Error al cargar sesiones filtradas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SessionsResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearDateFilter() {
        isFilterActive = false;
        startDate = null;
        endDate = null;
        
        // Resetear UI
        tvStartDate.setText("Seleccionar fecha");
        tvEndDate.setText("Seleccionar fecha");
        btnApplyFilter.setEnabled(false);
        filterInfoContainer.setVisibility(View.GONE);
        
        // Cargar todas las sesiones
        loadSessions();
    }

    private void loadClasses() {
        String token = prefs.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Token no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        ClasesApi api = retrofit.create(ClasesApi.class);
        Call<List<Clase>> call = api.getClases("Bearer " + token, null, null, null);
        call.enqueue(new Callback<List<Clase>>() {
            @Override
            public void onResponse(Call<List<Clase>> call, Response<List<Clase>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allClasses = response.body();
                    extractDisciplines();
                    populateDisciplineSpinner();
                    applyFilters(); // Aplicar filtros actuales
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

    private void extractDisciplines() {
        Set<String> disciplineSet = new HashSet<>();
        for (Clase clase : allClasses) {
            if (clase.getDiscipline() != null && !clase.getDiscipline().trim().isEmpty()) {
                disciplineSet.add(clase.getDiscipline());
            }
        }
        
        allDisciplines = new ArrayList<>(disciplineSet);
        allDisciplines.sort(String::compareTo); // Ordenar alfabéticamente
    }

    private void populateDisciplineSpinner() {
        List<String> disciplineNames = new ArrayList<>();
        disciplineNames.add("Todas"); // Siempre primero
        disciplineNames.addAll(allDisciplines);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, disciplineNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisciplina.setAdapter(adapter);
    }
}
