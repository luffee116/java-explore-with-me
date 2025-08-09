package ru.practicum.service.compilations;

import org.springframework.stereotype.Service;
import ru.practicum.dto.compilations.CompilationDto;

import java.util.List;

@Service
public interface CompilationPublicService {

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(Long compId);
}
