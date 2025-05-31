package com.tripmakin.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "trip_participants",
    indexes = {
        @Index(name = "idx_trip_participants_user_id", columnList = "user_id"),
        @Index(name = "idx_trip_participants_trip_id", columnList = "trip_id"),
        @Index(name = "idx_trip_participants_role", columnList = "role")
    }
)
@Getter
@Setter
public class TripParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Integer participantId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "status", length = 50)
    private String status;

    public TripParticipant() {
        this.joinedAt = LocalDateTime.now();
    }

}
