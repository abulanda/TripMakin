import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import "./Dashboard.css";
import AddTripForm from "./AddTripForm";
import InvitationNotifications from "./InvitationNotifications";
import Navbar from "./Navbar";

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

  const handleLogout = () => {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("userId");
    window.location.href = "/";
  };

  if (loading) {
    return <p>Ładowanie danych...</p>;
  }

  return (
    <>
      <Navbar onLogout={handleLogout} />
      <div className="dashboard-panels">
        <div className="dashboard-left">
          <InvitationNotifications onInvitationResponded={fetchTrips} />
        </div>
        <div className="dashboard-right">
          <h2>Twoje wycieczki</h2>
          {trips.length === 0 ? (
            <div className="no-trips">
              <p>Nie masz jeszcze żadnych wycieczek.</p>
            </div>
          ) : (
            <div className="trip-list-vertical">
              {trips.map((trip) => (
                <div key={trip.tripId} className="trip-card">
                  <h3>
                    <Link to={`/trips/${trip.tripId}`}>{trip.destination}</Link>
                  </h3>
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
            </div>
          )}
        </div>
      </div>
      <div className="dashboard-bottom">
        {showAddTripForm ? (
          <AddTripForm onTripAdded={handleTripAdded} />
        ) : (
          <button
            className="create-trip-button-wide"
            onClick={() => setShowAddTripForm(true)}
          >
            Stwórz nową wycieczkę
          </button>
        )}
      </div>
    </>
  );
};

export default Dashboard;