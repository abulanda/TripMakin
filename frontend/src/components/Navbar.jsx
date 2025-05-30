import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Navbar.css";

const Navbar = ({ onLogout }) => {
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();
  const userId = localStorage.getItem("userId");
  const payload = JSON.parse(localStorage.getItem("payload"));

  const handleUserPanel = () => {
    setOpen(false);
    navigate(`/users/${userId}`);
  };

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <img src="/logo.svg" alt="TripMakin logo" className="navbar-logo" />
        <span className="navbar-title">TripMakin</span>
      </div>
      <div className="navbar-center">{}</div>
      <div className="navbar-right" style={{ position: "relative" }}>
        <button
          className="user-menu-button"
          onClick={() => setOpen((v) => !v)}
          aria-label="Menu użytkownika"
        >
          {}
          <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
            <rect y="6" width="28" height="3" rx="1.5" fill="#3a5b3c" />
            <rect y="13" width="28" height="3" rx="1.5" fill="#3a5b3c" />
            <rect y="20" width="28" height="3" rx="1.5" fill="#3a5b3c" />
          </svg>
        </button>
        {open && (
          <div className="user-dropdown">
            <button className="dropdown-item" onClick={handleUserPanel}>
              Panel użytkownika
            </button>
            {payload?.roles?.includes("ROLE_ADMIN") && (
              <button
                className="dropdown-item"
                onClick={() => (window.location.href = "/admin")}
              >
                Panel administratora
              </button>
            )}
            <hr className="dropdown-divider" />
            <button className="dropdown-item" onClick={onLogout}>
              Wyloguj
            </button>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;