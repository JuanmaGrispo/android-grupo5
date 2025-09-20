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

import com.example.tp0gym.MainActivity;
import com.example.tp0gym.R;

public class VerificationFragment extends Fragment {

    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private Button resendButton;
    private String email;
    private final String correctCode = "123456";

    public VerificationFragment() {
        // constructor vacío obligatorio
    }

    // Método para pasar parámetros al fragment
    public static VerificationFragment newInstance(String email) {
        VerificationFragment fragment = new VerificationFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_verification, container, false);

        otp1 = view.findViewById(R.id.otp1);
        otp2 = view.findViewById(R.id.otp2);
        otp3 = view.findViewById(R.id.otp3);
        otp4 = view.findViewById(R.id.otp4);
        otp5 = view.findViewById(R.id.otp5);
        otp6 = view.findViewById(R.id.otp6);

        resendButton = view.findViewById(R.id.resendButton);

        setupOtpWatchers();

        resendButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Código reenviado a " + censorEmail(email), Toast.LENGTH_SHORT).show()
        );

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

                    String code = "";
                    for (EditText otp : otps) code += otp.getText().toString();

                    if (code.length() == 6) {
                        checkCode(code, otps);
                    }
                }
            });
        }
    }

    private void checkCode(String code, EditText[] otps) {
        if (code.equals(correctCode)) {
            Toast.makeText(getContext(), "Código correcto, sesión iniciada", Toast.LENGTH_SHORT).show();
            ((MainActivity)getActivity()).getNavigationManager().navigateTo("permissions");
        } else {
            Toast.makeText(getContext(), "Código incorrecto, inténtalo de nuevo", Toast.LENGTH_SHORT).show();

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
            censoredName = name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1);
        }

        return censoredName + "@" + domain;
    }
}
