package ru.practicum.service.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.dto.compilations.NewCompilationDto;
import ru.practicum.dto.compilations.UpdateCompilationRequest;
import ru.practicum.entity.compilation.Compilation;
import ru.practicum.entity.event.Event;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompilationAdminServiceImpl implements CompilationAdminService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Set<Event> events = newCompilationDto.getEvents() != null ?
                eventRepository.findAllByIdIn(newCompilationDto.getEvents()) :
                Collections.emptySet();

        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation not found with id: " + compId);
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found with id: " + compId));

        if (updateRequest.getEvents() != null) {
            Set<Event> events = eventRepository.findAllByIdIn(updateRequest.getEvents());
            compilation.setEvents(events);
        }
        if (updateRequest.getPinned() != null) {
            compilation.setPinned(updateRequest.getPinned());
        }
        if (updateRequest.getTitle() != null) {
            compilation.setTitle(updateRequest.getTitle());
        }

        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }
}
