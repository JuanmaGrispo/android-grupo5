package com.example.tp0gym.ui.components;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;

public class CustomTextField extends androidx.appcompat.widget.AppCompatEditText {
    public CustomTextField(Context context) {
        super(context);
        init();
    }

    public CustomTextField(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // NO usar setBackgroundColor
        setTextColor(Color.WHITE);
        setHintTextColor(Color.WHITE);
        setPadding(16, 16, 16, 16);
        setSingleLine(true);

        // Cursor blanco
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setTextCursorDrawable(null);
        }
    }

}
