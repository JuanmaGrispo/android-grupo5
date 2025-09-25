package com.example.tp0gym.ui.screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tp0gym.R;
import com.example.tp0gym.modelo.User;
import com.example.tp0gym.repository.UserRepository;
import com.example.tp0gym.ui.components.CustomTextField;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private CustomTextField nameField, emailField;
    private Button saveButton, backButton;
    private UserRepository repo;
    private String token;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        nameField = v.findViewById(R.id.nameField);
        emailField = v.findViewById(R.id.emailField);
        saveButton = v.findViewById(R.id.saveButton);
        backButton = v.findViewById(R.id.backButton);

        // Repo y token desde tus SharedPreferences existentes
        repo = new UserRepository();
        SharedPreferences prefs = requireContext().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        token = prefs.getString("TOKEN", null);

        // Email es solo lectura en este flujo
        emailField.setEnabled(false);

        // 1) Cargar perfil
        loadProfile();

        // 2) Guardar SOLO nombre
        saveButton.setOnClickListener(view -> {
            String name = textOf(nameField);
            String email = textOf(emailField); // solo para validar formato mostrado
            if (name.isEmpty()) {
                nameField.setError("Ingresá tu nombre");
                nameField.requestFocus();
                return;
            }
            if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // Si querés validar que lo que mostrás tenga formato de email
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
                        nameField.setText(u.getName() != null ? u.getName() : "");
                        emailField.setText(u.getEmail() != null ? u.getEmail() : "");
                        Toast.makeText(requireContext(), "Nombre actualizado", Toast.LENGTH_SHORT).show();
                    } else if (resp.code() == 401) {
                        Toast.makeText(requireContext(), "Sesión expirada. Iniciá sesión de nuevo.", Toast.LENGTH_SHORT).show();
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

        // Volver
        backButton.setOnClickListener(view ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
    }

    private void loadProfile() {
        setUiEnabled(false);
        repo.fetchMe(token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> resp) {
                setUiEnabled(true);
                if (resp.isSuccessful() && resp.body() != null) {
                    User u = resp.body();
                    nameField.setText(u.getName() != null ? u.getName() : "");
                    emailField.setText(u.getEmail() != null ? u.getEmail() : "");
                } else if (resp.code() == 401) {
                    Toast.makeText(requireContext(), "Sesión expirada. Iniciá sesión de nuevo.", Toast.LENGTH_SHORT).show();
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
        nameField.setEnabled(enabled);
        emailField.setEnabled(false); // lectura
        saveButton.setEnabled(enabled);
    }

    private String textOf(CustomTextField field) {
        return field.getText() != null ? field.getText().toString().trim() : "";
    }
}