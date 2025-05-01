package com.tripmakin.controller;

import com.tripmakin.exception.ResourceNotFoundException;
import com.tripmakin.model.Expense;
import com.tripmakin.service.ExpenseService;
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

import jakarta.validation.Valid;

@Tag(name = "Expenses", description = "Endpoints for managing expenses")
@RestController
@RequestMapping("/api/expenses")
public class ExpensesController {

    private final ExpenseService expenseService;

    public ExpensesController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Operation(summary = "Get all expenses", description = "Retrieve a list of all expenses")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of expenses")
    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    @Operation(summary = "Get an expense by ID", description = "Retrieve a specific expense by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the expense", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = Expense.class))),
        @ApiResponse(responseCode = "404", description = "Expense not found", 
                     content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Integer id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
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
        return ResponseEntity.status(201).body(expenseService.createExpense(newExpense));
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
    public ResponseEntity<Expense> updateExpense(@PathVariable Integer id, @RequestBody Expense expense) {
        Expense updatedExpense = expenseService.updateExpense(id, expense);
        if (updatedExpense == null) {
            throw new ResourceNotFoundException("Expense not found");
        }
        return ResponseEntity.ok(updatedExpense);
    }

    @Operation(summary = "Delete an expense", description = "Remove an expense from the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Expense successfully deleted", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Expense not found", 
                     content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteExpense(@PathVariable Integer id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(Map.of("message", "Expense deleted"));
    }
}
