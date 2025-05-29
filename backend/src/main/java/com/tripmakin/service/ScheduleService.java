package com.tripmakin.service;

import com.tripmakin.exception.ResourceNotFoundException;
import com.tripmakin.model.Schedule;
import com.tripmakin.model.Trip;
import com.tripmakin.model.User;
import com.tripmakin.repository.ScheduleRepository;
import com.tripmakin.repository.TripRepository;
import com.tripmakin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    public List<Schedule> getSchedulesForTrip(Integer tripId) {
        return scheduleRepository.findByTrip_TripIdOrderByDateAscStartTimeAsc(tripId);
    }

    public Schedule createSchedule(Schedule schedule) {
        Integer userId = schedule.getCreatedBy().getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        schedule.setCreatedBy(user);

        Integer tripId = schedule.getTrip().getTripId();
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
        schedule.setTrip(trip);

        return scheduleRepository.save(schedule);
    }
}
