package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.StatsDto;
import ru.practicum.entity.Hit;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public HitDto save(HitDto hitDto) {
        log.info("Creating hit: {}", hitDto);
        Hit hit = toHit(hitDto);
        Hit savedHit = statsRepository.save(hit);
        log.info("Successfully created hit: {}", hit);
        return toHitDto(savedHit);
    }

    @Override
    public List<StatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        log.info("Getting statistics from: {}, to {} (URIs: {}, unique: {})", start, end, uris, unique);

        if (start.isBlank() || end.isBlank()) {
            throw new ValidationException("Start and end must be provided");
        }

        LocalDateTime startTime = parseDate(start);
        LocalDateTime endTime = parseDate(end);


        if (startTime.isAfter(endTime)) {
            throw new ValidationException("Start date cannot be after end date");
        }

        return unique ?
                statsRepository.findUniqueStatsByUrisAndTimestampBetween(startTime, endTime, uris)
                : statsRepository.findStatisticsByUrisAndTimestampBetween(startTime, endTime, uris);
    }

    private LocalDateTime parseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }

    private Hit toHit(HitDto hitDto) {
        return Hit.builder()
                .id(hitDto.getId())
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private HitDto toHitDto(Hit hit) {
        return HitDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp())
                .build();
    }
}