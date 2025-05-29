package com.tripmakin.repository;

import com.tripmakin.model.TripParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TripParticipantRepository extends JpaRepository<TripParticipant, Integer> {
    List<TripParticipant> findByTrip_TripId(Integer tripId);
    Optional<TripParticipant> findByTrip_TripIdAndUser_UserId(Integer tripId, Integer userId);
}
