import React, { useEffect, useState } from "react";

const InvitationNotifications = ({ onInvitationResponded }) => {
  const [invitations, setInvitations] = useState([]);
  const userId = localStorage.getItem("userId");
  const token = localStorage.getItem("jwtToken");

  useEffect(() => {
    if (!userId || !token) return;
    fetch(`/api/invitations/user/${userId}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.ok ? res.json() : [])
      .then(setInvitations);
  }, [userId, token]);

  const respond = (invitationId, status) => {
    fetch(`/api/invitations/${invitationId}/respond`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ status }),
    })
      .then((res) => res.ok ? res.json() : null)
      .then(() => {
        setInvitations((prev) => prev.filter(i => i.invitationId !== invitationId));
        if (onInvitationResponded) onInvitationResponded();
      });
  };

  if (!userId) return null;

  return (
    <div>
      <h3>Zaproszenia</h3>
      {invitations.filter(i => i.status === "PENDING").length === 0 ? (
        <p>Brak nowych zaproszeń</p>
      ) : (
        <ul>
          {invitations
            .filter(i => i.status === "PENDING")
            .map((i) => (
              <li key={i.invitationId}>
                Zaproszenie do wycieczki: <b>{i.trip?.destination}</b> od <b>{i.inviter?.firstName} {i.inviter?.lastName}</b>
                <button onClick={() => respond(i.invitationId, "ACCEPTED")}>Akceptuj</button>
                <button onClick={() => respond(i.invitationId, "REJECTED")}>Odrzuć</button>
              </li>
            ))}
        </ul>
      )}
    </div>
  );
};

export default InvitationNotifications;