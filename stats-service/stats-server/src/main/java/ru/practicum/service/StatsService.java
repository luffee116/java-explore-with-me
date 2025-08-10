package ru.practicum.service;

import ru.practicum.HitDto;
import ru.practicum.StatsDto;

import java.util.List;

public interface StatsService {
    HitDto save(HitDto endpointHitDto);

    List<StatsDto> getStats(String start,
                            String end,
                            List<String> uris,
                            boolean unique);

}