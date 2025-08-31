package ru.practicum.service.comments;

import org.springframework.stereotype.Service;
import ru.practicum.dto.comments.CommentDto;

import java.util.List;

@Service
public interface PublicCommentsService {

    List<CommentDto> getApprovedCommentsByEvent(Long eventId, int from, int size);
}
