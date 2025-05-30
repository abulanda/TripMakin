import React from "react";
import "./Navbar.css";

const Navbar = ({ onLogout }) => (
  <nav className="navbar">
    <div className="navbar-left">
      <img src="/logo.svg" alt="TripMakin logo" className="navbar-logo" />
      <span className="navbar-title">TripMakin</span>
    </div>
    <div className="navbar-center">
      {/* to do */}
    </div>
    <div className="navbar-right">
      <button className="logout-button" onClick={onLogout}>Wyloguj</button>
    </div>
  </nav>
);

export default Navbar;