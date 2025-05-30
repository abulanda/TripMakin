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
    <div className="invitation-panel">
      <h3>Zaproszenia</h3>
      {invitations.filter(i => i.status === "PENDING").length === 0 ? (
        <p>Brak nowych zaproszeń</p>
      ) : (
        <div className="invitation-list">
          {invitations
            .filter(i => i.status === "PENDING")
            .map((i, idx, arr) => (
              <React.Fragment key={i.invitationId}>
                <div className="invitation-item">
                  <div className="invitation-text">
                    Zaproszenie do wycieczki: <b>{i.trip?.destination}</b> od <b>{i.inviter?.firstName} {i.inviter?.lastName}</b>
                  </div>
                  <div className="invitation-buttons">
                    <button onClick={() => respond(i.invitationId, "ACCEPTED")}>Akceptuj</button>
                    <button onClick={() => respond(i.invitationId, "REJECTED")}>Odrzuć</button>
                  </div>
                </div>
                {idx < arr.length - 1 && <hr className="invitation-divider" />}
              </React.Fragment>
            ))}
        </div>
      )}
    </div>
  );
};

export default InvitationNotifications;