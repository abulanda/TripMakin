package com.tripmakin.controller;

import com.tripmakin.exception.ResourceNotFoundException;
import com.tripmakin.model.Expense;
import com.tripmakin.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

@Tag(name = "Expenses", description = "Endpoints for managing expenses")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/expenses")
public class ExpensesController {

    private final ExpenseService expenseService;

    public ExpensesController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Operation(
        summary = "Get all expenses",
        description = "Retrieve a list of all expenses"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of expenses",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Expense.class)))
    })
    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    @Operation(
        summary = "Get an expense by ID",
        description = "Retrieve a specific expense by its ID",
        parameters = @Parameter(name = "id", description = "ID of the expense", required = true, example = "1")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the expense",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Expense.class))),
        @ApiResponse(responseCode = "404", description = "Expense not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Expense not found\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Integer id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    @Operation(
        summary = "Create a new expense",
        description = "Add a new expense to the system"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Expense successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Expense.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Validation failed\"}")))
    })
    @PostMapping
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense newExpense) {
        return ResponseEntity.status(201).body(expenseService.createExpense(newExpense));
    }

    @Operation(
        summary = "Update an existing expense",
        description = "Update the details of an existing expense",
        parameters = @Parameter(name = "id", description = "ID of the expense", required = true, example = "1")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Expense successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Expense.class))),
        @ApiResponse(responseCode = "404", description = "Expense not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Expense not found\"}"))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Validation failed\"}")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(
            @PathVariable Integer id,
            @Valid @RequestBody Expense expense) {
        Expense updatedExpense = expenseService.updateExpense(id, expense);
        if (updatedExpense == null) {
            throw new ResourceNotFoundException("Expense not found");
        }
        return ResponseEntity.ok(updatedExpense);
    }

    @Operation(
        summary = "Delete an expense",
        description = "Remove an expense from the system",
        parameters = @Parameter(name = "id", description = "ID of the expense", required = true, example = "1")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Expense successfully deleted",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"message\": \"Expense deleted\"}"))),
        @ApiResponse(responseCode = "404", description = "Expense not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Expense not found\"}")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteExpense(@PathVariable Integer id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(Map.of("message", "Expense deleted"));
    }

    @Operation(
        summary = "Get expenses for a trip",
        description = "Retrieve a list of expenses for a specific trip",
        parameters = @Parameter(name = "tripId", description = "ID of the trip", required = true, example = "1")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of expenses for the trip",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Expense.class))),
        @ApiResponse(responseCode = "404", description = "Trip not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Trip not found\"}"))),
        @ApiResponse(responseCode = "400", description = "Invalid trip ID",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Invalid trip ID\"}")))
    })
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<Expense>> getExpensesForTrip(
        @Parameter(description = "ID of the trip", required = true, example = "1")
        @PathVariable Integer tripId) {
        return ResponseEntity.ok(expenseService.getExpensesForTrip(tripId));
    }
}
