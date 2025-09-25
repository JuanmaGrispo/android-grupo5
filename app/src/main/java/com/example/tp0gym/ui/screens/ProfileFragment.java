package com.example.tp0gym.ui.screens;

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
import com.example.tp0gym.ui.components.CustomTextField;

public class ProfileFragment extends Fragment {

    private CustomTextField nameField, emailField;
    private Button saveButton, backButton;

    public ProfileFragment() { /* constructor vacío */ }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        // IDs del nuevo layout
        nameField = v.findViewById(R.id.nameField);
        emailField = v.findViewById(R.id.emailField);
        saveButton = v.findViewById(R.id.saveButton);
        backButton = v.findViewById(R.id.backButton);

        // Guardar (solo UI)
        saveButton.setOnClickListener(view -> {
            String name = nameField.getText() != null ? nameField.getText().toString().trim() : "";
            String email = emailField.getText() != null ? emailField.getText().toString().trim() : "";

            if (name.isEmpty()) {
                nameField.setError("Ingresá tu nombre");
                nameField.requestFocus();
                return;
            }
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.setError("Email inválido");
                emailField.requestFocus();
                return;
            }

            Toast.makeText(requireContext(),
                    "Guardado (solo UI)\nNombre: " + name + "\nEmail: " + email,
                    Toast.LENGTH_SHORT).show();

            // TODO: acá podrías llamar a tu ViewModel / API para persistir los datos
        });

        // Volver
        backButton.setOnClickListener(view ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
    }
}