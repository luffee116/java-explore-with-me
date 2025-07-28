package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class StatsClient {
    private final RestClient restClient;
    private final String url;

    @Autowired
    public StatsClient(@Value("http://localhost:9009") String serverUrl) {
        log.info("Stats server url: " + serverUrl);
        restClient = RestClient.builder()
                .baseUrl(serverUrl)
                .build();
        url = serverUrl;
    }

    public ResponseEntity<Object> saveHit(HitDto hitDto) {
        return restClient.post()
                .uri(uriBuilder -> uriBuilder.path("/hit").build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitDto)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start,
                                           LocalDateTime end,
                                           List<String> uris,
                                           Boolean unique) {
        String formattedStart = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String formattedEnd = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.queryParam("start", formattedStart);
                    uriBuilder.queryParam("end", formattedEnd);

                    if (uris != null && !uris.isEmpty()) {
                        uriBuilder.queryParam("uris", String.join(","), uris);
                    }
                    return uriBuilder.queryParam("unique", unique).build();
                })
                .retrieve()
                .toEntity(Object.class);

    }
}
