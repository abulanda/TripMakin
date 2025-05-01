package com.tripmakin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripmakin.model.Trip;
import com.tripmakin.model.User;
import com.tripmakin.repository.TripRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TripController.class)
class TripControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private TripRepository tripRepository;

    @Test
    void getTrips_ok() throws Exception {
        Trip t1 = sample(1, "Paryż", LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 20));
        Trip t2 = sample(2, "Londyn", LocalDate.of(2025, 7, 10), LocalDate.of(2025, 7, 15));
        Mockito.when(tripRepository.findAll()).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/api/trips"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].destination").value("Paryż"))
               .andExpect(jsonPath("$[1].destination").value("Londyn"));
    }

    @Test
    void getTripById_ok() throws Exception {
        Mockito.when(tripRepository.findById(1)).thenReturn(Optional.of(sample(1, "Paryż", LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 20))));

        mockMvc.perform(get("/api/trips/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.destination").value("Paryż"));
    }

    @Test
    void getTripById_notFound() throws Exception {
        Mockito.when(tripRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/trips/1"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Trip not found"))
               .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createTrip_created() throws Exception {
        Trip body = sample(null, "Paryż", LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 20));
        User createdBy = new User();
        createdBy.setUserId(1);
        body.setCreatedBy(createdBy);
    
        Mockito.when(tripRepository.save(any(Trip.class)))
               .thenAnswer(inv -> { Trip t = inv.getArgument(0); t.setTripId(3); return t; });
    
        mockMvc.perform(post("/api/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.destination").value("Paryż"));
    }

    @Test
    void createTrip_badRequest() throws Exception {
        mockMvc.perform(post("/api/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void createTrip_unprocessableEntity() throws Exception {
        Trip invalidTrip = sample(null, "Paryż", LocalDate.of(2025, 6, 20), LocalDate.of(2025, 6, 15));
    
        mockMvc.perform(post("/api/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTrip)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void updateTrip_badRequest() throws Exception {
        Mockito.when(tripRepository.findById(1)).thenReturn(Optional.of(sample(1, "Paryż", LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 20))));
    
        mockMvc.perform(put("/api/trips/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void updateTrip_unprocessableEntity() throws Exception {
        Mockito.when(tripRepository.findById(1)).thenReturn(Optional.of(sample(1, "Paryż", LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 20))));
        Trip invalidTrip = sample(1, "Paryż", LocalDate.of(2025, 6, 20), LocalDate.of(2025, 6, 15)); 
    
        mockMvc.perform(put("/api/trips/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTrip)))
               .andExpect(status().isBadRequest()) 
               .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void deleteTrip_ok() throws Exception {
        Mockito.when(tripRepository.findById(1)).thenReturn(Optional.of(sample(1, "Paryż", LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 20))));

        mockMvc.perform(delete("/api/trips/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message").value("Trip deleted"));
    }

    @Test
    void deleteTrip_notFound() throws Exception {
        Mockito.when(tripRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/trips/1"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Trip not found"))
               .andExpect(jsonPath("$.status").value(404));
    }

    private Trip sample(Integer id, String destination, LocalDate startDate, LocalDate endDate) {
        Trip t = new Trip();
        t.setTripId(id);
        t.setDestination(destination);
        t.setStartDate(startDate);
        t.setEndDate(endDate);
        t.setDescription("Opis wycieczki");
        t.setCoverPhoto("photo.jpg");
        t.setStatus("Planned");
        return t;
    }
}