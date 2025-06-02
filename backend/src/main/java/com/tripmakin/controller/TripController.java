package com.tripmakin.controller;

import com.tripmakin.exception.ResourceNotFoundException;
import com.tripmakin.model.Trip;
import com.tripmakin.model.TripParticipant;
import com.tripmakin.repository.TripParticipantRepository;
import com.tripmakin.repository.TripRepository;
import com.tripmakin.service.TripService;
import com.tripmakin.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

    @Operation(
        summary = "Get all trips",
        description = "Retrieve a paginated list of all trips",
        parameters = {
            @Parameter(name = "page", description = "Page number (zero-based)", example = "0"),
            @Parameter(name = "size", description = "Page size", example = "10"),
            @Parameter(name = "sort", description = "Sort format: [property,asc|desc]", example = "[\"tripId\",\"asc\"]"),
            @Parameter(name = "status", description = "Trip status filter", example = "ACTIVE")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of trips",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
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

    @Operation(
        summary = "Get a trip by ID",
        description = "Retrieve a specific trip by its ID",
        parameters = @Parameter(name = "id", description = "ID of the trip", required = true, example = "1")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the trip",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Trip.class))),
        @ApiResponse(responseCode = "404", description = "Trip not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Trip not found\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable Integer id) {
        return ResponseEntity.ok(tripService.getTripById(id));
    }

    @Operation(
        summary = "Create a new trip",
        description = "Add a new trip to the system"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Trip successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Trip.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Validation failed\"}")))
    })
    @PostMapping
    public ResponseEntity<Trip> createTrip(@Valid @RequestBody Trip newTrip) {
        return ResponseEntity.status(201).body(tripService.createTrip(newTrip));
    }

    @Operation(
        summary = "Update an existing trip",
        description = "Update the details of an existing trip",
        parameters = @Parameter(name = "id", description = "ID of the trip", required = true, example = "1")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Trip successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Trip.class))),
        @ApiResponse(responseCode = "404", description = "Trip not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Trip not found\"}"))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Validation failed\"}")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable Integer id, @Valid @RequestBody Trip updatedTrip) {
        tripService.getTripById(id);
        Trip trip = tripService.updateTrip(id, updatedTrip);
        return ResponseEntity.ok(trip);
    }

    @Operation(
        summary = "Delete a trip",
        description = "Remove a trip from the system",
        parameters = @Parameter(name = "id", description = "ID of the trip", required = true, example = "1")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Trip successfully deleted",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"message\": \"Trip deleted\"}"))),
        @ApiResponse(responseCode = "404", description = "Trip not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Trip not found\"}")))
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

    @Operation(
        summary = "Get trip participants",
        description = "Retrieve a list of participants for a specific trip",
        parameters = @Parameter(name = "id", description = "ID of the trip", required = true, example = "1")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved participants",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TripParticipant.class))),
        @ApiResponse(responseCode = "404", description = "Trip not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Trip not found\"}")))
    })
    @GetMapping("/{id}/participants")
    public List<TripParticipant> getTripParticipants(@PathVariable Integer id) {
        return tripParticipantRepository.findByTrip_TripId(id);
    }

    @Operation(
        summary = "Leave trip",
        description = "Remove a participant from a trip",
        parameters = {
            @Parameter(name = "tripId", description = "ID of the trip", required = true, example = "1"),
            @Parameter(name = "userId", description = "ID of the user", required = true, example = "2")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Left the trip",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"message\": \"Left the trip\"}"))),
        @ApiResponse(responseCode = "400", description = "Owner cannot leave the trip",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Owner cannot leave the trip\"}"))),
        @ApiResponse(responseCode = "404", description = "Participant not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Participant not found\"}")))
    })
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

    @Operation(
        summary = "Get all trips (admin)",
        description = "Retrieve a list of all trips (admin only)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all trips",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Trip.class)))
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    @Operation(
        summary = "Get my trips",
        description = "Retrieve a list of trips for the currently authenticated user"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user's trips",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Trip.class)))
    })
    @GetMapping("/my")
    public List<Trip> getMyTrips(Authentication authentication) {
        String email = authentication.getName();
        return tripService.getTripsForUser(email);
    }
}
