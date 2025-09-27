package com.example.tp0gym.ui.screens;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tp0gym.R;
import com.example.tp0gym.modelo.AttendanceDto;
import com.example.tp0gym.repository.AttendanceRepository;
import com.example.tp0gym.utils.AppPreferences;
import com.example.tp0gym.utils.DateFmt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class HistoryFragment extends Fragment {

    private LinearLayout cardsContainer;

    @Inject AttendanceRepository repo;
    @Inject AppPreferences appPrefs;

    public HistoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        cardsContainer = root.findViewById(R.id.cardsContainer);

        loadHistory();
    }

    // ============================
    // Backend (sin mocks)
    // ============================
    private void loadHistory() {
        final String token = appPrefs.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "Sesión no iniciada. Iniciá sesión.", Toast.LENGTH_SHORT).show();
            renderError("Sesión no iniciada. Iniciá sesión.");
            return;
        }

        showLoading();

        repo.getMyAttendance(token).enqueue(new Callback<List<AttendanceDto>>() {
            @Override public void onResponse(@NonNull Call<List<AttendanceDto>> call,
                                             @NonNull Response<List<AttendanceDto>> resp) {
                if (!isAdded()) return;

                if (resp.isSuccessful() && resp.body() != null) {
                    renderFromDto(resp.body());
                } else if (resp.code() == 401) {
                    Toast.makeText(requireContext(), "Sesión expirada. Iniciá sesión de nuevo.", Toast.LENGTH_SHORT).show();
                    renderError("Sesión expirada. Iniciá sesión de nuevo.");
                } else {
                    renderError("No se pudo cargar el historial (" + resp.code() + ")");
                }
            }

            @Override public void onFailure(@NonNull Call<List<AttendanceDto>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                renderError("Fallo de red: " + t.getMessage());
            }
        });
    }

    private void showLoading() {
        cardsContainer.removeAllViews();
        TextView tv = makeInfoText("Cargando historial...");
        cardsContainer.addView(tv);
    }

    private void renderError(String msg) {
        cardsContainer.removeAllViews();
        cardsContainer.addView(makeInfoText(msg));
    }

    private void renderEmpty() {
        cardsContainer.removeAllViews();
        cardsContainer.addView(makeInfoText("Sin historial por ahora."));
    }

    private TextView makeInfoText(String text) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        tv.setAlpha(0.9f);
        tv.setPadding(dp(8), dp(16), dp(8), dp(16));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        return tv;
    }

    private void renderFromDto(List<AttendanceDto> list) {
        // Mapear DTO -> items UI
        List<UiItem> items = new ArrayList<>();
        for (AttendanceDto dto : list) {
            AttendanceDto.SessionDto s = dto.getSession();
            if (s == null) continue;
            String title   = s.getClassRef() != null && s.getClassRef().getTitle() != null
                    ? s.getClassRef().getTitle() : "Clase";
            String dateUi  = DateFmt.toUi(s.getStartAt());
            String teacher = (s.getClassRef() != null) ? nullToEmpty(s.getClassRef().getInstructorName()) : "";
            String discipline = (s.getClassRef() != null) ? nullToEmpty(s.getClassRef().getDiscipline()) : "";
            String locationName = (s.getClassRef() != null) ? nullToEmpty(s.getClassRef().getLocationName()) : "";
            int durationMin = s.getDurationMin();

            // Este endpoint representa asistencias efectivas -> siempre "Asistida"
            boolean attended = true;

            items.add(new UiItem(
                    title,
                    buildSubtitle(dateUi, teacher, locationName, durationMin),
                    locationName,
                    durationMin,
                    attended));
        }

        if (items.isEmpty()) {
            renderEmpty();
        } else {
            renderCards(items);
        }
    }

    private String buildSubtitle(String dateUi, String teacher, String locationName, int durationMin) {
        StringBuilder subtitle = new StringBuilder(dateUi);
        
        if (durationMin > 0) {
            subtitle.append(" • ").append(durationMin).append(" min");
        }
        
        return subtitle.toString();
    }



    private void renderCards(List<UiItem> data) {
        @ColorInt int cWhite  = ContextCompat.getColor(requireContext(), R.color.white);
        @ColorInt int cYellow = ContextCompat.getColor(requireContext(), R.color.yellow);
        @ColorInt int cGray   = ContextCompat.getColor(requireContext(), R.color.gray);
        @ColorInt int cCardBg = 0xFF1A1A1A;
        @ColorInt int cStroke = 0xFF2A2A2A;

        cardsContainer.removeAllViews();

        for (UiItem it : data) {
            View card = buildCard(it, cWhite, cYellow, cGray, cCardBg, cStroke);
            cardsContainer.addView(card);
        }
    }

    // ===== UI item =====
    static class UiItem {
        final String title, subtitle, locationName;
        final int durationMin;
        final boolean attended;
        UiItem(String t, String s, String l, int d, boolean a) {
             title=t; subtitle=s; locationName=l; durationMin=d; attended=a;
        }
    }

    // ===== Card (CardView + pill TextView) =====
    private View buildCard(UiItem it,
                           @ColorInt int cWhite,
                           @ColorInt int cYellow,
                           @ColorInt int cGray,
                           @ColorInt int cCardBg,
                           @ColorInt int cStroke) {

        CardView card = new CardView(requireContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setMarginsDp(lp, 0, 8, 0, 8);
        card.setLayoutParams(lp);
        card.setRadius(dp(16));
        card.setUseCompatPadding(true);
        card.setCardBackgroundColor(cCardBg);
        card.setCardElevation(dp(2));

        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(14), dp(14), dp(14), dp(14));
        row.setBackground(makeRect(cCardBg, dp(16), cStroke, 1));
        card.addView(row);

        LinearLayout col = new LinearLayout(requireContext());
        col.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpCol = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lpCol.setMarginStart(dp(0)); lpCol.setMarginEnd(dp(10));
        col.setLayoutParams(lpCol);
        row.addView(col);

        TextView tvTitle = new TextView(requireContext());
        tvTitle.setText(it.title);
        tvTitle.setTextColor(cWhite);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tvTitle.setTypeface(tvTitle.getTypeface(), android.graphics.Typeface.BOLD);
        col.addView(tvTitle);

        TextView tvSubtitle = new TextView(requireContext());
        tvSubtitle.setText(it.subtitle);
        tvSubtitle.setTextColor(cWhite);
        tvSubtitle.setAlpha(0.8f);
        tvSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        col.addView(tvSubtitle);

        // Add location name if available
        if (it.locationName != null && !it.locationName.isEmpty()) {
            TextView tvLocation = new TextView(requireContext());
            tvLocation.setText(it.locationName);
            tvLocation.setTextColor(cWhite);
            tvLocation.setAlpha(0.7f);
            tvLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tvLocation.setPadding(0, dp(2), 0, 0);
            col.addView(tvLocation);
        }

        // Add duration if available
        if (it.durationMin > 0) {
            TextView tvDuration = new TextView(requireContext());
            tvDuration.setText(it.durationMin + " min");
            tvDuration.setTextColor(cWhite);
            tvDuration.setAlpha(0.7f);
            tvDuration.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tvDuration.setPadding(0, dp(2), 0, 0);
            col.addView(tvDuration);
        }

        TextView pill = new TextView(requireContext());
        pill.setText(it.attended ? "Asistida" : "Ausente");
        pill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        pill.setTypeface(pill.getTypeface(), android.graphics.Typeface.BOLD);
        pill.setPadding(dp(10), dp(6), dp(10), dp(6));
        pill.setIncludeFontPadding(false);
        if (it.attended) {
            pill.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            pill.setBackground(makePill(cYellow, 0, 0));
        } else {
            pill.setTextColor(cWhite);
            pill.setBackground(makePill(0x00000000, cGray, dp(1)));
        }
        row.addView(pill);

        card.setOnClickListener(v -> Toast.makeText(requireContext(), it.title, Toast.LENGTH_SHORT).show());
        return card;
    }

    // ===== Utils =====
    private int dp(int v) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, v,
                requireContext().getResources().getDisplayMetrics()
        );
    }

    private void setMarginsDp(ViewGroup.MarginLayoutParams lp, int l, int t, int r, int b) {
        lp.setMargins(dp(l), dp(t), dp(r), dp(b));
    }

    private GradientDrawable makeRect(@ColorInt int fill, int radiusPx, @ColorInt int strokeColor, int strokeDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(fill);
        d.setCornerRadius(radiusPx);
        d.setStroke(dp(strokeDp), strokeColor);
        return d;
    }

    private GradientDrawable makePill(@ColorInt int fill, @ColorInt int strokeColor, int strokeDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(fill);
        d.setCornerRadius(dp(999));
        if (strokeDp > 0) d.setStroke(strokeDp, strokeColor);
        return d;
    }

    private String nullToEmpty(String s) { return s == null ? "" : s; }
}