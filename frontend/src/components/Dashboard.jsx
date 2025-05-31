import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import "./Dashboard.css";
import AddTripForm from "./AddTripForm";
import InvitationNotifications from "./InvitationNotifications";
import Navbar from "./Navbar";
import NewsPanel from "./NewsPanel";

const Dashboard = ({ payload, onLogout }) => {
  const [trips, setTrips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAddTripForm, setShowAddTripForm] = useState(false);

  const fetchTrips = () => {
    fetch("http://localhost:8081/api/trips", {
      credentials: "include"
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
    <>
      <Navbar onLogout={onLogout} payload={payload} />
      <div className="dashboard-panels">
        <div className="dashboard-left" style={{ flex: "1 1 25%" }}>
          <InvitationNotifications onInvitationResponded={fetchTrips} />
        </div>
        <div className="dashboard-center" style={{ flex: "1 1 50%" }}>
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
        <div className="dashboard-news" style={{ flex: "1 1 25%" }}>
          <NewsPanel trips={trips} />
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