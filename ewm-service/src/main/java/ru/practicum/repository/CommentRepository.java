package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.entity.comments.Comment;

import java.util.List;
import java.util.Map;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
                SELECT c
                FROM Comment c
                JOIN FETCH c.event
                WHERE c.author.id = :userId
            """)
    List<Comment> findAllByAuthorIdWithEvent(@Param("userId") Long userId, Pageable pageable);

    @Query("""
                SELECT c
                FROM Comment c
                JOIN FETCH c.author
                JOIN FETCH c.event
                WHERE c.event.id = :eventId
                  AND c.moderated = true
                ORDER BY c.created DESC
            """)
    List<Comment> findApprovedByEventId(@Param("eventId") Long eventId, Pageable pageable);

    @Query("""
                SELECT c
                FROM Comment c
                JOIN FETCH c.author
                JOIN FETCH c.event
                WHERE c.moderated IS NULL
                ORDER BY c.created ASC
            """)
    List<Comment> findPending(Pageable pageable);

 //!!!!!
    @Query("""
            SELECT c.id, COUNT(c) FROM Comment c
            WHERE c.event.id = :eventId
            AND c.moderated = :moderated
            """)
    Map<Long, Long> findAllByEventIdAndModerated(Long eventId, boolean moderated);
}
