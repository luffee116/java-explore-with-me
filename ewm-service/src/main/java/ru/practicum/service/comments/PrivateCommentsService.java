package ru.practicum.service.comments;

import org.springframework.stereotype.Service;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.NewCommentDto;

import java.util.List;

@Service
public interface PrivateCommentsService {

    CommentDto createComment(Long userId, Long eventId, NewCommentDto commentDto);

    void deleteComment(Long userId, Long eventId, Long commentId);

    CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto commentDto);

    List<CommentDto> getAllUserComments(Long userId, int from, int size);
}
