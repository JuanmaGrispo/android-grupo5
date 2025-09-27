package com.example.tp0gym.ui.screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.InputType;
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
import androidx.navigation.fragment.NavHostFragment;

import com.example.tp0gym.R;
import com.example.tp0gym.modelo.User;
import com.example.tp0gym.repository.AuthRepository;
import com.example.tp0gym.ui.components.CustomTextField;
import com.example.tp0gym.utils.AppPreferences;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    @Inject
    AuthRepository authRepository;
    
    @Inject
    AppPreferences prefs;

    private CustomTextField emailField, passwordField;
    private ImageView togglePassword;
    private Button loginButton;
    private TextView otpButton;
    private boolean isPasswordVisible = false;

    public LoginFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        // Bind de vistas
        emailField     = view.findViewById(R.id.emailField);
        passwordField  = view.findViewById(R.id.passwordField);
        togglePassword = view.findViewById(R.id.togglePassword);
        loginButton    = view.findViewById(R.id.loginButton);
        otpButton      = view.findViewById(R.id.otpButton);

        // Password oculto por defecto
        passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Toggle mostrar/ocultar contraseña
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

        // Click de Login
        loginButton.setOnClickListener(v -> {
            String email    = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            authRepository.login(email, password, new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();

                        // Guardamos token y flags
                        prefs.setToken(user.getAccessToken());
                        prefs.setHasLoggedInOnce(true);
                        prefs.setPermissionsAsked(true); // 👈 importante

                        Toast.makeText(getContext(), "Login exitoso", Toast.LENGTH_SHORT).show();

                        // Navegación al Home
                        NavHostFragment.findNavController(LoginFragment.this)
                                .navigate(R.id.action_login_to_home);
                    } else {
                        Toast.makeText(getContext(), "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Click para OTP/Email
        otpButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_login_to_email);
        });

        return view;
    }
}