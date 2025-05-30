package com.tripmakin.repository;

import com.tripmakin.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Integer> {
    List<Trip> findByCreatedBy_Email(String email);
    @Query("SELECT t FROM Trip t WHERE t.tripId IN (SELECT tp.trip.tripId FROM TripParticipant tp WHERE tp.user.email = :email)")
    List<Trip> findAllByUserEmail(@Param("email") String email);

    @Query("SELECT t FROM Trip t WHERE t.tripId IN (SELECT tp.trip.tripId FROM TripParticipant tp WHERE tp.user.userId = :userId)")
    List<Trip> findAllByUserId(@Param("userId") Integer userId);
}