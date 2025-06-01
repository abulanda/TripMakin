package com.tripmakin.service;

import com.tripmakin.exception.ResourceNotFoundException;
import com.tripmakin.model.Trip;
import com.tripmakin.model.TripParticipant;
import com.tripmakin.model.User;
import com.tripmakin.repository.TripParticipantRepository;
import com.tripmakin.repository.TripRepository;
import com.tripmakin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripParticipantRepository tripParticipantRepository;

    @Autowired
    public TripService(TripRepository tripRepository, UserRepository userRepository, TripParticipantRepository tripParticipantRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.tripParticipantRepository = tripParticipantRepository;
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public Trip getTripById(Integer id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
    }

    public Trip createTrip(Trip newTrip) {
        String email = newTrip.getCreatedBy() != null ? newTrip.getCreatedBy().getEmail() : null;
        if (email == null) {
            throw new IllegalArgumentException("Email użytkownika jest wymagany");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        newTrip.setCreatedBy(user);
        Trip savedTrip = tripRepository.save(newTrip);

        TripParticipant participant = new TripParticipant();
        participant.setTrip(savedTrip);
        participant.setUser(user);
        participant.setRole("OWNER");
        participant.setStatus("ACTIVE");
        tripParticipantRepository.save(participant);

        return savedTrip;
    }

    public Trip updateTrip(Integer id, Trip updatedTrip) {
        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        updatedTrip.setTripId(existingTrip.getTripId());
        return tripRepository.save(updatedTrip);
    }

    public void deleteTrip(Integer id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
        tripRepository.delete(trip);
    }

    public List<Trip> getTripsForUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        List<Trip> trips = tripRepository.findAllByUserId(user.getUserId());
        LocalDate today = LocalDate.now();
        for (Trip trip : trips) {
            boolean changed = false;
            if (trip.getStartDate() != null && trip.getEndDate() != null) {
                if (today.isBefore(trip.getStartDate())) {
                    if (!"PLANNED".equals(trip.getStatus())) {
                        trip.setStatus("PLANNED");
                        changed = true;
                    }
                } else if ((today.isEqual(trip.getStartDate()) || today.isAfter(trip.getStartDate()))
                        && today.isBefore(trip.getEndDate().plusDays(1))) {
                    if (!"IN_PROGRESS".equals(trip.getStatus())) {
                        trip.setStatus("IN_PROGRESS");
                        changed = true;
                    }
                } else if (today.isAfter(trip.getEndDate())) {
                    if (!"FINISHED".equals(trip.getStatus())) {
                        trip.setStatus("FINISHED");
                        changed = true;
                    }
                }
            }
            if (changed) {
                tripRepository.save(trip);
            }
        }
        return trips;
    }

    public Page<Trip> getTrips(Pageable pageable, String status) {
        if (status != null && !status.isEmpty()) {
            return tripRepository.findByStatus(status, pageable);
        }
        return tripRepository.findAll(pageable);
    }
}