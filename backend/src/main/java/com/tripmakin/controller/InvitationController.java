package com.tripmakin.controller;

import com.tripmakin.model.Invitation;
import com.tripmakin.service.InvitationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@CrossOrigin(origins = "http://localhost:5173")
public class InvitationController {
    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping("/send")
    public Invitation sendInvitation(@RequestBody SendInvitationRequest req) {
        return invitationService.sendInvitation(req.tripId(), req.inviterId(), req.invitedUserId());
    }

    @GetMapping("/user/{userId}")
    public List<Invitation> getInvitationsForUser(@PathVariable Integer userId) {
        return invitationService.getInvitationsForUser(userId);
    }

    @PostMapping("/{invitationId}/respond")
    public Invitation respondToInvitation(@PathVariable Integer invitationId, @RequestBody RespondInvitationRequest req) {
        return invitationService.respondToInvitation(invitationId, req.status());
    }
}

record SendInvitationRequest(Integer tripId, Integer inviterId, Integer invitedUserId) {}
record RespondInvitationRequest(String status) {}
