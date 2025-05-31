import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./LoginPage.css";
import { authFetch } from "../utils/authFetch";

const LoginPage = ({ onLogin, onSwitchToRegister }) => {
  const [formData, setFormData] = useState({ username: "", password: "" });
  const navigate = useNavigate();

  const params = new URLSearchParams(window.location.search);
  const sessionExpired = params.get("sessionExpired");

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    authFetch("/api/v1/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(formData),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Błąd logowania");
        return res.json();
      })
      .then((data) => {
        localStorage.setItem("userId", data.userId);
        onLogin();
        navigate("/");
      })
      .catch((err) => alert(err.message));
  };

  return (
    <div className="login-page">
      <div className="center">
        <img src="/logo.svg" alt="Logo" className="logo" />
        <div className="login-container">
          <h1>Let's begin your journey!</h1>
          {sessionExpired && (
            <div className="session-expired-msg">
              Sesja wygasła. Zaloguj się ponownie.
            </div>
          )}
          <form onSubmit={handleSubmit}>
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              placeholder="Email"
              required
            />
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Password"
              required
            />
            <div className="button-group">
              <button
                type="button"
                className="secondary-button"
                onClick={onSwitchToRegister}
              >
                Sign Up
              </button>
              <button type="submit" className="primary-button">
                Log In
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;