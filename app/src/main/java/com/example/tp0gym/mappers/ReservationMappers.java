package com.example.tp0gym.mappers;

import com.example.tp0gym.repository.dto.ReservationDto;
import com.example.tp0gym.ui.model.ReservationUiModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class ReservationMappers {

    private ReservationMappers() {}

    private static Date parseStartAt(String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;

        String[] patterns = {
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSX",
                "yyyy-MM-dd'T'HH:mm:ssX",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss"
        };
        for (String p : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(p, Locale.US);
                if (!p.contains("X")) {
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                }
                return sdf.parse(raw);
            } catch (ParseException ignored) {}
        }
        return null;
    }

    private static String safe(String s) { return s == null ? "" : s; }

    private static String resolveStatusForUi(String backendStatus) {
        if (backendStatus == null) return "-";
        switch (backendStatus.toUpperCase(Locale.ROOT)) {
            case "CONFIRMED": return "Confirmada";
            case "CANCELED":  return "Cancelada";
            case "SCHEDULED": return "Programada";
            case "PENDING":   return "Pendiente";
            case "WAITLIST":  return "Lista de espera";
            default:          return backendStatus;
        }
    }

    public static ReservationUiModel toUi(ReservationDto dto) {
        // Fecha legible
        String formattedDate = "-";
        if (dto.session != null && dto.session.startAt != null) {
            Date parsed = parseStartAt(dto.session.startAt);
            if (parsed != null) {
                SimpleDateFormat out = new SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault());
                out.setTimeZone(TimeZone.getDefault());
                formattedDate = out.format(parsed);
            }
        }

        // Subtítulo: cupos (si existen)
        String subtitle = "";
        if (dto.session != null && dto.session.capacity != null && dto.session.reservedCount != null) {
            subtitle = "Cupo: " + dto.session.reservedCount + "/" + dto.session.capacity;
        }

        // Status traducido
        String status = resolveStatusForUi(dto.status);

        // Título: por ahora usamos el id de la sesión o “Reserva”
        String title = (dto.session != null && dto.session.id != null)
                ? "Sesión " + dto.session.id
                : "Reserva";

        return new ReservationUiModel(
                title,
                subtitle,
                formattedDate,
                status
        );
    }
}