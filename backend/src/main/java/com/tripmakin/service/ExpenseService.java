package com.tripmakin.service;

import com.tripmakin.exception.ResourceNotFoundException;
import com.tripmakin.model.Expense;
import com.tripmakin.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense getExpenseById(Integer id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
    }

    public Expense createExpense(Expense newExpense) {
        return expenseRepository.save(newExpense);
    }

    public Expense updateExpense(Integer id, Expense expense) {
        return expenseRepository.findById(id)
                .map(existingExpense -> {
                    existingExpense.setDescription(expense.getDescription());
                    existingExpense.setAmount(expense.getAmount());
                    existingExpense.setCategory(expense.getCategory());
                    existingExpense.setCurrency(expense.getCurrency());
                    existingExpense.setDate(expense.getDate());
                    existingExpense.setIsSettled(expense.getIsSettled());
                    return expenseRepository.save(existingExpense);
                }).orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
    }

    public void deleteExpense(Integer id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        expenseRepository.delete(expense);
    }
}