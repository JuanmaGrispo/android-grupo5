// app/src/main/java/com/example/tp0gym/ui/screens/ClasesFragment.java
package com.example.tp0gym.ui.screens;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.tp0gym.repository.api.ReservationsApi;
import com.example.tp0gym.repository.api.SessionsApi;
import com.example.tp0gym.repository.dto.CreateReservationBody;
import com.example.tp0gym.repository.dto.ReservationDto;
import com.example.tp0gym.utils.AppPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Set;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@AndroidEntryPoint
public class ClasesFragment extends Fragment {

    // UI
    private RecyclerView rvClases;
    private ClasesAdapter clasesAdapter;
    private Spinner spinnerSede, spinnerDisciplina;
    private Button btnApplyFilter, btnClearFilter;
    private TextView tvStartDate, tvEndDate, tvFilterInfo, tvDateRange;
    private LinearLayout filterInfoContainer;

    // Datos cacheados
    private final List<BranchDto> allBranches = new ArrayList<>();
    private final List<Clase> allClasses = new ArrayList<>();
    private final List<String> allDisciplines = new ArrayList<>();
    private final List<SessionDto> allSessions = new ArrayList<>();

    // Map rápido nombre→id para sede
    private final Map<String, String> branchNameToId = new HashMap<>();

    // Filtros de fecha
    private LocalDate startDate, endDate;
    private boolean isFilterActive = false;

    @Inject Retrofit retrofit;
    @Inject AppPreferences prefs;

