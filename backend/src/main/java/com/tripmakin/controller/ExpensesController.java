package com.tripmakin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/expenses")
public class ExpensesController {
            
    private static final Map<Integer, Map<String, String>> expenses = new HashMap<>();

    static {
        expenses.put(1, Map.of("id", "1", "name", "Bilet lotniczy", "amount", "1500"));
        expenses.put(2, Map.of("id", "2", "name", "Hotel", "amount", "2000"));
    }

    @GetMapping
    public ResponseEntity<Object> getExpenses() {
        return ResponseEntity.ok(expenses.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getExpenseById(@PathVariable int id) {
        if (!expenses.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Expense not found"));
        }
        return ResponseEntity.ok(expenses.get(id));
    }

    @PostMapping
    public ResponseEntity<Object> createExpense(@RequestBody Map<String, String> newExpense) {
        int newId = expenses.size() + 1;
        expenses.put(newId, Map.of(
            "id", String.valueOf(newId),
            "name", newExpense.get("name"),
            "amount", newExpense.get("amount")
        ));
        return ResponseEntity.status(201).body(expenses.get(newId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateExpense(@PathVariable int id, @RequestBody Map<String, String> updatedExpense) {
        if (!expenses.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Expense not found"));
        }
        expenses.put(id, Map.of(
            "id", String.valueOf(id),
            "name", updatedExpense.get("name"),
            "amount", updatedExpense.get("amount")
        ));
        return ResponseEntity.ok(expenses.get(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteExpense(@PathVariable int id) {
        if (!expenses.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Expense not found"));
        }
        expenses.remove(id);
        return ResponseEntity.ok(Map.of("message", "Expense deleted"));
    }
}
