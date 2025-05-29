package com.tripmakin.repository;

import com.tripmakin.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByTrip_TripIdOrderByDateAscStartTimeAsc(Integer tripId);
}
