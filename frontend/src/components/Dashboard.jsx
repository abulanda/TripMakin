import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import "./Dashboard.css";
import AddTripForm from "./AddTripForm";
import InvitationNotifications from "./InvitationNotifications";

const Dashboard = () => {
  const [trips, setTrips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAddTripForm, setShowAddTripForm] = useState(false);

  const fetchTrips = () => {
    const token = localStorage.getItem("jwtToken");
    fetch("http://localhost:8081/api/trips", {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => {
        if (!res.ok) {
          throw new Error("Błąd podczas pobierania wycieczek");
        }
        return res.json();
      })
      .then((data) => {
        setTrips(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Błąd podczas pobierania wycieczek:", err);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchTrips();
  }, []);

  const handleTripAdded = () => {
    setShowAddTripForm(false);
    fetchTrips();
  };

  if (loading) {
    return <p>Ładowanie danych...</p>;
  }

  return (
    <div className="dashboard">
      <InvitationNotifications onInvitationResponded={fetchTrips} />
      <h1>Twoje wycieczki</h1>
      {showAddTripForm ? (
        <AddTripForm onTripAdded={handleTripAdded} />
      ) : (
        <>
          {trips.length === 0 ? (
            <div className="no-trips">
              <p>Nie masz jeszcze żadnych wycieczek.</p>
              <button
                className="create-trip-button"
                onClick={() => setShowAddTripForm(true)}
              >
                Stwórz nową wycieczkę
              </button>
            </div>
          ) : (
            <div className="trip-list">
              {trips.map((trip) => (
                <div key={trip.tripId} className="trip-card">
                  <h2>
                    <Link to={`/trips/${trip.tripId}`}>{trip.destination}</Link>
                  </h2>
                  <p>
                    <strong>Data:</strong> {trip.startDate} - {trip.endDate}
                  </p>
                  <p>
                    <strong>Status:</strong> {trip.status}
                  </p>
                  <Link className="details-link" to={`/trips/${trip.tripId}`}>
                    Szczegóły
                  </Link>
                </div>
              ))}
              <button
                className="create-trip-button"
                onClick={() => setShowAddTripForm(true)}
              >
                Stwórz nową wycieczkę
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default Dashboard;