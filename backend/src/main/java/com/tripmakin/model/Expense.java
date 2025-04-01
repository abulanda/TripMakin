package com.tripmakin.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
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


    @Column(name = "date")
    private LocalDate date;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "is_settled")
    private Boolean isSettled;

    public Expense() {
        this.isSettled = false;
    }

    public Integer getExpenseId() {
        return expenseId;
    }
    public void setExpenseId(Integer expenseId) {
        this.expenseId = expenseId;
    }

    public Trip getTrip() {
        return trip;
    }
    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getIsSettled() {
        return isSettled;
    }
    public void setIsSettled(Boolean isSettled) {
        this.isSettled = isSettled;
    }

}
