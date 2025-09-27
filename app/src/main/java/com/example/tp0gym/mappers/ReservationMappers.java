// app/src/main/java/com/example/tp0gym/mappers/ReservationMappers.java
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
        // IDs
        String reservationId = dto.id;
        String sessionId = (dto.session != null) ? dto.session.id : null;

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

        // Título: priorizamos nombre de la clase; si no hay, disciplina; si no, "Reserva"
        String title = "Reserva";
        String discipline = "";
        if (dto.session != null && dto.session.classRef != null) {
            String classTitle = dto.session.classRef.title;
            discipline = safe(dto.session.classRef.discipline);
            if (classTitle != null && !classTitle.isEmpty()) {
                title = classTitle;
            } else if (!discipline.isEmpty()) {
                title = discipline;
            }
        }

        // Sede / lugar
        String place = "";
        if (dto.session != null) {
            if (dto.session.branch != null && dto.session.branch.name != null) {
                place = dto.session.branch.name;
            } else if (dto.session.classRef != null) {
                place = safe(dto.session.classRef.locationName);
            }
        }

        // Cupo X/Y
        String cupo = "";
        if (dto.session != null && dto.session.capacity != null && dto.session.reservedCount != null) {
            cupo = "Cupo " + dto.session.reservedCount + "/" + dto.session.capacity;
        }

        // Subtítulo: “<Disciplina o Clase> · <Sede> · <Cupo>”
        String disciplineOrTitle = !discipline.isEmpty()
                ? discipline
                : (dto.session != null && dto.session.classRef != null ? safe(dto.session.classRef.title) : "");
        StringBuilder subtitleBuilder = new StringBuilder();
        if (!disciplineOrTitle.isEmpty()) subtitleBuilder.append(disciplineOrTitle);
        if (!place.isEmpty()) {
            if (subtitleBuilder.length() > 0) subtitleBuilder.append(" · ");
            subtitleBuilder.append(place);
        }
        if (!cupo.isEmpty()) {
            if (subtitleBuilder.length() > 0) subtitleBuilder.append(" · ");
            subtitleBuilder.append(cupo);
        }
        String subtitle = subtitleBuilder.length() == 0 ? "" : subtitleBuilder.toString();

        // Estado (de la reserva)
        String status = resolveStatusForUi(dto.status);

        return new ReservationUiModel(
                reservationId,
                sessionId,
                title,
                subtitle,
                formattedDate,
                status
        );
    }
}