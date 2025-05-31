package com.tripmakin.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "trip_participants",
    indexes = {
        @Index(name = "idx_trip_participants_user_id", columnList = "user_id"),
        @Index(name = "idx_trip_participants_trip_id", columnList = "trip_id"),
        @Index(name = "idx_trip_participants_role", columnList = "role")
    }
)
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

    public Integer getParticipantId() {
        return participantId;
    }
    public void setParticipantId(Integer participantId) {
        this.participantId = participantId;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Trip getTrip() {
        return trip;
    }
    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}
