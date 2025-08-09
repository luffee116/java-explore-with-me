package ru.practicum.service.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.entity.compilation.Compilation;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.repository.CompilationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationPublicServiceImpl implements CompilationPublicService {
    private final CompilationRepository compilationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);

        List<Compilation> compilations = pinned != null ?
                compilationRepository.findAllByPinned(pinned, page) :
                compilationRepository.findAll(page).getContent();

        return compilations.stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found with id: " + compId));

        return CompilationMapper.toDto(compilation);
    }

}
