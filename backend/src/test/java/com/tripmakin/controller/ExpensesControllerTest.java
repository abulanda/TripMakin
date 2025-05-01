package com.tripmakin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripmakin.model.Expense;
import com.tripmakin.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpensesController.class)
class ExpensesControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ExpenseRepository expenseRepository;

    @Test
    void getExpenses_ok() throws Exception {
        Expense e1 = sample(1, "Bilet lotniczy", BigDecimal.valueOf(1500), "Transport");
        Expense e2 = sample(2, "Hotel", BigDecimal.valueOf(2000), "Accommodation");
        Mockito.when(expenseRepository.findAll()).thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/api/expenses"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].description").value("Bilet lotniczy"))
               .andExpect(jsonPath("$[1].description").value("Hotel"));
    }

    @Test
    void getExpenseById_ok() throws Exception {
        Mockito.when(expenseRepository.findById(1)).thenReturn(Optional.of(sample(1, "Bilet lotniczy", BigDecimal.valueOf(1500), "Transport")));

        mockMvc.perform(get("/api/expenses/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.description").value("Bilet lotniczy"));
    }

    @Test
    void getExpenseById_notFound() throws Exception {
        Mockito.when(expenseRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/expenses/1"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Expense not found"));
    }

    @Test
    void createExpense_created() throws Exception {
        Expense body = sample(null, "Bilet lotniczy", BigDecimal.valueOf(1500), "Transport");
        Mockito.when(expenseRepository.save(any(Expense.class)))
               .thenAnswer(inv -> { Expense e = inv.getArgument(0); e.setExpenseId(3); return e; });

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.description").value("Bilet lotniczy"));
    }

    @Test
    void createExpense_badRequest() throws Exception {
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
               .andExpect(status().isBadRequest());
    }

    @Test
    void updateExpense_ok() throws Exception {
        Mockito.when(expenseRepository.findById(1)).thenReturn(Optional.of(sample(1, "Bilet lotniczy", BigDecimal.valueOf(1500), "Transport")));
        Expense updatedExpense = sample(1, "Hotel", BigDecimal.valueOf(2000), "Accommodation");
        Mockito.when(expenseRepository.save(any(Expense.class))).thenReturn(updatedExpense);

        mockMvc.perform(put("/api/expenses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedExpense)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.description").value("Hotel"));
    }

    @Test
    void updateExpense_notFound() throws Exception {
        Mockito.when(expenseRepository.findById(1)).thenReturn(Optional.empty());
        Expense updatedExpense = sample(1, "Hotel", BigDecimal.valueOf(2000), "Accommodation");

        mockMvc.perform(put("/api/expenses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedExpense)))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Expense not found"));
    }

    @Test
    void deleteExpense_ok() throws Exception {
        Mockito.when(expenseRepository.findById(1)).thenReturn(Optional.of(sample(1, "Bilet lotniczy", BigDecimal.valueOf(1500), "Transport")));

        mockMvc.perform(delete("/api/expenses/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message").value("Expense deleted"));
    }

    @Test
    void deleteExpense_notFound() throws Exception {
        Mockito.when(expenseRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/expenses/1"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Expense not found"));
    }

    private Expense sample(Integer id, String description, BigDecimal amount, String category) {
        Expense e = new Expense();
        e.setExpenseId(id);
        e.setDescription(description);
        e.setAmount(amount);
        e.setCategory(category);
        e.setCurrency("PLN");
        e.setDate(LocalDate.now());
        e.setIsSettled(false);
        return e;
    }
}