package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.entity.event.Event;
import ru.practicum.entity.event.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Event findByIdAndInitiatorId(Long id, Long initiatorId);

    @EntityGraph(attributePaths = {"category", "initiator"})
    Optional<Event> findByIdAndState(Long id, EventState state);

    @Query("""
            SELECT e FROM Event e
            WHERE e.state = 'PUBLISHED'
              AND (
                  :text IS NULL OR :text = '' OR
                  LOWER(e.annotation) LIKE CONCAT('%', LOWER(:text), '%') OR
                  LOWER(e.description) LIKE CONCAT('%', LOWER(:text), '%')
              )
              AND (:categories IS NULL OR e.category.id IN :categories)
              AND (:paid IS NULL OR e.paid = :paid)
              AND e.eventDate >= :rangeStart
              AND e.eventDate <= :rangeEnd
              AND (
                   :onlyAvailable = FALSE OR
                   e.participantLimit = 0 OR
                   e.participantLimit > (
                       SELECT COUNT(r) FROM ParticipationRequest r
                       WHERE r.event = e AND r.requestStatus = 'CONFIRMED'
                   )
              )
            """)
    Page<Event> findPublicEvents(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") Boolean onlyAvailable,
            Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.id IN :eventIds AND e.state = 'PUBLISHED'")
    List<Event> findPublishedEventsByIds(@Param("eventIds") List<Long> eventIds);

    @Query("SELECT e FROM Event e " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND e.eventDate >= :rangeStart " +
            "AND e.eventDate <= :rangeEnd")
    Page<Event> searchEventsByAdmin(
            @Param("users") List<Long> users,
            @Param("states") List<EventState> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);


    boolean existsByCategoryId(Long categoryId);

    Set<Event> findAllByIdIn(Set<Long> ids);


}
