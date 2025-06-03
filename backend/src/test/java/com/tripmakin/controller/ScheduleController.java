package com.tripmakin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripmakin.model.Schedule;
import com.tripmakin.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScheduleController.class)
@Import(com.tripmakin.config.TestSecurityConfig.class)
class ScheduleControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ScheduleService scheduleService;

    private Schedule sample(Integer id) {
        Schedule s = new Schedule();
        s.setScheduleId(id);
        s.setDate(LocalDate.of(2025, 6, 15));
        s.setStartTime(LocalTime.of(10, 0));
        s.setEndTime(LocalTime.of(12, 0));
        s.setTitle("Zwiedzanie muzeum");
        s.setDescription("Opis punktu");
        s.setLocation("Paryż");
        s.setType("Kultura");
        return s;
    }

    @Test
    void shouldCreateSchedule() throws Exception {
        Schedule s = sample(1);
        Mockito.when(scheduleService.createSchedule(any(Schedule.class))).thenReturn(s);

        mockMvc.perform(post("/api/v1/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(s)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.scheduleId").value(1))
            .andExpect(jsonPath("$.title").value("Zwiedzanie muzeum"));
    }

    @Test
    void shouldReturnSchedulesForTrip() throws Exception {
        Schedule s1 = sample(1);
        Schedule s2 = sample(2);
        Mockito.when(scheduleService.getSchedulesForTrip(1)).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/v1/schedules/trip/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].scheduleId").value(1))
            .andExpect(jsonPath("$[1].scheduleId").value(2));
    }
}
