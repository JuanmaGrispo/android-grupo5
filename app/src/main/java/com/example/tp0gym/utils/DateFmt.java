package com.example.tp0gym.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DateFmt {

    private DateFmt() {}

    private static final String UI_PATTERN = "dd/MM - HH:mm 'hs'";

    // Acepta: 2025-09-26T18:00:00Z y 2025-09-26T18:00:00.000Z y con offset ±HH:mm
    private static final String[] ISO_PATTERNS = new String[] {
            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            "yyyy-MM-dd'T'HH:mm:ssX"
    };

    /** Convierte ISO-8601 a "dd/MM - HH:mm hs" en la zona del dispositivo */
    public static String toUi(String iso) {
        if (iso == null || iso.isEmpty()) return "";
        try {
            Date date = parseIso(iso);
            if (date == null) return iso;
            SimpleDateFormat out = new SimpleDateFormat(UI_PATTERN, Locale.getDefault());
            out.setTimeZone(TimeZone.getDefault());
            return out.format(date);
        } catch (Exception e) {
            return iso;
        }
    }

    /** Convierte ISO a millis (útil para ordenar) */
    public static long toMillis(String iso) {
        if (iso == null || iso.isEmpty()) return 0L;
        try {
            Date d = parseIso(iso);
            return d != null ? d.getTime() : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    private static Date parseIso(String iso) {
        for (String p : ISO_PATTERNS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(p, Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // ISO se interpreta en UTC
                return sdf.parse(iso);
            } catch (ParseException ignored) {}
        }
        return null;
    }
}
