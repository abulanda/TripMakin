package com.tripmakin.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
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

    public Integer getInvitationId() {
        return invitationId;
    }
    public void setInvitationId(Integer invitationId) {
        this.invitationId = invitationId;
    }
    public Trip getTrip() {
        return trip;
    }
    public void setTrip(Trip trip) {
        this.trip = trip;
    }
    public User getInvitedUser() {
        return invitedUser;
    }
    public void setInvitedUser(User invitedUser) {
        this.invitedUser = invitedUser;
    }
    public User getInviter() {
        return inviter;
    }
    public void setInviter(User inviter) {
        this.inviter = inviter;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
