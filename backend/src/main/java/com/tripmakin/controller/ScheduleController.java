package com.tripmakin.controller;

import com.tripmakin.model.Schedule;
import com.tripmakin.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Schedules", description = "Endpoints for managing schedules")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Operation(
        summary = "Create a new schedule",
        description = "Add a new schedule to the system"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Schedule successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Schedule.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Validation failed\"}")))
    })
    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@RequestBody Schedule schedule) {
        Schedule saved = scheduleService.createSchedule(schedule);
        return ResponseEntity.status(201).body(saved);
    }

    @Operation(
        summary = "Get schedules for a trip",
        description = "Retrieve a list of schedules for a specific trip",
        parameters = @Parameter(name = "tripId", description = "ID of the trip", required = true, example = "1")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of schedules for the trip",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Schedule.class))),
        @ApiResponse(responseCode = "404", description = "Trip not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Trip not found\"}"))),
        @ApiResponse(responseCode = "400", description = "Invalid trip ID",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Invalid trip ID\"}")))
    })
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<Schedule>> getSchedulesForTrip(
        @Parameter(description = "ID of the trip", required = true, example = "1")
        @PathVariable Integer tripId) {
        return ResponseEntity.ok(scheduleService.getSchedulesForTrip(tripId));
    }
}
