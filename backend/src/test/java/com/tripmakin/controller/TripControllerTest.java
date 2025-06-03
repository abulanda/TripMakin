package com.tripmakin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripmakin.model.Trip;
import com.tripmakin.model.TripParticipant;
import com.tripmakin.model.User;
import com.tripmakin.service.TripService;
import com.tripmakin.config.TestSecurityConfig;
import com.tripmakin.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TripController.class)
@Import(TestSecurityConfig.class)
class TripControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private TripService tripService;

    @MockBean
    private com.tripmakin.repository.TripParticipantRepository tripParticipantRepository;

    @MockBean
    private com.tripmakin.repository.TripRepository tripRepository;

    @Test
    void shouldReturnPagedTrips() throws Exception {
        Trip t1 = sample(1, "Paryż", LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 20));
        Trip t2 = sample(2, "Londyn", LocalDate.of(2025, 7, 10), LocalDate.of(2025, 7, 15));
        Mockito.when(tripService.getTrips(any(), any())).thenReturn(new PageImpl<>(List.of(t1, t2), PageRequest.of(0, 10), 2));

        mockMvc.perform(get("/api/v1/trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].destination").value("Paryż"))
                .andExpect(jsonPath("$.content[1].destination").value("Londyn"));
    }

    @Test
    void shouldReturnTripById() throws Exception {
        Mockito.when(tripService.getTripById(1)).thenReturn(sample(1, "Paryż", LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 20)));

        mockMvc.perform(get("/api/v1/trips/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destination").value("Paryż"));
    }

    @Test
    void shouldReturn404WhenTripNotFound() throws Exception {
        Mockito.when(tripService.getTripById(1)).thenThrow(new ResourceNotFoundException("Trip not found"));

        mockMvc.perform(get("/api/v1/trips/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Trip not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldCreateTrip() throws Exception {
        Trip body = sample(null, "Paryż", LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 20));
        User createdBy = new User();
        createdBy.setUserId(1);
        body.setCreatedBy(createdBy);

        Mockito.when(tripService.createTrip(any(Trip.class))).thenReturn(body);

        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destination").value("Paryż"));
    }

    @Test
    void shouldReturnBadRequestWhenCreateTripWithInvalidData() throws Exception {
        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void shouldUpdateTrip() throws Exception {
        Trip updated = sample(1, "Londyn", LocalDate.of(2025, 7, 10), LocalDate.of(2025, 7, 15));
        User createdBy = new User();
        createdBy.setUserId(1);
        updated.setCreatedBy(createdBy);

        Mockito.when(tripService.getTripById(1)).thenReturn(updated);
        Mockito.when(tripService.updateTrip(Mockito.eq(1), any(Trip.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/trips/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destination").value("Londyn"));
    }

    @Test
    void shouldReturn404WhenUpdateTripNotFound() throws Exception {
        Trip trip = sample(1, "Paryż", LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 20));
        User createdBy = new User();
        createdBy.setUserId(1);
        trip.setCreatedBy(createdBy);

        Mockito.when(tripService.getTripById(1)).thenThrow(new ResourceNotFoundException("Trip not found"));

        mockMvc.perform(put("/api/v1/trips/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Trip not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldDeleteTrip() throws Exception {
        Mockito.doNothing().when(tripService).deleteTrip(1);

        mockMvc.perform(delete("/api/v1/trips/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Trip deleted"));
    }

    @Test
    void shouldReturn404WhenDeleteTripNotFound() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Trip not found")).when(tripService).deleteTrip(1);

        mockMvc.perform(delete("/api/v1/trips/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Trip not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturnTripParticipants() throws Exception {
        TripParticipant participant = new TripParticipant();
        participant.setTrip(new Trip());
        participant.setUser(new User());
        Mockito.when(tripParticipantRepository.findByTrip_TripId(1)).thenReturn(List.of(participant));

        mockMvc.perform(get("/api/v1/trips/1/participants"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldLeaveTrip() throws Exception {
        TripParticipant participant = new TripParticipant();
        participant.setRole("MEMBER");
        Mockito.when(tripParticipantRepository.findByTrip_TripIdAndUser_UserId(1, 2))
                .thenReturn(java.util.Optional.of(participant));

        mockMvc.perform(delete("/api/v1/trips/1/participants/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Left the trip"));
    }

    @Test
    void shouldReturnBadRequestWhenOwnerTriesToLeaveTrip() throws Exception {
        TripParticipant participant = new TripParticipant();
        participant.setRole("OWNER");
        Mockito.when(tripParticipantRepository.findByTrip_TripIdAndUser_UserId(1, 2))
                .thenReturn(java.util.Optional.of(participant));

        mockMvc.perform(delete("/api/v1/trips/1/participants/2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Owner cannot leave the trip"));
    }

    @Test
    void shouldReturn404WhenParticipantNotFound() throws Exception {
        Mockito.when(tripParticipantRepository.findByTrip_TripIdAndUser_UserId(1, 2))
                .thenReturn(java.util.Optional.empty());

        mockMvc.perform(delete("/api/v1/trips/1/participants/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Participant not found"));
    }

    @Test
    void shouldReturnAllTripsForAdmin() throws Exception {
        Trip t1 = sample(1, "Paryż", LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 20));
        Mockito.when(tripRepository.findAll()).thenReturn(List.of(t1));

        mockMvc.perform(get("/api/v1/trips/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].destination").value("Paryż"));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void shouldReturnMyTrips() throws Exception {
        Trip t1 = sample(1, "Paryż", LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 20));
        Mockito.when(tripService.getTripsForUser("user@example.com")).thenReturn(List.of(t1));

        mockMvc.perform(get("/api/v1/trips/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].destination").value("Paryż"));
    }

    private Trip sample(Integer id, String destination, LocalDate startDate, LocalDate endDate) {
        Trip t = new Trip();
        t.setTripId(id);
        t.setDestination(destination);
        t.setStartDate(startDate);
        t.setEndDate(endDate);
        t.setDescription("Opis wycieczki");
        t.setStatus("PLANNED");
        return t;
    }
}