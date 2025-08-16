package ru.practicum.entity.comments;


import jakarta.persistence.*;
import lombok.*;
import ru.practicum.entity.event.Event;
import ru.practicum.entity.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
@Builder(toBuilder = true)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "moderated")
    private Boolean moderated;

    @Column(name = "created")
    private LocalDateTime created;

}
