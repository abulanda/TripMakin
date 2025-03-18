package com.tripmakin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private static final Map<Integer, Map<String, String>> trips = new HashMap<>();

    static {
        trips.put(1, Map.of("id", "1", "destination", "Paryż", "date", "2025-06-15"));
        trips.put(2, Map.of("id", "2", "destination", "Nowy Jork", "date", "2025-07-20"));
    }

    @GetMapping
    public ResponseEntity<Object> getTrips() {
        return ResponseEntity.ok(trips.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTripById(@PathVariable int id) {
        if (!trips.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Trip not found"));
        }
        return ResponseEntity.ok(trips.get(id));
    }

    @PostMapping
    public ResponseEntity<Object> createTrip(@RequestBody Map<String, String> newTrip) {
        int newId = trips.size() + 1;
        trips.put(newId, Map.of(
            "id", String.valueOf(newId),
            "destination", newTrip.get("destination"),
            "date", newTrip.get("date")
        ));
        return ResponseEntity.status(201).body(trips.get(newId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateTrip(@PathVariable int id, @RequestBody Map<String, String> updatedTrip) {
        if (!trips.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Trip not found"));
        }
        trips.put(id, Map.of(
            "id", String.valueOf(id),
            "destination", updatedTrip.get("destination"),
            "date", updatedTrip.get("date")
        ));
        return ResponseEntity.ok(trips.get(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTrip(@PathVariable int id) {
        if (!trips.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Trip not found"));
        }
        trips.remove(id);
        return ResponseEntity.ok(Map.of("message", "Trip deleted"));
    }
}
