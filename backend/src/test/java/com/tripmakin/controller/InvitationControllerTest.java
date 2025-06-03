package com.tripmakin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripmakin.config.TestSecurityConfig;
import com.tripmakin.model.Invitation;
import com.tripmakin.service.InvitationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvitationController.class)
@Import(TestSecurityConfig.class)
@WithMockUser 
class InvitationControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private InvitationService invitationService;

    private Invitation sample(Integer id) {
        Invitation inv = new Invitation();
        inv.setInvitationId(id);
        inv.setStatus("PENDING");
        inv.setSentAt(LocalDateTime.now());
        return inv;
    }

    @Test
    void shouldSendInvitation() throws Exception {
        Invitation inv = sample(1);
        Mockito.when(invitationService.sendInvitation(anyInt(), anyInt(), anyInt())).thenReturn(inv);

        mockMvc.perform(post("/api/v1/invitations/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"tripId":1,"inviterId":2,"invitedUserId":3}
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.invitationId").value(1))
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldGetInvitationsForUser() throws Exception {
        Invitation inv1 = sample(1);
        Invitation inv2 = sample(2);
        Mockito.when(invitationService.getInvitationsForUser(3)).thenReturn(List.of(inv1, inv2));

        mockMvc.perform(get("/api/v1/invitations/user/3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].invitationId").value(1))
            .andExpect(jsonPath("$[1].invitationId").value(2));
    }

    @Test
    void shouldRespondToInvitation() throws Exception {
        Invitation inv = sample(1);
        inv.setStatus("ACCEPTED");
        Mockito.when(invitationService.respondToInvitation(eq(1), eq("ACCEPTED"))).thenReturn(inv);

        mockMvc.perform(post("/api/v1/invitations/1/respond")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"status":"ACCEPTED"}
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.invitationId").value(1))
            .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }
}
