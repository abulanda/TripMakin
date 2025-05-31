import React, { useState, useEffect } from "react";
import { Routes, Route } from "react-router-dom";
import LoginPage from "./components/LoginPage";
import RegisterPage from "./components/RegisterPage";
import Dashboard from "./components/Dashboard";
import UserPanel from "./components/UserPanel";
import TripDetails from "./components/TripDetails";
import AdminPanel from "./components/AdminPanel";
import RequireAdmin from "./components/RequireAdmin";
import Navbar from "./components/Navbar";
import "./App.css";

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isRegistering, setIsRegistering] = useState(false);
  const [loading, setLoading] = useState(true);
  const [payload, setPayload] = useState(null);

  useEffect(() => {
    fetch("/api/auth/me", { credentials: "include" })
      .then((res) => (res.ok ? res.json() : null))
      .then((data) => {
        if (data && data.roles) {
          setPayload(data);
          localStorage.setItem("payload", JSON.stringify(data));
          setIsLoggedIn(true);
        } else {
          setPayload(null);
          localStorage.removeItem("payload");
          setIsLoggedIn(false);
        }
      })
      .finally(() => setLoading(false));
  }, []);

  const handleLogin = () => {
    fetch("/api/auth/me", { credentials: "include" })
      .then((res) => (res.ok ? res.json() : null))
      .then((data) => {
        if (data && data.roles) {
          setPayload(data);
          localStorage.setItem("payload", JSON.stringify(data));
          setIsLoggedIn(true);
        } else {
          setPayload(null);
          localStorage.removeItem("payload");
          setIsLoggedIn(false);
        }
      });
  };

  const handleLogout = () => {
    fetch("/api/auth/logout", { method: "POST", credentials: "include" })
      .then(() => {
        setIsLoggedIn(false);
      });
    localStorage.removeItem("userId");
  };

  const handleRegister = () => {
    setIsRegistering(false);
  };

  if (loading) return <div>Ładowanie...</div>;

  if (!isLoggedIn) {
    return isRegistering ? (
      <RegisterPage onRegister={handleRegister} />
    ) : (
      <LoginPage
        onLogin={handleLogin}
        onSwitchToRegister={() => setIsRegistering(true)}
      />
    );
  }

  return (
    <div className="App">
      <Navbar onLogout={handleLogout} payload={payload} />
      <Routes>
        <Route
          path="/admin"
          element={
            <RequireAdmin payload={payload}>
              <AdminPanel />
            </RequireAdmin>
          }
        />
        <Route path="/users/:id" element={<UserPanel />} />
        <Route path="/trips/:id" element={<TripDetails />} />
        <Route
          path="*"
          element={<Dashboard payload={payload} onLogout={handleLogout} />}
        />
      </Routes>
    </div>
  );
}

export default App;
