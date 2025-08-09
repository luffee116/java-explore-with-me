package ru.practicum.service.statsClient;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.StatsClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventStatsClient {
    private final StatsClient statsClient;
    private final String appName = "ewm-main-service";

    public void recordEventView(HttpServletRequest request) {
        HitDto hitDto = HitDto.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        ResponseEntity<Object> response = statsClient.saveHit(hitDto);
        log.debug("Stats service response: {}", response.getStatusCode());
    }

    public void recordEventView(HttpServletRequest request, Long eventId) {
        HitDto hitDto = HitDto.builder()
                .app(appName)
                .uri("/events/" + eventId)
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        ResponseEntity<Object> response = statsClient.saveHit(hitDto);
        log.debug("Stats service response: {}", response.getStatusCode());
    }

    public Map<Long, Long> getEventsViews(List<Long> eventIds) {
        LocalDateTime start = LocalDateTime.now().minusYears(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        ResponseEntity<Object> response = statsClient.getStats(start, end, uris, true);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return parseStatsResponse(response.getBody(), eventIds);
        }
        return Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Long> parseStatsResponse(Object body, List<Long> eventIds) {
        try {
            List<Map<String, Object>> stats = (List<Map<String, Object>>) body;
            return stats.stream()
                    .filter(stat -> stat.get("uri") != null)
                    .collect(Collectors.toMap(
                            stat -> extractEventId((String) stat.get("uri")),
                            stat -> ((Number) stat.get("hits")).longValue(),
                            (existing, replacement) -> existing
                    ));
        } catch (ClassCastException e) {
            log.error("Failed to parse stats response", e);
            return eventIds.stream()
                    .collect(Collectors.toMap(id -> id, id -> 0L));
        }
    }

    private Long extractEventId(String uri) {
        try {
            return Long.parseLong(uri.substring("/events/".length()));
        } catch (Exception e) {
            log.error("Invalid URI format: {}", uri);
            return null;
        }
    }
}