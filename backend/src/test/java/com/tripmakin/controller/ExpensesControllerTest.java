package com.tripmakin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripmakin.model.Expense;
import com.tripmakin.model.Trip;
import com.tripmakin.model.User;
import com.tripmakin.service.ExpenseService;
import com.tripmakin.config.TestSecurityConfig;
import com.tripmakin.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpensesController.class)
@Import(TestSecurityConfig.class)
class ExpensesControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ExpenseService expenseService;

    @Test
    void getExpenses_ok() throws Exception {
        Expense e1 = sample(1, "Bilet lotniczy", BigDecimal.valueOf(1500), "Transport");
        Expense e2 = sample(2, "Hotel", BigDecimal.valueOf(2000), "Accommodation");
        Mockito.when(expenseService.getAllExpenses()).thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/api/expenses"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].description").value("Bilet lotniczy"))
               .andExpect(jsonPath("$[1].description").value("Hotel"));
    }

    @Test
    void getExpenseById_ok() throws Exception {
        Mockito.when(expenseService.getExpenseById(1)).thenReturn(sample(1, "Bilet lotniczy", BigDecimal.valueOf(1500), "Transport"));

        mockMvc.perform(get("/api/expenses/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.description").value("Bilet lotniczy"));
    }

    @Test
    void getExpenseById_notFound() throws Exception {
        Mockito.when(expenseService.getExpenseById(1)).thenThrow(new ResourceNotFoundException("Expense not found"));

        mockMvc.perform(get("/api/expenses/1"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Expense not found"));
    }

    @Test
    void createExpense_created() throws Exception {
        Expense body = sample(null, "Bilet lotniczy", BigDecimal.valueOf(1500), "Transport");
        Mockito.when(expenseService.createExpense(any(Expense.class))).thenReturn(body);

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
        Expense updatedExpense = sample(1, "Hotel", BigDecimal.valueOf(2000), "Accommodation");
        Mockito.when(expenseService.updateExpense(Mockito.eq(1), Mockito.any(Expense.class)))
           .thenReturn(updatedExpense);
           
        mockMvc.perform(put("/api/expenses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedExpense)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.description").value("Hotel"))
               .andExpect(jsonPath("$.amount").value(2000))
               .andExpect(jsonPath("$.category").value("Accommodation"));
    }

    @Test
    void updateExpense_notFound() throws Exception {
        Expense updatedExpense = sample(1, "Hotel", BigDecimal.valueOf(2000), "Accommodation");
        Mockito.when(expenseService.updateExpense(Mockito.eq(1), Mockito.any(Expense.class)))
               .thenThrow(new ResourceNotFoundException("Expense not found"));

        mockMvc.perform(put("/api/expenses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedExpense)))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Expense not found"))
               .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteExpense_ok() throws Exception {
        Mockito.doNothing().when(expenseService).deleteExpense(1);

        mockMvc.perform(delete("/api/expenses/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message").value("Expense deleted"));
    }

    @Test
    void deleteExpense_notFound() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Expense not found")).when(expenseService).deleteExpense(1);

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

        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setPassword("secret123");
        e.setUser(user);

        Trip trip = new Trip();
        trip.setDestination("Warszawa");
        trip.setStartDate(LocalDate.now());
        trip.setEndDate(LocalDate.now().plusDays(2));
        trip.setStatus("PLANNED");
        trip.setCreatedBy(user);
        e.setTrip(trip);
        
        return e;
    }

}