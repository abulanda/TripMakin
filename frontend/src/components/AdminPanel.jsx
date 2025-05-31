import React, { useEffect, useState } from "react";
import Navbar from "./Navbar";
import { useNavigate } from "react-router-dom";
import "./AdminPanel.css";
import { authFetch } from "../utils/authFetch";


const AdminPanel = () => {
  const [users, setUsers] = useState([]);
  const [trips, setTrips] = useState([]);
  const [loadingUsers, setLoadingUsers] = useState(true);
  const [loadingTrips, setLoadingTrips] = useState(true);
  const [expandedUsers, setExpandedUsers] = useState(false);
  const [expandedTrips, setExpandedTrips] = useState(false);
  const navigate = useNavigate();

  const fetchUsers = () => {
    setLoadingUsers(true);
    authFetch("/api/v1/users", {
      credentials: "include"
    })
      .then(res => {
        if (!res.ok) throw new Error("Błąd pobierania użytkowników");
        return res.json();
      })
      .then(data => {
        setUsers(data);
        setLoadingUsers(false);
      })
      .catch(() => {
        setUsers([]);
        setLoadingUsers(false);
      });
  };

  const fetchTrips = () => {
    setLoadingTrips(true);
    authFetch("/api/v1/trips/all", {
      credentials: "include"
    })
      .then(res => {
        if (!res.ok) throw new Error("Błąd pobierania wycieczek");
        return res.json();
      })
      .then(data => {
        setTrips(data);
        setLoadingTrips(false);
      })
      .catch(() => {
        setTrips([]);
        setLoadingTrips(false);
      });
  };

  const handleDeleteUser = (id) => {
    if (!window.confirm("Na pewno usunąć użytkownika?")) return;
    authFetch(`/api/v1/users/${id}`, {
      method: "DELETE",
      credentials: "include"
    })
      .then(res => {
        if (res.ok) fetchUsers();
        else alert("Błąd usuwania użytkownika");
      });
  };

  const handleDeleteTrip = (id) => {
    if (!window.confirm("Na pewno usunąć wycieczkę?")) return;
    authFetch(`/api/v1/trips/${id}`, {
      method: "DELETE",
      credentials: "include"
    })
      .then(res => {
        if (res.ok) fetchTrips();
        else alert("Błąd usuwania wycieczki");
      });
  };

  useEffect(() => {
    fetchUsers();
    fetchTrips();
  }, []);

  return (
    <>
      <Navbar onLogout={() => {
        localStorage.removeItem("userId");
        localStorage.removeItem("payload");
        window.location.href = "/";
      }} />
      <div className="admin-panel-container">
        <button
          onClick={() => navigate("/")}
          className="admin-back-btn"
        >
          ⬅ Cofnij
        </button>
        <h2 className="admin-panel-title">Panel administratora</h2>

        {}
        <div className="admin-users-box">
          <div
            className="admin-users-header"
            onClick={() => setExpandedUsers(e => !e)}
          >
            {expandedUsers ? "▼" : "►"} Lista użytkowników ({users.length})
          </div>
          {expandedUsers && (
            <div>
              {loadingUsers ? (
                <div className="admin-users-loading">Ładowanie użytkowników...</div>
              ) : users.length === 0 ? (
                <div className="admin-users-empty">Brak użytkowników w systemie.</div>
              ) : (
                <table className="admin-users-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Imię</th>
                      <th>Nazwisko</th>
                      <th>Email</th>
                      <th>Akcje</th>
                    </tr>
                  </thead>
                  <tbody>
                    {users.map(u => (
                      <tr key={u.userId}>
                        <td>{u.userId}</td>
                        <td>{u.firstName}</td>
                        <td>{u.lastName}</td>
                        <td>{u.email}</td>
                        <td>
                          <button
                            onClick={() => handleDeleteUser(u.userId)}
                            className="admin-delete-btn"
                          >
                            Usuń
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          )}
        </div>

        {}
        <div className="admin-users-box" style={{ marginTop: 32 }}>
          <div
            className="admin-users-header"
            onClick={() => setExpandedTrips(e => !e)}
          >
            {expandedTrips ? "▼" : "►"} Lista wycieczek ({trips.length})
          </div>
          {expandedTrips && (
            <div>
              {loadingTrips ? (
                <div className="admin-users-loading">Ładowanie wycieczek...</div>
              ) : trips.length === 0 ? (
                <div className="admin-users-empty">Brak wycieczek w systemie.</div>
              ) : (
                <table className="admin-users-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Cel podróży</th>
                      <th>Data</th>
                      <th>Status</th>
                      <th>Akcje</th>
                    </tr>
                  </thead>
                  <tbody>
                    {trips.map(t => (
                      <tr key={t.tripId}>
                        <td>{t.tripId}</td>
                        <td>{t.destination}</td>
                        <td>{t.startDate} - {t.endDate}</td>
                        <td>{t.status}</td>
                        <td>
                          <button
                            onClick={() => handleDeleteTrip(t.tripId)}
                            className="admin-delete-btn"
                          >
                            Usuń
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default AdminPanel;