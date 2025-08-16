package ru.practicum.service.comments;

import org.springframework.stereotype.Service;
import ru.practicum.dto.comments.CommentDto;

import java.util.List;

@Service
public interface AdminCommentsService {
    List<CommentDto> getPendingComments(int from, int size);

    CommentDto setModerationStatus(Long commentId, boolean approved);
}