    // ====================== Ciclo de vida ======================

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clases, container, false);

        // Bind UI
        rvClases           = view.findViewById(R.id.rvClases);
        spinnerSede        = view.findViewById(R.id.spinnerSede);
        spinnerDisciplina  = view.findViewById(R.id.spinnerDisciplina);
        btnApplyFilter     = view.findViewById(R.id.btnApplyFilter);
        btnClearFilter     = view.findViewById(R.id.btnClearFilter);
        tvStartDate        = view.findViewById(R.id.tvStartDate);
        tvEndDate          = view.findViewById(R.id.tvEndDate);
        filterInfoContainer= view.findViewById(R.id.filterInfoContainer);
        tvFilterInfo       = view.findViewById(R.id.tvFilterInfo);
        tvDateRange        = view.findViewById(R.id.tvDateRange);

        // Recycler
        rvClases.setLayoutManager(new LinearLayoutManager(getContext()));
        clasesAdapter = new ClasesAdapter(new ArrayList<>());
        rvClases.setAdapter(clasesAdapter);

        // Click "Reservar" por item
        clasesAdapter.setOnReserveClickListener(this::onReserve);

        // Date pickers + botones
        setupDateSelectors();
        setupButtons();

        // Carga inicial
        loadInitialData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!prefs.getPermissionsAsked()) {
            NavController nav = NavHostFragment.findNavController(this);
            nav.navigate(R.id.action_clases_to_permissions);
        }
    }

    // ====================== Carga inicial ======================

    private void loadInitialData() {
        loadBranches();
        loadClasses();
        loadSessions(); // sin filtros
    }

    // ====================== Branches ======================

    private void loadBranches() {
        String token = prefs.getToken();
        if (isTokenMissing(token)) return;

        BranchesApi api = retrofit.create(BranchesApi.class);
        api.getBranches("Bearer " + token).enqueue(new Callback<List<BranchDto>>() {
            @Override public void onResponse(Call<List<BranchDto>> call, Response<List<BranchDto>> resp) {
                if (!isAdded()) return;
                if (resp.isSuccessful() && resp.body() != null) {
                    allBranches.clear();
                    allBranches.addAll(resp.body());
                    rebuildBranchIndexAndSpinner();
                } else {
                    toast("Error al cargar sedes");
                }
            }
            @Override public void onFailure(Call<List<BranchDto>> call, Throwable t) {
                if (!isAdded()) return;
                toast("Error de conexión al cargar sedes: " + t.getMessage());
            }
        });
    }

    private void rebuildBranchIndexAndSpinner() {
        branchNameToId.clear();

        List<String> names = new ArrayList<>();
        names.add("Todas"); // opción default

        for (BranchDto b : allBranches) {
            if (!TextUtils.isEmpty(b.getName())) {
                names.add(b.getName());
                branchNameToId.put(b.getName(), b.getId());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                names
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSede.setAdapter(adapter);
    }

    private @Nullable String resolveBranchIdFromSpinner() {
        String sel = safeGetSelected(spinnerSede);
        if (sel == null) return null; // "Todas"
        return branchNameToId.get(sel); // puede ser null si no matchea
    }

    // ====================== Date pickers / botones ======================

    private void setupDateSelectors() {
        tvStartDate.setOnClickListener(v -> showDatePicker(true));
        tvEndDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void setupButtons() {
        btnApplyFilter.setOnClickListener(v -> applyFilters());
        btnClearFilter.setOnClickListener(v -> clearDateFilter());
        updateApplyButtonState();
    }

    private void showDatePicker(boolean isStart) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(
                requireContext(),
                (view, y, m, d) -> {
                    LocalDate sel = LocalDate.of(y, m + 1, d);
                    String label = sel.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    if (isStart) {
                        startDate = sel;
                        tvStartDate.setText(label);
                    } else {
                        endDate = sel;
                        tvEndDate.setText(label);
                    }
                    updateApplyButtonState();
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        dp.getDatePicker().setMaxDate(System.currentTimeMillis());
        dp.show();
    }

    private void updateApplyButtonState() {
        boolean can = startDate != null && endDate != null && !startDate.isAfter(endDate);
        btnApplyFilter.setEnabled(can);
    }

    private @Nullable String safeGetSelected(Spinner sp) {
        Object it = sp.getSelectedItem();
        if (it == null) return null;
        String s = String.valueOf(it);
        return "Todas".equals(s) ? null : s;
    }

    // ====================== Sesiones ======================

    private void loadSessions() {
        String token = prefs.getToken();
        if (isTokenMissing(token)) return;

        SessionsApi api = retrofit.create(SessionsApi.class);
        // Sin filtros, solo paginación
        Call<SessionsResponse> call = api.getSessions("Bearer " + token, 1, 100);
        call.enqueue(new Callback<SessionsResponse>() {
            @Override
            public void onResponse(Call<SessionsResponse> call, Response<SessionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allSessions.clear();
                    allSessions.addAll(response.body().getItems());
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

    private void loadSessionsWithFilters(@Nullable String branchId, @Nullable String discipline, @NonNull LocalDate from, @NonNull LocalDate to) {
        String token = prefs.getToken();
        if (isTokenMissing(token)) return;

        String fromStr = from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String toStr   = to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        SessionsApi api = retrofit.create(SessionsApi.class);
        api.getSessionsWithFilters("Bearer " + token, fromStr, toStr, branchId, discipline, 1, 200)
                .enqueue(new Callback<SessionsResponse>() {
                    @Override public void onResponse(Call<SessionsResponse> call, Response<SessionsResponse> resp) {
                        if (!isAdded()) return;
                        if (resp.isSuccessful() && resp.body() != null && resp.body().getItems() != null) {
                            allSessions.clear();
                            allSessions.addAll(resp.body().getItems());
                            clasesAdapter.updateData(convertSessionsToClases(allSessions));

                            isFilterActive = true;
                            filterInfoContainer.setVisibility(View.VISIBLE);
                            tvFilterInfo.setText("Mostrando: " + allSessions.size() + " sesiones");
                            String range = from.format(DateTimeFormatter.ofPattern("dd MMM")) + " - " +
                                    to.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
                            tvDateRange.setText(range);
                        } else {
                            toast("Error al cargar sesiones filtradas");
                        }
                    }
                    @Override public void onFailure(Call<SessionsResponse> call, Throwable t) {
                        if (!isAdded()) return;
                        toast("Error de conexión: " + t.getMessage());
                    }
                });
    }

    /** Dedupe por classRef.id: mostramos una tarjeta por clase. */
    private List<Clase> convertSessionsToClases(List<SessionDto> sessions) {
        Map<String, Clase> map = new HashMap<>();
        for (SessionDto s : sessions) {
            if (s.getClassRef() == null) continue;
            String classId = s.getClassRef().getId();
            if (TextUtils.isEmpty(classId)) continue;

            if (!map.containsKey(classId)) {
                Clase c = new Clase();
                c.setId(classId);
                c.setTitle(s.getClassRef().getTitle());
                c.setDescription(s.getClassRef().getDescription());
                c.setDiscipline(s.getClassRef().getDiscipline());
                // Mostrar defaults o de la sesión (a elección)
                c.setDefaultDurationMin(s.getDurationMin());
                c.setDefaultCapacity(s.getCapacity());
                c.setInstructorName(s.getClassRef().getInstructorName());
                c.setLocationName(s.getClassRef().getLocationName());
                c.setLocationAddress(s.getClassRef().getLocationAddress());
                map.put(classId, c);
            }
        }
        return new ArrayList<>(map.values());
    }

    // ====================== Clases ======================

    private void loadClasses() {
        String token = prefs.getToken();
        if (isTokenMissing(token)) return;

        ClasesApi api = retrofit.create(ClasesApi.class);
        api.getClases("Bearer " + token, null, null, null).enqueue(new Callback<List<Clase>>() {
            @Override public void onResponse(Call<List<Clase>> call, Response<List<Clase>> resp) {
                if (!isAdded()) return;
                if (resp.isSuccessful() && resp.body() != null) {
                    allClasses.clear();
                    allClasses.addAll(resp.body());
                    extractDisciplines();
                    populateDisciplineSpinner();
                } else {
                    toast("Error al cargar clases");
                }
            }
            @Override public void onFailure(Call<List<Clase>> call, Throwable t) {
                if (!isAdded()) return;
                toast("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void extractDisciplines() {
        Set<String> set = new HashSet<>();
        for (Clase c : allClasses) {
            if (!TextUtils.isEmpty(c.getDiscipline())) set.add(c.getDiscipline());
        }
        allDisciplines.clear();
        allDisciplines.addAll(set);
        allDisciplines.sort(String::compareTo);
    }

    private void populateDisciplineSpinner() {
        List<String> list = new ArrayList<>();
        list.add("Todas");
        list.addAll(allDisciplines);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                list
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisciplina.setAdapter(adapter);
    }

    // ====================== Filtros UI → llamada ======================

    private void applyFilters() {
        if (startDate == null || endDate == null) {
            toast("Selecciona un rango de fechas");
            return;
        }
        String branchId = resolveBranchIdFromSpinner();         // ahora pasamos ID, no nombre
        String discipline = safeGetSelected(spinnerDisciplina); // puede ser null
        loadSessionsWithFilters(branchId, discipline, startDate, endDate);
    }

    private void clearDateFilter() {
        isFilterActive = false;
        startDate = null;
        endDate = null;
        tvStartDate.setText("Seleccionar fecha");
        tvEndDate.setText("Seleccionar fecha");
        btnApplyFilter.setEnabled(false);
        filterInfoContainer.setVisibility(View.GONE);
        loadSessions();
    }

    // ====================== Reservas ======================

    private void onReserve(Clase clase) {
        if (clase == null) {
            toast("Error: clase no disponible");
            return;
        }

        // Buscar la primera sesión disponible para esta clase
        SessionDto session = pickFirstAvailableSessionForClass(clase.getId());
        if (session == null) {
            toast("No hay sesiones disponibles para esta clase");
            return;
        }

        // Crear la reserva
        createReservation(session.getId());
    }

    /** Selecciona la primera sesión SCHEDULED no vencida; si no hay, la más próxima igual. */
    private @Nullable SessionDto pickFirstAvailableSessionForClass(@NonNull String classId) {
        long now = System.currentTimeMillis();

        // 1) SCHEDULED y a futuro
        SessionDto best = allSessions.stream()
                .filter(s -> s.getClassRef() != null && classId.equals(s.getClassRef().getId()))
                .filter(s -> "SCHEDULED".equalsIgnoreCase(s.getStatus()))
                .filter(s -> parseToMillisUtc(s.getStartAt()) >= now)
                .min(Comparator.comparingLong(s -> parseToMillisUtc(s.getStartAt())))
                .orElse(null);

        if (best != null) return best;

        // 2) Si no hay futuras, tomo la SCHEDULED más cercana (aunque sea pasada)
        best = allSessions.stream()
                .filter(s -> s.getClassRef() != null && classId.equals(s.getClassRef().getId()))
                .filter(s -> "SCHEDULED".equalsIgnoreCase(s.getStatus()))
                .min(Comparator.comparingLong(s -> Math.abs(parseToMillisUtc(s.getStartAt()) - now)))
                .orElse(null);

        return best;
    }

    private void createReservation(@NonNull String sessionId) {
        String token = prefs.getToken();
        if (isTokenMissing(token)) return;

        ReservationsApi api = retrofit.create(ReservationsApi.class);
        api.createReservation("Bearer " + token, new CreateReservationBody(sessionId))
                .enqueue(new Callback<ReservationDto>() {
                    @Override public void onResponse(Call<ReservationDto> call, Response<ReservationDto> resp) {
                        if (!isAdded()) return;
                        if (resp.isSuccessful()) {
                            toast("Reserva creada ✔");
                            // refrescar sesiones para reflejar reservedCount, etc.
                            if (isFilterActive && startDate != null && endDate != null) {
                                loadSessionsWithFilters(resolveBranchIdFromSpinner(), safeGetSelected(spinnerDisciplina), startDate, endDate);
                            } else {
                                loadSessions();
                            }
                            return;
                        }

                        // Manejo específico "Sin cupo"
                        String msg = "No se pudo reservar";
                        if (resp.code() == 400) {
                            try {
                                ResponseBody eb = resp.errorBody();
                                String raw = eb != null ? eb.string() : null;
                                if (raw != null && raw.contains("Sin cupo")) {
                                    msg = "La clase no tiene cupo disponible.";
                                }
                            } catch (Exception ignored) { }
                        }
                        toast(msg);
                    }

                    @Override public void onFailure(Call<ReservationDto> call, Throwable t) {
                        if (!isAdded()) return;
                        toast("Error reservando: " + t.getMessage());
                    }
                });
    }

    // ====================== Utils ======================

    private boolean isTokenMissing(@Nullable String token) {
        if (TextUtils.isEmpty(token)) {
            toast("Token no disponible");
            return true;
        }
        return false;
    }

    private void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /** Parser robusto para ISO-8601 (con o sin Z/offset). Devuelve epoch ms UTC o Long.MAX_VALUE si falla. */
    private long parseToMillisUtc(@Nullable String raw) {
        if (TextUtils.isEmpty(raw)) return Long.MAX_VALUE;
        // Patrones comunes
        String[] patterns = new String[] {
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss"
        };
        for (String p : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(p, Locale.US);
                // Si el patrón no trae zona (no contiene X ni Z), asumir UTC
                if (!p.contains("X") && !p.contains("Z")) {
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                }
                return sdf.parse(raw).getTime();
            } catch (ParseException ignored) {}
        }
        return Long.MAX_VALUE;
    }
}