package com.tripmakin.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "invitations",
    indexes = {
        @Index(name = "idx_invitations_trip_id", columnList = "trip_id"),
        @Index(name = "idx_invitations_invited_user_id", columnList = "invited_user_id"),
        @Index(name = "idx_invitations_inviter_id", columnList = "inviter_id"),
        @Index(name = "idx_invitations_status", columnList = "status")
    }
)
@Getter
@Setter
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer invitationId;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "invited_user_id")
    private User invitedUser;

    @ManyToOne
    @JoinColumn(name = "inviter_id")
    private User inviter;

    private String status;

    private LocalDateTime sentAt;

    public Invitation() {
        this.status = "PENDING";
        this.sentAt = LocalDateTime.now();
    }
}
