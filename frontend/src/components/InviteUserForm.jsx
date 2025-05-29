import React, { useState } from "react";

const InviteUserForm = ({ tripId, onInvited }) => {
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);

  const handleInvite = (e) => {
    e.preventDefault();
    setLoading(true);
    const token = localStorage.getItem("jwtToken");
    const inviterId = localStorage.getItem("userId");
    fetch("/api/users/email/" + email, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.ok ? res.json() : Promise.reject("Nie znaleziono użytkownika"))
      .then((user) => {
        return fetch("/api/invitations/send", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            tripId: Number(tripId),
            inviterId: Number(inviterId),
            invitedUserId: user.userId,
          }),
        });
      })
      .then((res) => {
        setLoading(false);
        if (!res.ok) throw new Error("Błąd podczas wysyłania zaproszenia");
        setEmail("");
        onInvited && onInvited();
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
        onChange={e => setEmail(e.target.value)}
        placeholder="Email użytkownika"
        required
        disabled={loading}
      />
      <button type="submit" disabled={loading}>
        {loading ? "Wysyłanie..." : "Zaproś"}
      </button>
    </form>
  );
};

export default InviteUserForm;