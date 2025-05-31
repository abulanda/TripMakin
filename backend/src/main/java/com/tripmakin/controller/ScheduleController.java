package com.tripmakin.controller;

import com.tripmakin.model.Schedule;
import com.tripmakin.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@RequestBody Schedule schedule) {
        Schedule saved = scheduleService.createSchedule(schedule);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<Schedule>> getSchedulesForTrip(@PathVariable Integer tripId) {
        return ResponseEntity.ok(scheduleService.getSchedulesForTrip(tripId));
    }
}
