// ui/screens/HistoryFragment.java
package com.example.tp0gym.ui.screens;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private Button backButton;

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
        backButton     = root.findViewById(R.id.backButton);

        backButton.setOnClickListener(v ->
                NavHostFragment.findNavController(HistoryFragment.this).navigateUp()
        );

        loadHistory();
    }

    // ============================
    // Carga desde backend + fallbacks
    // ============================
    private void loadHistory() {
        final String token = appPrefs.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "Sesión no iniciada. Iniciá sesión.", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigate(R.id.loginFragment);
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
                    NavHostFragment.findNavController(HistoryFragment.this).navigate(R.id.loginFragment);
                } else {
                    Toast.makeText(requireContext(), "No se pudo cargar el historial (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                    renderFromMock();
                }
            }

            @Override public void onFailure(@NonNull Call<List<AttendanceDto>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                renderFromMock();
            }
        });
    }

    private void showLoading() {
        cardsContainer.removeAllViews();
        TextView tv = new TextView(requireContext());
        tv.setText("Cargando historial...");
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        tv.setPadding(dp(8), dp(16), dp(8), dp(16));
        cardsContainer.addView(tv);
    }

    private void renderFromDto(List<AttendanceDto> list) {
        List<UiItem> items = new ArrayList<>();
        for (AttendanceDto dto : list) {
            AttendanceDto.SessionDto s = dto.getSession();
            String title   = (s != null && s.getClassRef() != null) ? safe(s.getClassRef().getTitle()) : "Clase";
            String dateUi  = (s != null) ? DateFmt.toUi(s.getStartAt()) : "";
            String teacher = (s != null && s.getClassRef() != null) ? safe(s.getClassRef().getInstructorName()) : "";
            String discipline = (s != null && s.getClassRef() != null) ? safe(s.getClassRef().getDiscipline()) : "";

            // Este endpoint devuelve ATTENDANCE (o sea, asistencias efectivas)
            boolean attended = true;

            items.add(new UiItem(pickEmoji(title, discipline), title, buildSubtitle(dateUi, teacher), teacher, attended));
        }
        renderCards(items);
    }

    private void renderFromMock() {
        List<UiItem> items = new ArrayList<>();
        items.add(new UiItem("💪","Funcional Training","12/09 - 18:00 hs • Profesor: Juan Pérez","Juan Pérez", true));
        items.add(new UiItem("🧘","Yoga","15/09 - 10:00 hs • Profesora: Ana Gómez","Ana Gómez", false));
        items.add(new UiItem("🏋️","Crossfit","20/09 - 19:00 hs • Profesor: Carlos Ruiz","Carlos Ruiz", true));
        items.add(new UiItem("🥊","Boxeo","22/09 - 19:30 hs • Profesora: María Díaz","María Díaz", true));
        renderCards(items);
    }

    private void renderCards(List<UiItem> data) {
        @ColorInt int cWhite  = ContextCompat.getColor(requireContext(), R.color.white);
        @ColorInt int cYellow = ContextCompat.getColor(requireContext(), R.color.yellow);
        @ColorInt int cGray   = ContextCompat.getColor(requireContext(), R.color.gray);
        @ColorInt int cCardBg = 0xFF1A1A1A;
        @ColorInt int cStroke = 0xFF2A2A2A;

        cardsContainer.removeAllViews();

        if (data.isEmpty()) {
            TextView tv = new TextView(requireContext());
            tv.setText("No hay registros todavía.");
            tv.setTextColor(cWhite);
            tv.setPadding(dp(8), dp(16), dp(8), dp(16));
            cardsContainer.addView(tv);
            return;
        }

        for (UiItem it : data) {
            View card = buildCard(it, cWhite, cYellow, cGray, cCardBg, cStroke);
            cardsContainer.addView(card);
        }
    }

    // ============================
    // UI item + helpers
    // ============================
    static class UiItem {
        final String emoji, title, subtitle, teacher;
        final boolean attended;
        UiItem(String e, String t, String s, String te, boolean a) {
            emoji=e; title=t; subtitle=s; teacher=te; attended=a;
        }
    }

    private String buildSubtitle(String dateUi, @Nullable String teacher) {
        if (teacher == null || teacher.isEmpty()) return dateUi;
        return dateUi + " • Profesor: " + teacher;
    }

    private String pickEmoji(String title, String discipline) {
        String t = (title == null ? "" : title).toLowerCase();
        String d = (discipline == null ? "" : discipline).toLowerCase();
        String src = t + " " + d;
        if (src.contains("yoga")) return "🧘";
        if (src.contains("cross")) return "🏋️";
        if (src.contains("box"))  return "🥊";
        if (src.contains("spin") || src.contains("bike")) return "🚴";
        if (src.contains("funcion")) return "💪";
        return "💪";
    }

    private String safe(String s) { return s == null ? "" : s; }

    // ============================
    // Card (CardView + pill TextView)
    // ============================
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

        TextView tvEmoji = new TextView(requireContext());
        LinearLayout.LayoutParams lpEmoji = new LinearLayout.LayoutParams(dp(40), dp(40));
        tvEmoji.setLayoutParams(lpEmoji);
        tvEmoji.setGravity(Gravity.CENTER);
        tvEmoji.setText(it.emoji);
        tvEmoji.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tvEmoji.setTextColor(cWhite);
        row.addView(tvEmoji);

        LinearLayout col = new LinearLayout(requireContext());
        col.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpCol = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lpCol.setMarginStart(dp(10)); lpCol.setMarginEnd(dp(10));
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

    // ============================
    // Utils
    // ============================
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
}
