package com.tripmakin.service;

import com.tripmakin.model.Invitation;
import com.tripmakin.model.Trip;
import com.tripmakin.model.TripParticipant;
import com.tripmakin.model.User;
import com.tripmakin.repository.InvitationRepository;
import com.tripmakin.repository.TripParticipantRepository;
import com.tripmakin.repository.TripRepository;
import com.tripmakin.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripParticipantRepository tripParticipantRepository;

    public InvitationService(
        InvitationRepository invitationRepository,
        TripRepository tripRepository,
        UserRepository userRepository,
        TripParticipantRepository tripParticipantRepository
    ) {
        this.invitationRepository = invitationRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.tripParticipantRepository = tripParticipantRepository;
    }

    public Invitation sendInvitation(Integer tripId, Integer inviterId, Integer invitedUserId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow();
        User inviter = userRepository.findById(inviterId).orElseThrow();
        User invitedUser = userRepository.findById(invitedUserId).orElseThrow();

        Invitation invitation = new Invitation();
        invitation.setTrip(trip);
        invitation.setInviter(inviter);
        invitation.setInvitedUser(invitedUser);
        invitation.setStatus("PENDING");
        return invitationRepository.save(invitation);
    }

    public List<Invitation> getInvitationsForUser(Integer userId) {
        return invitationRepository.findByInvitedUser_UserId(userId);
    }

    public Invitation respondToInvitation(Integer invitationId, String status) {
        Invitation invitation = invitationRepository.findById(invitationId).orElseThrow();
        invitation.setStatus(status);

        if ("ACCEPTED".equalsIgnoreCase(status)) {
            TripParticipant participant = new TripParticipant();
            participant.setTrip(invitation.getTrip());
            participant.setUser(invitation.getInvitedUser());
            participant.setRole("USER");
            participant.setStatus("ACTIVE");
            tripParticipantRepository.save(participant);
        }

        return invitationRepository.save(invitation);
    }
}
