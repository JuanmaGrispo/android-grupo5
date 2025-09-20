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

public class EmailFragment extends Fragment {

    private EditText emailField;
    private Button nextButton, backButton;

    private String censorEmail(String email) {
        int at = email.indexOf("@");
        if(at <= 1) return "*"+email.substring(at);
        return email.substring(0,1) + "***" + email.substring(at);
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

        // --- FORZAR TEXTO, HINT Y CURSOR BLANCOS ---
        emailField.setTextColor(Color.WHITE);
        emailField.setHintTextColor(Color.WHITE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            emailField.setTextCursorDrawable(null);
        }

        nextButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            if(email.isEmpty())
                Toast.makeText(getContext(), "Ingrese un email", Toast.LENGTH_SHORT).show();
            else if(!email.contains("@"))
                Toast.makeText(getContext(), "Email inválido", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getContext(), "Código enviado a " + censorEmail(email), Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putString("email", email);
                ((MainActivity)getActivity()).getNavigationManager().navigateTo("verification", bundle);
            }
        });

        backButton.setOnClickListener(v ->
                ((MainActivity)getActivity()).getNavigationManager().navigateTo("welcome")
        );

        return view;
    }
}
