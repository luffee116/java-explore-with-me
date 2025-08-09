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
    public StatsClient(@Value("${stats-server.url}") String serverUrl) {
        log.info("url: " + serverUrl);
        this.restClient = RestClient.builder()
                .baseUrl(serverUrl)
                .build();
        this.url = serverUrl;
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
        String formattedStart = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String formattedEnd = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return restClient.get()
                .uri(uriBuilder -> {
                    log.info("url before /stats: " + url);
                    uriBuilder.path("/stats");
                    uriBuilder.queryParam("start", formattedStart);
                    uriBuilder.queryParam("end", formattedEnd);

                    if (uris != null && !uris.isEmpty()) {
                        uriBuilder.queryParam("uris", uris.toArray());
                    }
                    return uriBuilder.queryParam("unique", unique).build();
                })
                .retrieve()
                .toEntity(Object.class);

    }
}
