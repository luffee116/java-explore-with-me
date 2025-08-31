package ru.practicum.service.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.entity.comments.Comment;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.UserMapper;
import ru.practicum.repository.CommentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCommentsServiceImpl implements AdminCommentsService {

    private final CommentRepository commentRepository;

    @Override
    public List<CommentDto> getPendingComments(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findPending(pageable);

        return comments.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto setModerationStatus(Long commentId, boolean approved) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        comment.setModerated(approved);
        commentRepository.save(comment);

        return toDto(comment);
    }

    private CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .moderated(comment.getModerated())
                .created(comment.getCreated())
                .event(EventMapper.toEventShortDto(comment.getEvent()))
                .author(UserMapper.toDto(comment.getAuthor()))
                .build();
    }
}

