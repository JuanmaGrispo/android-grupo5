package com.example.tp0gym.ui.screens;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tp0gym.R;
import com.example.tp0gym.modelo.OtpResponse;
import com.example.tp0gym.modelo.User;
import com.example.tp0gym.repository.AuthRepository;
import com.example.tp0gym.utils.AppPreferences;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class VerificationCodeFragment extends Fragment {

    @Inject
    AuthRepository authRepository;

    @Inject
    AppPreferences prefs;

    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private Button resendButton;
    private String email;

    public VerificationCodeFragment() { }

    public static VerificationCodeFragment newInstance(String email) {
        VerificationCodeFragment fragment = new VerificationCodeFragment();
        Bundle args = new Bundle();
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString("email");
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_verification, container, false);

        otp1 = view.findViewById(R.id.otp1);
        otp2 = view.findViewById(R.id.otp2);
        otp3 = view.findViewById(R.id.otp3);
        otp4 = view.findViewById(R.id.otp4);
        otp5 = view.findViewById(R.id.otp5);
        otp6 = view.findViewById(R.id.otp6);
        resendButton = view.findViewById(R.id.resendButton);

        setupOtpWatchers();
        resendButton.setOnClickListener(v -> resendOtp());

        return view;
    }

    private void setupOtpWatchers() {
        EditText[] otps = {otp1, otp2, otp3, otp4, otp5, otp6};

        for (int i = 0; i < otps.length; i++) {
            final int index = i;
            otps[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < otps.length - 1) {
                        otps[index + 1].requestFocus();
                    }

                    StringBuilder codeBuilder = new StringBuilder();
                    for (EditText otp : otps) codeBuilder.append(otp.getText().toString());
                    String code = codeBuilder.toString();

                    if (code.length() == 6) {
                        verifyLogin(code, otps);
                    }
                }
            });
        }
    }

    private void verifyLogin(String code, EditText[] otps) {
        authRepository.verifyLogin(email, code, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null && getContext() != null) {
                    User user = response.body();

                    // Guarda token real si viene; sino usa uno dummy para no romper el flujo
                    prefs.setToken(user.getAccessToken() != null ? user.getAccessToken() : "token_de_prueba");
                    prefs.setHasLoggedInOnce(true);

                    Toast.makeText(getContext(), "Código correcto, sesión iniciada", Toast.LENGTH_SHORT).show();

                    // Navegación con Navigation Component
                    NavController nav = NavHostFragment.findNavController(VerificationCodeFragment.this);
                    nav.navigate(R.id.action_login_to_home);
                } else {
                    showErrorOtps(otps);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showErrorOtps(otps);
            }
        });
    }

    private void resendOtp() {
        // ✅ Cambiado startLogin -> startLoginOtp
        authRepository.startLoginOtp(email, null, new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                if (response.isSuccessful() && response.body() != null && getContext() != null) {
                    OtpResponse otpResponse = response.body();
                    String message = otpResponse.getMessage() != null ? otpResponse.getMessage() : "";

                    if (otpResponse.isSuccess() || message.toLowerCase().contains("otp vigente")) {
                        Toast.makeText(
                                getContext(),
                                otpResponse.isSuccess()
                                        ? "OTP enviado a " + censorEmail(email)
                                        : "Ya existe un OTP vigente. Revísalo.",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Toast.makeText(getContext(), "No hay una cuenta asignada a este email", Toast.LENGTH_SHORT).show();
                    }
                } else if (getContext() != null) {
                    Toast.makeText(getContext(), "Error al reenviar OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error al reenviar OTP: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showErrorOtps(EditText[] otps) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Código incorrecto, inténtalo de nuevo", Toast.LENGTH_SHORT).show();
        }

        for (EditText otp : otps) {
            otp.setBackgroundResource(R.drawable.rounded_border_red);
            otp.setText("");
        }

        if (getContext() != null) {
            Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(100);
                }
            }
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            for (EditText otp : otps) {
                otp.setBackgroundResource(R.drawable.rounded_border_gray);
            }
            otp1.requestFocus();
        }, 1000);
    }

    private String censorEmail(String email) {
        String[] parts = email.split("@");
        if (parts.length != 2) return email;
        String name = parts[0];
        String domain = parts[1];

        String censoredName;
        if (name.length() <= 2) {
            censoredName = name.charAt(0) + "*";
        } else {
            StringBuilder stars = new StringBuilder();
            for (int i = 0; i < name.length() - 2; i++) stars.append("*");
            censoredName = name.charAt(0) + stars.toString() + name.charAt(name.length() - 1);
        }

        return censoredName + "@" + domain;
    }
}
