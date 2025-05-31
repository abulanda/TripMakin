import React, { useEffect, useState } from "react";
import { authFetch } from "../utils/authFetch";

const InviteUserForm = ({ tripId, onUserInvited }) => {
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);
  const [participants, setParticipants] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    authFetch(`/api/v1/trips/${tripId}/participants`, {
      credentials: "include",
    })
      .then((res) => (res.ok ? res.json() : []))
      .then((data) => setParticipants(data));
  }, [tripId]);

  const handleInvite = (e) => {
    e.preventDefault();
    setError("");
    const alreadyParticipant = participants.some(
      (p) => p.user?.email?.toLowerCase() === email.toLowerCase()
    );
    if (alreadyParticipant) {
      setError("Użytkownik już uczestniczy w tej wycieczce.");
      return;
    }
    setLoading(true);

    const inviterId = Number(localStorage.getItem("userId"));

    authFetch("/api/v1/users/email/" + email, {
      credentials: "include",
    })
      .then((res) =>
        res.ok ? res.json() : Promise.reject("Nie znaleziono użytkownika")
      )
      .then((user) => {
        return authFetch("/api/v1/invitations/send", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
          body: JSON.stringify({
            tripId: Number(tripId),
            inviterId,
            invitedUserId: user.userId,
          }),
        });
      })
      .then((res) => {
        setLoading(false);
        if (!res.ok) throw new Error("Błąd podczas wysyłania zaproszenia");
        setEmail("");
        onUserInvited && onUserInvited();
        alert("Zaproszenie wysłane!");
      })
      .catch((err) => {
        setLoading(false);
        alert(err);
      });
  };

  return (
    <form onSubmit={handleInvite} style={{ margin: "1em 0" }}>
      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Email użytkownika"
        required
        disabled={loading}
      />
      <button type="submit" disabled={loading}>
        {loading ? "Wysyłanie..." : "Zaproś"}
      </button>
      {error && (
        <div style={{ color: "red", marginTop: "0.5em" }}>{error}</div>
      )}
    </form>
  );
};

export default InviteUserForm;