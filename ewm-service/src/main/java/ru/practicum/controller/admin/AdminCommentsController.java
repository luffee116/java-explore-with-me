package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.service.comments.AdminCommentsService;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentsController {
    private final AdminCommentsService adminCommentsService;

    @GetMapping("/pending")
    public List<CommentDto> getPendingComments(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return adminCommentsService.getPendingComments(from, size);
    }

    @PatchMapping("/{commentId}/approve")
    public CommentDto approveComment(@PathVariable Long commentId) {
        return adminCommentsService.setModerationStatus(commentId, true);
    }

    @PatchMapping("/{commentId}/reject")
    public CommentDto rejectComment(@PathVariable Long commentId) {
        return adminCommentsService.setModerationStatus(commentId, false);
    }
}
