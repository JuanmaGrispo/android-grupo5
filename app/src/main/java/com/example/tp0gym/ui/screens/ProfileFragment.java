// ProfileFragment.java
package com.example.tp0gym.ui.screens;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tp0gym.R;
import com.example.tp0gym.modelo.User;
import com.example.tp0gym.repository.UserRepository;
import com.example.tp0gym.ui.components.CustomTextField;
import com.example.tp0gym.utils.AppPreferences;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import androidx.navigation.fragment.NavHostFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint // ➜ IMPRESCINDIBLE para que Hilt inyecte 'repo' y 'appPrefs'
public class ProfileFragment extends Fragment {

    private CustomTextField nameField, emailField;
    private Button saveButton, backButton;

    // === NUEVOS ELEMENTOS (mismo XML) ===
    private ImageButton editButton;          // lápiz
    private LinearLayout editNameContainer;  // bloque oculto de edición
    private EditText editNameInput;          // input del nombre
    private Button confirmEditBtn, cancelEditBtn;

    @Inject UserRepository repo;     // ➜ Inyectado por Hilt (ya NO será null)
    @Inject AppPreferences appPrefs; // ➜ Tenés el provider en AppModule

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        // Campos existentes
        nameField      = v.findViewById(R.id.nameField);
        emailField     = v.findViewById(R.id.emailField);
        saveButton     = v.findViewById(R.id.saveButton);
        backButton     = v.findViewById(R.id.backButton);

        // Nuevos (mismo XML)
        editButton         = v.findViewById(R.id.editButton);
        editNameContainer  = v.findViewById(R.id.editNameContainer);
        editNameInput      = v.findViewById(R.id.editNameInput);
        confirmEditBtn     = v.findViewById(R.id.confirmEditBtn);
        cancelEditBtn      = v.findViewById(R.id.cancelEditBtn);

        // Email solo lectura; nombre también (se edita desde el bloque)
        emailField.setEnabled(false);
        nameField.setEnabled(false);

        // 1) Cargar perfil (con chequeo de token)
        loadProfile();

        // 2) Guardar SOLO nombre (botón grande de la pantalla) — opcional mantenerlo
        saveButton.setOnClickListener(view -> {
            final String token = appPrefs.getToken(); // siempre que necesites el token, léelo aquí
            if (token == null || token.isEmpty()) {
                Toast.makeText(requireContext(), "Sesión no iniciada. Iniciá sesión.", Toast.LENGTH_SHORT).show();
                // Ir a Login directamente (destino existe en tu nav_graph)
                NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.loginFragment);
                return;
            }

            String name  = textOf(nameField);
            String email = textOf(emailField); // solo validar formato mostrado (aunque es read-only)

            if (name.isEmpty()) {
                nameField.setError("Ingresá tu nombre");
                nameField.requestFocus();
                return;
            }
            if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.setError("Email inválido");
                emailField.requestFocus();
                return;
            }

            setUiEnabled(false);
            repo.updateMyName(token, name).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> resp) {
                    setUiEnabled(true);
                    if (resp.isSuccessful() && resp.body() != null) {
                        User u = resp.body();
                        nameField.setText(u.getName()  != null ? u.getName()  : "");
                        emailField.setText(u.getEmail() != null ? u.getEmail() : "");
                        Toast.makeText(requireContext(), "Nombre actualizado", Toast.LENGTH_SHORT).show();
                    } else if (resp.code() == 401) {
                        Toast.makeText(requireContext(), "Sesión expirada. Iniciá sesión de nuevo.", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.loginFragment);
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    setUiEnabled(true);
                    Toast.makeText(requireContext(), "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // 3) Volver (respeta el back stack del nav_graph)
        backButton.setOnClickListener(view ->
                NavHostFragment.findNavController(ProfileFragment.this).navigateUp()
        );

        // === LÓGICA DEL EDIT IN-LINE (mismo XML) ===

        // Abrir bloque de edición con el valor actual
        editButton.setOnClickListener(view -> {
            editNameInput.setText(textOf(nameField));
            toggleEditContainer(true);
        });

        // Cancelar edición
        cancelEditBtn.setOnClickListener(view -> {
            editNameInput.setText(""); // opcional: limpiar
            toggleEditContainer(false);
        });

        // Confirmar edición (guardar al backend)
        confirmEditBtn.setOnClickListener(view -> {
            final String token = appPrefs.getToken();
            if (token == null || token.isEmpty()) {
                Toast.makeText(requireContext(), "Sesión no iniciada. Iniciá sesión.", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.loginFragment);
                return;
            }

            String newName = editNameInput.getText() != null ? editNameInput.getText().toString().trim() : "";
            if (TextUtils.isEmpty(newName)) {
                editNameInput.setError("Ingresá tu nombre");
                editNameInput.requestFocus();
                return;
            }

            setEditEnabled(false);
            repo.updateMyName(token, newName).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> resp) {
                    setEditEnabled(true);
                    if (resp.isSuccessful() && resp.body() != null) {
                        User u = resp.body();
                        nameField.setText(u.getName() != null ? u.getName() : newName);
                        Toast.makeText(requireContext(), "Nombre actualizado", Toast.LENGTH_SHORT).show();
                        toggleEditContainer(false);
                    } else if (resp.code() == 401) {
                        Toast.makeText(requireContext(), "Sesión expirada. Iniciá sesión de nuevo.", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.loginFragment);
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    setEditEnabled(true);
                    Toast.makeText(requireContext(), "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadProfile() {
        final String token = appPrefs.getToken(); // leer token por Hilt (AppPreferences)
        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "Sesión no iniciada. Iniciá sesión.", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.loginFragment);
            return;
        }

        setUiEnabled(false);
        repo.fetchMe(token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> resp) {
                setUiEnabled(true);
                if (resp.isSuccessful() && resp.body() != null) {
                    User u = resp.body();
                    nameField.setText(u.getName()  != null ? u.getName()  : "");
                    emailField.setText(u.getEmail() != null ? u.getEmail() : "");
                } else if (resp.code() == 401) {
                    Toast.makeText(requireContext(), "Sesión expirada. Iniciá sesión de nuevo.", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.loginFragment);
                } else {
                    Toast.makeText(requireContext(), "No se pudo cargar el perfil (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                setUiEnabled(true);
                Toast.makeText(requireContext(), "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUiEnabled(boolean enabled) {
        // Campos principales
        nameField.setEnabled(false); // siempre lectura (se edita con el bloque)
        emailField.setEnabled(false); // siempre lectura
        saveButton.setEnabled(enabled);

        // Controles de edición in-line
        editButton.setEnabled(enabled);
        confirmEditBtn.setEnabled(enabled);
        cancelEditBtn.setEnabled(enabled);
        editNameInput.setEnabled(enabled);
    }

    private void setEditEnabled(boolean enabled) {
        confirmEditBtn.setEnabled(enabled);
        cancelEditBtn.setEnabled(enabled);
        editNameInput.setEnabled(enabled);
    }

    private void toggleEditContainer(boolean show) {
        if (editNameContainer != null) {
            editNameContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private String textOf(CustomTextField field) {
        return field.getText() != null ? field.getText().toString().trim() : "";
    }
}
