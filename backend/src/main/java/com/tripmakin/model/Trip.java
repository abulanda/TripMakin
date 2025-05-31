package com.tripmakin.model;

import jakarta.persistence.*;
import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(
    name = "trips",
    indexes = {
        @Index(name = "idx_trips_status", columnList = "status"),
        @Index(name = "idx_trips_created_by", columnList = "created_by")
    }
)
@Getter
@Setter
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Integer tripId;

    @NotBlank(message = "Destination is required")
    @Size(max = 255, message = "Destination must not exceed 255 characters")
    @Column(name = "destination", nullable = false, length = 255)
    private String destination;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @AssertTrue(message = "Start date must be before end date")
    public boolean isStartDateBeforeEndDate() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return startDate.isBefore(endDate);
    }

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_photo")
    private String coverPhoto;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @NotNull(message = "Created by is required")
    private User createdBy;

    @NotBlank(message = "Status is required")
    @Column(name = "status", nullable = false)
    private String status;

    public Trip() {}

}
