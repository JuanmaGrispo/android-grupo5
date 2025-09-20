package com.example.tp0gym.ui.screens;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tp0gym.MainActivity;
import com.example.tp0gym.R;
import com.example.tp0gym.ui.components.CustomTextField;

public class WelcomeFragment extends Fragment {

    private CustomTextField emailField, passwordField;
    private ImageView togglePassword;
    private Button loginButton;
    private TextView otpButton;
    private boolean isPasswordVisible = false;

    public WelcomeFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        // Referencias
        emailField = view.findViewById(R.id.emailField);
        passwordField = view.findViewById(R.id.passwordField);
        togglePassword = view.findViewById(R.id.togglePassword);
        loginButton = view.findViewById(R.id.loginButton);
        otpButton = view.findViewById(R.id.otpButton);

        // Colores blancos
        emailField.setTextColor(Color.WHITE);
        emailField.setHintTextColor(Color.WHITE);
        passwordField.setTextColor(Color.WHITE);
        passwordField.setHintTextColor(Color.WHITE);

        // Tipo contraseña
        passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordField.setSelection(passwordField.getText().length());

        // Cursor blanco para Android Q+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            emailField.setTextCursorDrawable(null);
            passwordField.setTextCursorDrawable(null);
        }

        // Mostrar / ocultar contraseña
        togglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye_off);
                isPasswordVisible = false;
            } else {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye);
                isPasswordVisible = true;
            }
            passwordField.setSelection(passwordField.getText().length());
        });

        // Login
        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            // Validaciones
            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Por favor complete los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getContext(), "Por favor ingrese su email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!email.contains("@")) {
                Toast.makeText(getContext(), "Email inválido", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Por favor ingrese su contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getContext(), "Login exitoso", Toast.LENGTH_SHORT).show();
            ((MainActivity)getActivity()).getNavigationManager().navigateTo("permissions");
        });

        // OTP
        otpButton.setOnClickListener(v -> {
            ((MainActivity)getActivity()).getNavigationManager().navigateTo("email");
        });

        return view;
    }
}
