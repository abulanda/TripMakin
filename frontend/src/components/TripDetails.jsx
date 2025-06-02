import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import AddExpenseForm from "./AddExpenseForm";
import ScheduleList from "./ScheduleList";
import InviteUserForm from "./InviteUserForm";
import Navbar from "./Navbar";
import { authFetch } from "../utils/authFetch";
import "./TripDetails.css";

const TripDetails = ({ onLogout }) => {
  const { id } = useParams();
  const [trip, setTrip] = useState(null);
  const [participants, setParticipants] = useState([]);
  const [expenses, setExpenses] = useState([]);
  const [showAddExpense, setShowAddExpense] = useState(false);
  const [showInviteUser, setShowInviteUser] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    authFetch(`/api/v1/trips/${id}`, {
      credentials: "include",
    })
      .then((res) => res.json())
      .then((data) => {
        setTrip(data);
        setLoading(false);
      });

    authFetch(`/api/v1/trips/${id}/participants`, {
      credentials: "include",
    })
      .then((res) => res.ok ? res.json() : [])
      .then((data) => setParticipants(data));

    authFetch(`/api/v1/expenses/trip/${id}`, {
      credentials: "include",
    })
      .then((res) => res.ok ? res.json() : [])
      .then((data) => setExpenses(data));
  }, [id]);

  const summary = {};
  expenses.forEach(e => {
    const name = `${e.user?.firstName} ${e.user?.lastName}`;
    summary[name] = (summary[name] || 0) + parseFloat(e.amount);
  });

  const myParticipant = participants.find(
    (p) => p.user?.userId === Number(localStorage.getItem("userId"))
  );

  const handleLogout = () => {
    localStorage.removeItem("userId");
    window.location.href = "/";
  };

  if (loading || !trip) return <p>Ładowanie...</p>;

  return (
    <>
      <Navbar onLogout={onLogout} />
      <div className="trip-details-page">
        <div className="trip-details-back">
          <Link to="/">⬅ Powrót</Link>
        </div>
        <div className="trip-details-top">
          <div className="trip-info-card">
            <h2>{trip.destination}</h2>
            <p><strong>Data:</strong> {trip.startDate} - {trip.endDate}</p>
            <p><strong>Status:</strong> {trip.status}</p>
            <p><strong>Opis:</strong> {trip.description}</p>
            <p><strong>Stworzył:</strong> {trip.createdBy?.firstName} {trip.createdBy?.lastName} ({trip.createdBy?.email})</p>
          </div>
          <div className="trip-participants-card">
            <h3>Uczestnicy</h3>
            <div className="participants-list">
              {participants.length === 0 && <div>Brak uczestników</div>}
              {participants.map((p, idx) => (
                <React.Fragment key={p.participantId}>
                  <div className="participant-item">
                    {p.user?.firstName} {p.user?.lastName} ({p.user?.email}) - {p.role}
                  </div>
                  {idx < participants.length - 1 && <hr className="participant-divider" />}
                </React.Fragment>
              ))}
            </div>
            {myParticipant && myParticipant.role === "OWNER" && (
              <>
                <button onClick={() => setShowInviteUser(!showInviteUser)}>
                  {showInviteUser ? "Anuluj zaproszenie" : "Zaproś użytkownika"}
                </button>
                {showInviteUser && (
                  <InviteUserForm tripId={id} onUserInvited={() => {
                    setShowInviteUser(false);
                    authFetch(`/api/v1/trips/${id}/participants`, {
                      credentials: "include",
                    })
                      .then((res) => res.ok ? res.json() : [])
                      .then((data) => setParticipants(data));
                  }} />
                )}
              </>
            )}
            {myParticipant && myParticipant.role !== "OWNER" && (
              <button
                onClick={() => {
                  authFetch(
                    `/api/v1/trips/${id}/participants/${myParticipant.user.userId}`,
                    {
                      method: "DELETE",
                      credentials: "include",
                    }
                  )
                    .then((res) => {
                      if (!res.ok) throw new Error("Błąd podczas opuszczania wycieczki");
                      window.location.href = "/";
                    })
                    .catch((err) => alert(err.message));
                }}
              >
                Opuść wycieczkę
              </button>
            )}
          </div>
        </div>
        <div className="trip-details-bottom">
          <div className="trip-expenses-card">
            <h3>Wydatki</h3>
            {expenses.length === 0 ? (
              <p>Brak wydatków</p>
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>Opis</th>
                    <th>Kategoria</th>
                    <th>Kwota</th>
                    <th>Waluta</th>
                    <th>Data</th>
                    <th>Kto zapłacił</th>
                  </tr>
                </thead>
                <tbody>
                  {expenses.map((e) => (
                    <tr key={e.expenseId}>
                      <td>{e.description}</td>
                      <td>{e.category}</td>
                      <td>{e.amount}</td>
                      <td>{e.currency}</td>
                      <td>{e.date}</td>
                      <td>{e.user?.firstName} {e.user?.lastName}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
            <h4>Podsumowanie wydatków:</h4>
            <ul>
              {Object.entries(summary).map(([name, sum]) => (
                <li key={name}>{name}: {sum.toFixed(2)} PLN</li>
              ))}
            </ul>
            <button onClick={() => setShowAddExpense(!showAddExpense)}>
              {showAddExpense ? "Anuluj" : "Dodaj wydatek"}
            </button>
            {showAddExpense && (
              <AddExpenseForm tripId={id} onExpenseAdded={() => {
                setShowAddExpense(false);
                authFetch(`/api/v1/expenses/trip/${id}`, {
                  credentials: "include",
                })
                  .then((res) => res.ok ? res.json() : [])
                  .then((data) => setExpenses(data));
              }} participants={participants} />
            )}
          </div>
          <div className="trip-schedule-card">
            <ScheduleList tripId={id} />
          </div>
        </div>
      </div>
    </>
  );
};

export default TripDetails;