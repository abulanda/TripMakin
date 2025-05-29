package com.tripmakin.repository;

import com.tripmakin.model.TripParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TripParticipantRepository extends JpaRepository<TripParticipant, Integer> {
    List<TripParticipant> findByTrip_TripId(Integer tripId);
}
