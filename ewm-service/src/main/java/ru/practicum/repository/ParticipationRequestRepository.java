package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.entity.request.ParticipationRequest;
import ru.practicum.entity.request.RequestStatus;

import java.util.List;
import java.util.Map;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    ParticipationRequest findParticipationRequestByIdAndRequesterId(Long id, Long requesterId);

    @Query("SELECT r.event.id, COUNT(r) FROM ParticipationRequest r " +
            "WHERE r.requestStatus = :status AND r.event.id IN :eventIds " +
            "GROUP BY r.event.id")
    Map<Long, Long> countConfirmedRequestsByEventIds(@Param("eventIds") List<Long> eventIds,
                                                     @Param("status") RequestStatus status);

    @Query("SELECT r FROM ParticipationRequest r " +
            "WHERE r.id IN :requestIds " +
            "AND r.event.id = :eventId " +
            "AND r.event.initiator.id = :initiatorId")
    List<ParticipationRequest> findAllByIdInAndEventIdAndInitiatorId(
            @Param("requestIds") List<Long> requestIds,
            @Param("eventId") Long eventId,
            @Param("initiatorId") Long initiatorId);

    @Query("SELECT COUNT(r) FROM ParticipationRequest r WHERE r.event.id = :eventId AND r.requestStatus = :status")
    Long countRequestsByEventAndStatus(@Param("eventId") Long eventId,
                                       @Param("status") RequestStatus status);

    Boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    @Query("SELECT r.event.id, COUNT(r) " +
            "FROM ParticipationRequest r " +
            "WHERE r.event.id IN :eventIds " +
            "AND r.requestStatus = :status " +
            "GROUP BY r.event.id")
    List<Object[]> countByEventIdInAndStatus(
            @Param("eventIds") List<Long> eventIds,
            @Param("status") RequestStatus status);
}
