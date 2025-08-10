package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.StatsDto;
import ru.practicum.service.StatsService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto save(@RequestBody final HitDto hitDto) {
        log.info("Create endpoint hit: {}", hitDto);
        return statsService.save(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String  end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique
    ) {
        log.info("Getting stats from parameters: start={}, end={}, uri={}, unique={}", start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }
}