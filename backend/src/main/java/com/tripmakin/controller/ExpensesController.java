package com.tripmakin.controller;

import com.tripmakin.model.Expense;
import com.tripmakin.repository.ExpenseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.validation.Valid;

@Tag(name = "Expenses", description = "Endpoints for managing expenses")
@RestController
@RequestMapping("/api/expenses")
public class ExpensesController {

    private final ExpenseRepository expenseRepository;

    public ExpensesController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Operation(summary = "Get all expenses", description = "Retrieve a list of all expenses")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of expenses")
    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses() {
        return ResponseEntity.ok(expenseRepository.findAll());
    }

    @Operation(summary = "Get an expense by ID", description = "Retrieve a specific expense by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the expense", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Expense.class))),
        @ApiResponse(responseCode = "404", description = "Expense not found", 
                     content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> getExpenseById(@PathVariable Integer id) {
        Optional<Expense> expense = expenseRepository.findById(id);
        if (expense.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Expense not found"));
        }
        return ResponseEntity.ok(expense.get());
    }

    @Operation(summary = "Create a new expense", description = "Add a new expense to the system")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Expense successfully created", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Expense.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed", 
                     content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense newExpense) {
        Expense savedExpense = expenseRepository.save(newExpense);
        return ResponseEntity.status(201).body(savedExpense);
    }

    @Operation(summary = "Update an existing expense", description = "Update the details of an existing expense")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Expense successfully updated", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Expense.class))),
        @ApiResponse(responseCode = "404", description = "Expense not found", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Validation failed", 
                     content = @Content(mediaType = "application/json"))
    })
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

    @Operation(summary = "Delete an expense", description = "Remove an expense from the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Expense successfully deleted", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Expense not found", 
                     content = @Content(mediaType = "application/json"))
    })
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
