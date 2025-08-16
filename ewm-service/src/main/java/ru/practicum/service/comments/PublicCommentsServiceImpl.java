package ru.practicum.service.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.entity.comments.Comment;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.UserMapper;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicCommentsServiceImpl implements PublicCommentsService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CommentDto> getApprovedCommentsByEvent(Long eventId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }

        List<Comment> comments = commentRepository.findApprovedByEventId(eventId, pageable);

        return comments.stream().map(
                c -> CommentDto.builder()
                        .event(EventMapper.toEventShortDto(c.getEvent()))
                        .id(c.getId())
                        .text(c.getText())
                        .author(UserMapper.toDto(c.getAuthor()))
                        .created(c.getCreated())
                        .moderated(c.getModerated())
                        .build()
        ).collect(Collectors.toList());
    }
}
