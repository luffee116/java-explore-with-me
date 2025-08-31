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

    @Query("""
            SELECT c.event.id, COUNT(c)
            FROM Comment c
            WHERE c.event.id IN :eventIds
              AND c.moderated = :moderated
            GROUP BY c.event.id
            """)
    List<Object[]> countCommentsByEventIdAndModerated(@Param("eventIds") List<Long> eventIds,
                                                      @Param("moderated") boolean moderated);

    @Query("""
            SELECT c.event.id, COUNT(c)
            FROM Comment c
            WHERE c.event.id IN :eventIds
              AND c.moderated = :moderated
            GROUP BY c.event.id
            """)
    Map<Long, Long> countCommentsByModeratedAndEventId(@Param("eventIds") List<Long> eventIds,
                                                      @Param("moderated") boolean moderated);

    @Query("""
            SELECT COUNT(c)
            FROM Comment c
            WHERE c.event.id = :eventId
              AND c.moderated = :moderated
            GROUP BY c.event.id
            """)
    Long findCommentCountByEventIdAndModerated(@Param("eventId") Long eventId,
                                               @Param("moderated") boolean moderated);
}
