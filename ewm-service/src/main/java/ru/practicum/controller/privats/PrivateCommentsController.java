package ru.practicum.controller.privats;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.NewCommentDto;
import ru.practicum.service.comments.PrivateCommentsService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
public class PrivateCommentsController {
    private final PrivateCommentsService privateCommentsService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable("userId") Long userId,
                                    @RequestParam Long eventId,
                                    @RequestBody NewCommentDto comment) {
        return privateCommentsService.createComment(userId, eventId, comment);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable("userId") Long userId,
                              @RequestParam Long eventId,
                              @PathVariable("commentId") Long commentId) {
        privateCommentsService.deleteComment(userId, eventId, commentId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable("userId") Long userId,
                                    @RequestParam Long eventId,
                                    @PathVariable("commentId") Long commentId,
                                    @RequestBody NewCommentDto comment) {
        return privateCommentsService.updateComment(userId, eventId, commentId, comment);
    }

    // ДОБАВИТЬ PAGEABLE
    @GetMapping
    public List<CommentDto> getAllUserComments(@PathVariable("userId") Long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        return privateCommentsService.getAllUserComments(userId, from, size);
    }
}
