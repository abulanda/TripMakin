import React, { useEffect, useState } from "react";
import Navbar from "./Navbar";
import { useNavigate } from "react-router-dom";
import "./AdminPanel.css";
import { authFetch } from "../utils/authFetch";


const AdminPanel = ({ onLogout }) => {
  const [users, setUsers] = useState([]);
  const [trips, setTrips] = useState([]);
  const [loadingUsers, setLoadingUsers] = useState(true);
  const [loadingTrips, setLoadingTrips] = useState(true);
  const [expandedUsers, setExpandedUsers] = useState(false);
  const [expandedTrips, setExpandedTrips] = useState(false);

  const [tripPage, setTripPage] = useState(0);
  const [tripSize, setTripSize] = useState(10);
  const [tripSort, setTripSort] = useState("startDate,desc");
  const [tripStatus, setTripStatus] = useState("");
  const [tripTotalPages, setTripTotalPages] = useState(1);
  const [userPage, setUserPage] = useState(0);
  const [userSize, setUserSize] = useState(10);
  const [userSort, setUserSort] = useState("userId,asc");
  const [userTotalPages, setUserTotalPages] = useState(1);

  const navigate = useNavigate();

  const fetchUsers = () => {
    setLoadingUsers(true);
    let url = `/api/v1/users?page=${userPage}&size=${userSize}&sort=${userSort}`;
    authFetch(url, { credentials: "include" })
      .then(res => {
        if (!res.ok) throw new Error("Błąd pobierania użytkowników");
        return res.json();
      })
      .then(data => {
        setUsers(data.content);
        setUserTotalPages(data.totalPages);
        setLoadingUsers(false);
      })
      .catch(() => {
        setUsers([]);
        setLoadingUsers(false);
      });
  };

  const fetchTrips = () => {
    setLoadingTrips(true);
    let url = `/api/v1/trips?page=${tripPage}&size=${tripSize}&sort=${tripSort}`;
    if (tripStatus) url += `&status=${tripStatus}`;
    authFetch(url, {
      credentials: "include"
    })
      .then(res => {
        if (!res.ok) throw new Error("Błąd pobierania wycieczek");
        return res.json();
      })
      .then(data => {
        setTrips(data.content);
        setTripTotalPages(data.totalPages);
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
  }, [userPage, userSize, userSort]);

  useEffect(() => {
    fetchTrips();
  }, [tripPage, tripSize, tripSort, tripStatus]);

  return (
    <>
      <Navbar onLogout={onLogout} />
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
              {}
              <div style={{ margin: "12px 0" }}>
                <label>Sortuj po: </label>
                <select value={userSort} onChange={e => setUserSort(e.target.value)}>
                  <option value="userId,asc">ID rosnąco</option>
                  <option value="userId,desc">ID malejąco</option>
                  <option value="firstName,asc">Imię A-Z</option>
                  <option value="lastName,asc">Nazwisko A-Z</option>
                  <option value="email,asc">Email A-Z</option>
                </select>
                <label style={{ marginLeft: 16 }}>Na stronę: </label>
                <select value={userSize} onChange={e => setUserSize(Number(e.target.value))}>
                  <option value={5}>5</option>
                  <option value={10}>10</option>
                  <option value={20}>20</option>
                </select>
              </div>
              {loadingUsers ? (
                <div className="admin-users-loading">Ładowanie użytkowników...</div>
              ) : users.length === 0 ? (
                <div className="admin-users-empty">Brak użytkowników w systemie.</div>
              ) : (
                <>
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
                  <div style={{ marginTop: 8 }}>
                    <button
                      onClick={() => setUserPage(p => Math.max(0, p - 1))}
                      disabled={userPage === 0}
                    >
                      Poprzednia
                    </button>
                    <span> Strona {userPage + 1} z {userTotalPages} </span>
                    <button
                      onClick={() => setUserPage(p => Math.min(userTotalPages - 1, p + 1))}
                      disabled={userPage >= userTotalPages - 1}
                    >
                      Następna
                    </button>
                  </div>
                </>
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
              {}
              <div style={{ margin: "12px 0" }}>
                <label>Status: </label>
                <select value={tripStatus} onChange={e => setTripStatus(e.target.value)}>
                  <option value="">Wszystkie</option>
                  <option value="PLANNED">PLANNED</option>
                  <option value="IN_PROGRESS">IN_PROGRESS</option>
                  <option value="FINISHED">FINISHED</option>
                </select>
                <label style={{ marginLeft: 16 }}>Sortuj po: </label>
                <select value={tripSort} onChange={e => setTripSort(e.target.value)}>
                  <option value="startDate,desc">Data rozpoczęcia ↓</option>
                  <option value="startDate,asc">Data rozpoczęcia ↑</option>
                  <option value="destination,asc">Cel podróży A-Z</option>
                  <option value="tripId,asc">ID rosnąco</option>
                  <option value="tripId,desc">ID malejąco</option>
                </select>
                <label style={{ marginLeft: 16 }}>Na stronę: </label>
                <select value={tripSize} onChange={e => setTripSize(Number(e.target.value))}>
                  <option value={5}>5</option>
                  <option value={10}>10</option>
                  <option value={20}>20</option>
                </select>
              </div>
              {loadingTrips ? (
                <div className="admin-users-loading">Ładowanie wycieczek...</div>
              ) : trips.length === 0 ? (
                <div className="admin-users-empty">Brak wycieczek w systemie.</div>
              ) : (
                <>
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
                  <div style={{ marginTop: 8 }}>
                    <button
                      onClick={() => setTripPage(p => Math.max(0, p - 1))}
                      disabled={tripPage === 0}
                    >
                      Poprzednia
                    </button>
                    <span> Strona {tripPage + 1} z {tripTotalPages} </span>
                    <button
                      onClick={() => setTripPage(p => Math.min(tripTotalPages - 1, p + 1))}
                      disabled={tripPage >= tripTotalPages - 1}
                    >
                      Następna
                    </button>
                  </div>
                </>
              )}
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default AdminPanel;