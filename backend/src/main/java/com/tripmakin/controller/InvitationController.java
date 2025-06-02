package com.tripmakin.controller;

import com.tripmakin.model.Invitation;
import com.tripmakin.service.InvitationService;
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

@Tag(name = "Invitations", description = "Endpoints for managing invitations")
@RestController
@RequestMapping("/api/v1/invitations")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class InvitationController {
    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @Operation(
        summary = "Send an invitation",
        description = "Send an invitation to a user to join a trip"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invitation sent",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Invitation.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Invalid request\"}")))
    })
    @PostMapping("/send")
    public Invitation sendInvitation(@RequestBody SendInvitationRequest req) {
        return invitationService.sendInvitation(req.tripId(), req.inviterId(), req.invitedUserId());
    }

    @Operation(
        summary = "Get invitations for user",
        description = "Retrieve all invitations for a specific user",
        parameters = @Parameter(name = "userId", description = "ID of the user", required = true, example = "1")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved invitations",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Invitation.class)))
    })
    @GetMapping("/user/{userId}")
    public List<Invitation> getInvitationsForUser(@PathVariable Integer userId) {
        return invitationService.getInvitationsForUser(userId);
    }

    @Operation(
        summary = "Respond to invitation",
        description = "Respond to an invitation (accept or decline)",
        parameters = @Parameter(name = "invitationId", description = "ID of the invitation", required = true, example = "1")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invitation response saved",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Invitation.class))),
        @ApiResponse(responseCode = "404", description = "Invitation not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Invitation not found\"}")))
    })
    @PostMapping("/{invitationId}/respond")
    public Invitation respondToInvitation(@PathVariable Integer invitationId, @RequestBody RespondInvitationRequest req) {
        return invitationService.respondToInvitation(invitationId, req.status());
    }
}

record SendInvitationRequest(Integer tripId, Integer inviterId, Integer invitedUserId) {}
record RespondInvitationRequest(String status) {}
