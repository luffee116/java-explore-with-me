package ru.practicum.entity.event;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.entity.category.Category;
import ru.practicum.entity.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(name = "description", length = 7000, nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "annotation", length = 2000, nullable = false, columnDefinition = "text")
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Column(name = "created_on",nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false, name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "participant_limit",nullable = false)
    private Integer participantLimit;

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private EventState state;

    @Column(name = "views", nullable = false)
    private Long views;

    @Column(name = "confirmed_requests")
    private Long confirmedRequests;

    @Column(name = "comments")
    private Long comments;
}
