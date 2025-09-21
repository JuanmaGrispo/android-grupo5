package com.example.tp0gym.ui.screens;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tp0gym.MainActivity;
import com.example.tp0gym.R;
import com.example.tp0gym.modelo.OTPResponse;
import com.example.tp0gym.repository.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailFragment extends Fragment {

    private EditText emailField;
    private Button nextButton, backButton;
    private final AuthRepository authRepository = new AuthRepository();

    private String censorEmail(String email) {
        int at = email.indexOf("@");
        if (at <= 1) return "*" + email.substring(at);
        return email.substring(0, 1) + "***" + email.substring(at);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_email, container, false);

        emailField = view.findViewById(R.id.emailField);
        nextButton = view.findViewById(R.id.nextButton);
        backButton = view.findViewById(R.id.backButton);

        emailField.setTextColor(Color.WHITE);
        emailField.setHintTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            emailField.setTextCursorDrawable(null);
        }

        nextButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();

            if(email.isEmpty()) {
                Toast.makeText(getContext(), "Ingrese un email", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!email.contains("@")) {
                Toast.makeText(getContext(), "Email inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            authRepository.startLogin(email, new Callback<OTPResponse>() {
                @Override
                public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        OTPResponse otpResponse = response.body();
                        String message = otpResponse.getMessage() != null ? otpResponse.getMessage() : "";

                        if (otpResponse.isSuccess() || message.toLowerCase().contains("otp vigente")) {
                            Toast.makeText(getContext(),
                                    otpResponse.isSuccess() ? "Código enviado a " + censorEmail(email)
                                            : "Ya existe un OTP vigente. Revísalo.",
                                    Toast.LENGTH_SHORT).show();
                            navegarVerification(email);
                        } else {
                            Toast.makeText(getContext(), "No hay una cuenta asignada a ese email", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al enviar OTP", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OTPResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        backButton.setOnClickListener(v ->
                ((MainActivity)getActivity()).getNavigationManager().navigateTo("welcome")
        );

        return view;
    }

    private void navegarVerification(String email) {
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        ((MainActivity)getActivity()).getNavigationManager().navigateTo("verification", bundle);
    }
}
