package com.tripmakin.controller;

import com.tripmakin.model.Trip;
import com.tripmakin.repository.TripRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.validation.Valid;

@Tag(name = "Trips", description = "Endpoints for managing trips")
@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripRepository tripRepository;

    public TripController(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Operation(summary = "Get all trips", description = "Retrieve a list of all trips")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of trips")
    @GetMapping
    public ResponseEntity<List<Trip>> getTrips() {
        return ResponseEntity.ok(tripRepository.findAll());
    }

    @Operation(summary = "Get a trip by ID", description = "Retrieve a specific trip by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the trip", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Trip.class))),
        @ApiResponse(responseCode = "404", description = "Trip not found", 
                     content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> getTripById(@PathVariable Integer id) {
        Optional<Trip> trip = tripRepository.findById(id);
        if (trip.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Trip not found", "status", 404));
        }
        return ResponseEntity.ok(trip.get());
    }

    @Operation(summary = "Create a new trip", description = "Add a new trip to the system")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Trip successfully created", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Trip.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed", 
                     content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<Trip> createTrip(@Valid @RequestBody Trip newTrip) {
        Trip savedTrip = tripRepository.save(newTrip);
        return ResponseEntity.status(201).body(savedTrip);
    }

    @Operation(summary = "Update an existing trip", description = "Update the details of an existing trip")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Trip successfully updated", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Trip.class))),
        @ApiResponse(responseCode = "404", description = "Trip not found", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Validation failed", 
                     content = @Content(mediaType = "application/json"))
    })
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

    @Operation(summary = "Delete a trip", description = "Remove a trip from the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Trip successfully deleted", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Trip not found", 
                     content = @Content(mediaType = "application/json"))
    })
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
