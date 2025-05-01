package com.tripmakin.controller;

import com.tripmakin.model.Trip;
import com.tripmakin.repository.TripRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripRepository tripRepository;

    public TripController(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @GetMapping
    public ResponseEntity<List<Trip>> getTrips() {
        return ResponseEntity.ok(tripRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTripById(@PathVariable Integer id) {
        Optional<Trip> trip = tripRepository.findById(id);
        if (trip.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Trip not found", "status", 404));
        }
        return ResponseEntity.ok(trip.get());
    }

    @PostMapping
    public ResponseEntity<Trip> createTrip(@Valid @RequestBody Trip newTrip) {
        Trip savedTrip = tripRepository.save(newTrip);
        return ResponseEntity.status(201).body(savedTrip);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateTrip(@PathVariable Integer id, @Valid @RequestBody Trip updatedTrip) {
        Optional<Trip> existingTrip = tripRepository.findById(id);
        if (existingTrip.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Trip not found", "status", 404));
        }
    
        updatedTrip.setTripId(id);
        Trip savedTrip = tripRepository.save(updatedTrip);
        return ResponseEntity.ok(savedTrip);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTrip(@PathVariable Integer id) {
        Optional<Trip> trip = tripRepository.findById(id);
        if (trip.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Trip not found", "status", 404));
        }
        tripRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Trip deleted"));
    }
}
