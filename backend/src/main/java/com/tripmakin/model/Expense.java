package com.tripmakin.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "expenses",
    indexes = {
        @Index(name = "idx_expenses_trip_id", columnList = "trip_id"),
        @Index(name = "idx_expenses_user_id", columnList = "user_id")
    }
)
@Getter
@Setter
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Integer expenseId;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @NotNull(message = "Date is required")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull(message = "Amount is required")
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @NotNull(message = "Description is required")
    @Size(min = 1, max = 500, message = "Description must be between 1 and 500 characters")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Category is required")
    @Column(name = "category", length = 100, nullable = false)
    private String category;

    @NotNull(message = "Currency is required")
    @Column(name = "currency", length = 10, nullable = false)
    private String currency;

    @Column(name = "is_settled")
    private Boolean isSettled;

    public Expense() {
        this.isSettled = false;
    }

}
