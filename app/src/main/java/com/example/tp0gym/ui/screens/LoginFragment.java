package com.example.tp0gym.ui.screens;

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

// Jetpack Navigation
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
        // Inicializamos el wrapper de SharedPreferences
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        emailField     = view.findViewById(R.id.emailField);
        passwordField  = view.findViewById(R.id.passwordField);
        togglePassword = view.findViewById(R.id.togglePassword);
        loginButton    = view.findViewById(R.id.loginButton);
        otpButton      = view.findViewById(R.id.otpButton);

        // Estética básica
        emailField.setTextColor(Color.WHITE);
        emailField.setHintTextColor(Color.WHITE);
        passwordField.setTextColor(Color.WHITE);
        passwordField.setHintTextColor(Color.WHITE);

        // Password oculto por defecto
        passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordField.setSelection(passwordField.getText().length());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            emailField.setTextCursorDrawable(null);
            passwordField.setTextCursorDrawable(null);
        }

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

            authRepository.login(email, password, new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();

                        // Guardamos token y flag
                        prefs.setToken(user.getAccessToken());
                        prefs.setHasLoggedInOnce(true);

                        Toast.makeText(getContext(), "Login exitoso", Toast.LENGTH_SHORT).show();

                        // ✅ Navegar al Home usando Navigation Component (acción definida en nav_graph)
                        NavHostFragment.findNavController(LoginFragment.this)
                                .navigate(R.id.action_login_to_home);
                        // Si no tuvieras action, podrías usar directamente el destino:
                        // .navigate(R.id.clasesFragment);
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

        // Click para ir al flujo de OTP/Email
        otpButton.setOnClickListener(v -> {
            // ✅ Navegación al destino del email (definí una acción en el nav_graph: action_login_to_email)
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_login_to_email);
            // Si no tenés esa action, podés navegar por id de fragment directamente:
            // .navigate(R.id.emailLoginFragment);
        });

        return view;
    }
}