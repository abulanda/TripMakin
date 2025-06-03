import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Navbar from "./Navbar";
import { authFetch } from "../utils/authFetch";

const UserPanel = ({ onLogout }) => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [edit, setEdit] = useState(false);
  const [form, setForm] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    authFetch(`/api/v1/users/${id}`, {
      credentials: "include",
    })
      .then(res => {
        if (!res.ok) throw new Error("Brak dostępu lub błąd pobierania danych użytkownika");
        return res.json();
      })
      .then(data => {
        setUser(data);
        setForm(data);
        setLoading(false);
      })
      .catch(err => {
        setLoading(false);
        alert(err.message);
      });
  }, [id]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleSave = (e) => {
    e.preventDefault();
    const userPayload = { ...form };
    delete userPayload.createdAt;
    delete userPayload.lastLoginAt;
    delete userPayload.roles;
    delete userPayload.isActive;
    delete userPayload.userId;
    delete userPayload.profilePicture;
    if (
      !form.password ||
      form.password === user.password ||
      (form.password && form.password.startsWith("$2a$"))
    ) {
      delete userPayload.password;
    }

    authFetch(`/api/v1/users/${id}`, {
      method: "PUT",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userPayload),
    })
      .then(res => {
        if (!res.ok) throw new Error("Błąd podczas zapisu");
        return res.json();
      })
      .then(data => {
        setUser(data);
        setEdit(false);
        alert("Dane zapisane!");
      })
      .catch(err => alert(err.message));
  };

  const handleLogout = () => {
    localStorage.removeItem("userId");
    window.location.href = "/";
  };

  if (loading || !user) return (
    <>
      <Navbar onLogout={onLogout} />
      <p>Ładowanie...</p>
    </>
  );

  return (
    <>
      <Navbar onLogout={onLogout} />
      <div style={{ display: 'flex', justifyContent: 'flex-start', margin: '90px 0 1.5rem 2.5rem' }}>
        <button
          style={{
            background: "#7fa88c",
            color: "#fff",
            border: "none",
            borderRadius: "8px",
            padding: "0.5rem 1.2rem",
            cursor: "pointer",
            fontSize: "1rem"
          }}
          onClick={() => navigate("/")}
        >
          ⬅ Cofnij
        </button>
      </div>
      <div className="user-panel">
        <h2>Panel użytkownika</h2>
        {!edit ? (
          <div>
            <p><b>Imię:</b> {user.firstName}</p>
            <p><b>Nazwisko:</b> {user.lastName}</p>
            <p><b>Email:</b> {user.email}</p>
            <p><b>Telefon:</b> {user.phoneNumber || "-"}</p>
            <p><b>Bio:</b> {user.bio || "-"}</p>
            <button onClick={() => setEdit(true)}>Edytuj dane</button>
          </div>
        ) : (
          <form onSubmit={handleSave}>
            <input
              name="firstName"
              value={form.firstName || ""}
              onChange={handleChange}
              placeholder="Imię"
              required
            />
            <input
              name="lastName"
              value={form.lastName || ""}
              onChange={handleChange}
              placeholder="Nazwisko"
              required
            />
            <input
              name="email"
              value={form.email || ""}
              onChange={handleChange}
              placeholder="Email"
              required
            />
            <input
              name="nickname"
              value={form.nickname || ""}
              onChange={handleChange}
              placeholder="Nickname"
            />
            <input
              name="phoneNumber"
              value={form.phoneNumber || ""}
              onChange={handleChange}
              placeholder="Telefon"
            />
            <textarea
              name="bio"
              value={form.bio || ""}
              onChange={handleChange}
              placeholder="Bio"
            />
            <button type="submit">Zapisz</button>
            <button type="button" onClick={() => setEdit(false)}>Anuluj</button>
          </form>
        )}
      </div>
    </>
  );
};

export default UserPanel;