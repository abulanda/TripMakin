package com.tripmakin.repository;

import com.tripmakin.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Integer> {
    List<Invitation> findByInvitedUser_UserId(Integer userId);
    List<Invitation> findByTrip_TripId(Integer tripId);
}
