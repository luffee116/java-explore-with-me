package ru.practicum.service.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.NewCommentDto;
import ru.practicum.entity.comments.Comment;
import ru.practicum.entity.event.Event;
import ru.practicum.entity.user.User;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.UserMapper;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateCommentsServiceImpl implements PrivateCommentsService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto commentDto) {
        User author = getUser(userId);
        Event event = getEvent(eventId);

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }

        Comment requestComment = Comment.builder()
                .created(LocalDateTime.now())
                .text(commentDto.getText())
                .author(author)
                .event(event)
                .moderated(null)
                .build();

        Comment savedComment = commentRepository.save(requestComment);

        return toFullDto(savedComment, author, event);
    }

    @Override
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        Event event = getEvent(eventId);

        Comment comment = getComment(commentId);

        if (!comment.getEvent().equals(event)) {
            throw new BadRequestException("Comment with id " + commentId + " is not in event " + event);
        }

        User user = getUser(userId);

        if (!comment.getAuthor().equals(user)) {
            throw new BadRequestException("Only authors can delete comments");
        }

        commentRepository.delete(comment);
    }

    @Override
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto commentDto) {
        Event event = getEvent(eventId);

        Comment comment = getComment(commentId);

        if (!comment.getEvent().equals(event)) {
            throw new BadRequestException("Comment with id " + commentId + " is not in event " + event);
        }

        User user = getUser(userId);

        if (!comment.getAuthor().equals(user)) {
            throw new BadRequestException("Only authors can update comments");
        }

        if (commentDto.getText().isEmpty()) {
            throw new BadRequestException("Comment text is empty");
        }

        comment.setText(commentDto.getText());
        comment.setModerated(null);

        Comment updatedComment = commentRepository.save(comment);


        return toFullDto(updatedComment, user, event);
    }

    @Override
    public List<CommentDto> getAllUserComments(Long userId, int from, int size) {
        getUser(userId);

        Pageable pageable = PageRequest.of(from / size, size);

        return commentRepository.findAllByAuthorIdWithEvent(userId, pageable)
                .stream()
                .map(c -> toFullDto(c, c.getAuthor(), c.getEvent()))
                .toList();
    }

    // PRIVATE METHODS
    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment with id " + commentId + " not found")
        );
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id " + userId + " not found")
        );
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id " + eventId + " not found")
        );
    }

    private CommentDto toFullDto(Comment comment, User user, Event event) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .moderated(comment.getModerated())
                .event(EventMapper.toEventShortDto(event))
                .author(UserMapper.toDto(user))
                .build();
    }
}
