package com.tripmakin.controller;

import com.tripmakin.model.Expense;
import com.tripmakin.repository.ExpenseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expenses")
public class ExpensesController {

    private final ExpenseRepository expenseRepository;

    public ExpensesController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses() {
        return ResponseEntity.ok(expenseRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getExpenseById(@PathVariable Integer id) {
        Optional<Expense> expense = expenseRepository.findById(id);
        if (expense.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Expense not found"));
        }
        return ResponseEntity.ok(expense.get());
    }

    @PostMapping
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense newExpense) {
        Expense savedExpense = expenseRepository.save(newExpense);
        return ResponseEntity.status(201).body(savedExpense);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateExpense(@PathVariable Integer id, @RequestBody Expense updatedExpense) {
        Optional<Expense> existingExpense = expenseRepository.findById(id);
        if (existingExpense.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Expense not found"));
        }
        updatedExpense.setExpenseId(id);
        Expense savedExpense = expenseRepository.save(updatedExpense);
        return ResponseEntity.ok(savedExpense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteExpense(@PathVariable Integer id) {
        Optional<Expense> expense = expenseRepository.findById(id);
        if (expense.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Expense not found"));
        }
        expenseRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Expense deleted"));
    }
}
