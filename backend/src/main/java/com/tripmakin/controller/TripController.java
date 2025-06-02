package com.tripmakin.controller;

import com.tripmakin.exception.ResourceNotFoundException;
import com.tripmakin.model.Trip;
import com.tripmakin.model.TripParticipant;
import com.tripmakin.repository.TripParticipantRepository;
import com.tripmakin.repository.TripRepository;
import com.tripmakin.service.TripService;
import com.tripmakin.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

@Tag(name = "Trips", description = "Endpoints for managing trips")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @Operation(summary = "Get all trips", description = "Retrieve a list of all trips")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of trips")
    @GetMapping
    public ResponseEntity<Page<Trip>> getTrips(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "tripId,asc") String[] sort,
        @RequestParam(required = false) String status
    ) {
        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Trip> trips = tripService.getTrips(pageable, status);
        return ResponseEntity.ok(trips);
    }

    @Operation(summary = "Get a trip by ID", description = "Retrieve a specific trip by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the trip", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Trip.class))),
        @ApiResponse(responseCode = "404", description = "Trip not found", 
                     content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable Integer id) {
        return ResponseEntity.ok(tripService.getTripById(id));
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
        return ResponseEntity.status(201).body(tripService.createTrip(newTrip));
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
    public ResponseEntity<Trip> updateTrip(@PathVariable Integer id, @Valid @RequestBody Trip updatedTrip) {
        tripService.getTripById(id);
        Trip trip = tripService.updateTrip(id, updatedTrip);
        return ResponseEntity.ok(trip);
    }

    @Operation(summary = "Delete a trip", description = "Remove a trip from the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Trip successfully deleted", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Trip not found", 
                     content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTrip(@PathVariable Integer id) {
        tripService.deleteTrip(id);
        return ResponseEntity.ok(Map.of("message", "Trip deleted"));
    }

    @Autowired
    private TripParticipantRepository tripParticipantRepository;

    @Autowired
    private TripRepository tripRepository;

    @GetMapping("/{id}/participants")
    public List<TripParticipant> getTripParticipants(@PathVariable Integer id) {
        return tripParticipantRepository.findByTrip_TripId(id);
    }

    @DeleteMapping("/{tripId}/participants/{userId}")
    public ResponseEntity<?> leaveTrip(@PathVariable Integer tripId, @PathVariable Integer userId) {
        TripParticipant participant = tripParticipantRepository.findByTrip_TripIdAndUser_UserId(tripId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));
        if ("OWNER".equals(participant.getRole())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Owner cannot leave the trip"));
        }
        tripParticipantRepository.delete(participant);
        return ResponseEntity.ok(Map.of("message", "Left the trip"));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    @GetMapping("/my")
    public List<Trip> getMyTrips(Authentication authentication) {
        String email = authentication.getName();
        return tripService.getTripsForUser(email);
    }
}
