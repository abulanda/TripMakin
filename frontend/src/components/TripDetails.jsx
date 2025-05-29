import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import AddExpenseForm from "./AddExpenseForm";

const TripDetails = () => {
  const { id } = useParams();
  const [trip, setTrip] = useState(null);
  const [participants, setParticipants] = useState([]);
  const [expenses, setExpenses] = useState([]);
  const [showAddExpense, setShowAddExpense] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("jwtToken");
    fetch(`/api/trips/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => res.json())
      .then((data) => {
        setTrip(data);
        setLoading(false);
      });

    fetch(`/api/trips/${id}/participants`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => res.ok ? res.json() : [])
      .then((data) => setParticipants(data));

    fetch(`/api/expenses/trip/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => res.ok ? res.json() : [])
      .then((data) => setExpenses(data));
  }, [id]);

  const summary = {};
  expenses.forEach(e => {
    const name = `${e.user?.firstName} ${e.user?.lastName}`;
    summary[name] = (summary[name] || 0) + parseFloat(e.amount);
  });

  if (loading || !trip) return <p>Ładowanie...</p>;

  return (
    <div>
      <h2>{trip.destination}</h2>
      <p><strong>Data:</strong> {trip.startDate} - {trip.endDate}</p>
      <p><strong>Status:</strong> {trip.status}</p>
      <p><strong>Opis:</strong> {trip.description}</p>
      <p><strong>Stworzył:</strong> {trip.createdBy?.firstName} {trip.createdBy?.lastName} ({trip.createdBy?.email})</p>
      <h3>Uczestnicy:</h3>
      <ul>
        {participants.length === 0 && <li>Brak uczestników</li>}
        {participants.map((p) => (
          <li key={p.participantId}>
            {p.user?.firstName} {p.user?.lastName} ({p.user?.email}) - {p.role}
          </li>
        ))}
      </ul>
      <h3>Wydatki:</h3>
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
          const token = localStorage.getItem("jwtToken");
          fetch(`/api/expenses/trip/${id}`, {
            headers: { Authorization: `Bearer ${token}` },
          })
            .then((res) => res.ok ? res.json() : [])
            .then((data) => setExpenses(data));
        }} participants={participants} />
      )}
      <Link to="/">⬅ Powrót</Link>
    </div>
  );
};

export default TripDetails;